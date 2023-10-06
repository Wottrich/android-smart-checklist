package wottrich.github.io.smartchecklist.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import org.koin.core.context.GlobalContext
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders

abstract class KotlinResultUseCase<Params, ReturnType>(
    private val appDispatchersProviders: DispatchersProviders = GlobalContext.get().get()
) : UseCase<Params, Result<ReturnType>> where ReturnType : Any? {

    @Suppress("UNCHECKED_CAST")
    override suspend operator fun invoke(params: Params): Result<ReturnType> {
        return invokeImpl(params)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend operator fun invoke(): Result<ReturnType> {
        return invokeImpl(UseCase.None() as Params)
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