package wottrich.github.io.smartchecklist.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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

    override suspend fun invoke(): Flow<Result<ReturnType>> {
        return invokeImpl(UseCase.None() as Params)
    }

    private suspend fun invokeImpl(params: Params): Flow<Result<ReturnType>> {
        return appendRequestInAsyncThread {
            flow {
                val flowCollector = this
                execute(params).flowOn(appDispatchersProviders.main).collect { result ->
                    flowCollector.emit(result)
                }
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