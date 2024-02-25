package wottrich.github.io.smartchecklist.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext
import org.koin.core.context.GlobalContext
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders

abstract class FlowableUseCase<Params, ReturnType>(
    private val appDispatchersProviders: DispatchersProviders = GlobalContext.get().get()
) : UseCase<Params, Flow<Result<ReturnType>>> {
    override suspend fun invoke(params: Params): Flow<Result<ReturnType>> {
        return invokeImpl(params)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun invoke(): Flow<Result<ReturnType>> {
        return invokeImpl(UseCase.None() as Params)
    }

    open fun mapError(throwable: Throwable): Result<ReturnType> {
        return Result.failure(throwable)
    }

    private suspend fun invokeImpl(params: Params): Flow<Result<ReturnType>> {
        return appendRequestInAsyncThread {
            execute(params).buffer(onBufferOverflow = BufferOverflow.DROP_OLDEST)
                .catch {
                    emit(mapError(it))
                }
        }
    }

    private suspend fun appendRequestInAsyncThread(block: suspend CoroutineScope.() -> Flow<Result<ReturnType>>): Flow<Result<ReturnType>> {
        return withContext(appDispatchersProviders.io) {
            block.invoke(this)
        }
    }

    private suspend fun postResultInSaveThread(block: suspend CoroutineScope.() -> Unit) {
        return withContext(appDispatchersProviders.main) {
            block.invoke(this)
        }
    }
}