package wottrich.github.io.smartchecklist.coroutines

interface UseCase<Params, ReturnType> where ReturnType : Any? {
    suspend fun execute(params: Params): ReturnType
    suspend operator fun invoke(params: Params): ReturnType
    suspend operator fun invoke(): ReturnType
    class None
    class Empty
}