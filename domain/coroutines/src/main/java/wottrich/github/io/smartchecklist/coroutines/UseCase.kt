package wottrich.github.io.smartchecklist.coroutines

import wottrich.github.io.smartchecklist.coroutines.UseCase.Empty
import wottrich.github.io.smartchecklist.coroutines.UseCase.None
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.koin.core.context.GlobalContext

interface UseCase<Params, ReturnType> where ReturnType : Any? {
    suspend fun execute(params: Params): ReturnType
    suspend operator fun invoke(params: Params): ReturnType
    suspend operator fun invoke(): ReturnType
    class None
    class Empty
}

abstract class FlowableUseCase<Params, ReturnType>(
    private val appDispatchersProviders: DispatchersProviders = GlobalContext.get().get()
) : UseCase<Params, Flow<Result<ReturnType>>> {
    override suspend fun invoke(params: Params): Flow<Result<ReturnType>> {
        return invokeImpl(params)
    }

    override suspend fun invoke(): Flow<Result<ReturnType>> {
        return invokeImpl(None() as Params)
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

abstract class KotlinResultUseCase<Params, ReturnType>(
    private val appDispatchersProviders: DispatchersProviders = GlobalContext.get().get()
) : UseCase<Params, Result<ReturnType>> where ReturnType : Any? {

    @Suppress("UNCHECKED_CAST")
    override suspend operator fun invoke(params: Params): Result<ReturnType> {
        return invokeImpl(params)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend operator fun invoke(): Result<ReturnType> {
        return invokeImpl(None() as Params)
    }

    private suspend fun invokeImpl(params: Params): Result<ReturnType> {
        return appendRequestInAsyncThread {
            try {
                if (params == null) { throw Exception("params is requested") }
                val result = execute(params)
                postResultInSaveThread {
                    result
                }
            } catch (ex: Exception) {
                postResultInSaveThread {
                    Result.failure(ex)
                }
            }
        }
    }

    private suspend fun appendRequestInAsyncThread(block: suspend CoroutineScope.() -> Result<ReturnType>): Result<ReturnType> {
        return withContext(appDispatchersProviders.io) {
            block.invoke(this)
        }
    }

    private suspend fun postResultInSaveThread(block: suspend CoroutineScope.() -> Result<ReturnType>): Result<ReturnType> {
        return withContext(appDispatchersProviders.main) {
            block.invoke(this)
        }
    }
}

fun successEmptyResult(): Result<Empty> = Result.success(Empty())
fun failureEmptyResult(exception: Exception): Result<Empty> = Result.failure(exception = exception)