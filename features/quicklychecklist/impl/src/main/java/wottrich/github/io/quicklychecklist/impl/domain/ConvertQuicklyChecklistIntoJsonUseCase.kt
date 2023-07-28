package wottrich.github.io.quicklychecklist.impl.domain

import com.google.gson.Gson
import github.io.wottrich.coroutines.KotlinResultUseCase
import github.io.wottrich.coroutines.base.Result
import wottrich.github.io.smartchecklist.datasource.entity.QuicklyChecklist

class ConvertQuicklyChecklistIntoJsonUseCase : KotlinResultUseCase<QuicklyChecklist, String>() {
    override suspend fun execute(params: QuicklyChecklist): Result<String> {
        return try {
            Result.success(Gson().toJson(params))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}