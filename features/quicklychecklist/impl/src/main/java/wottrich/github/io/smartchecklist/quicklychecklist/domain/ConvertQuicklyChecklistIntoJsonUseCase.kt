package wottrich.github.io.smartchecklist.quicklychecklist.domain

import com.google.gson.Gson
import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.datasource.data.model.QuicklyChecklist

class ConvertQuicklyChecklistIntoJsonUseCase : KotlinResultUseCase<QuicklyChecklist, String>() {
    override suspend fun execute(params: QuicklyChecklist): Result<String> {
        return try {
            Result.success(Gson().toJson(params))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}