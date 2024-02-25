package wottrich.github.io.smartchecklist.coroutines

import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result

fun successEmptyResult(): Result<UseCase.Empty> = Result.success(UseCase.Empty())
fun failureEmptyResult(exception: Exception): Result<UseCase.Empty> = Result.failure(exception = exception)