package wottrich.github.io.quicklychecklist.impl.domain

import com.google.gson.Gson
import wottrich.github.io.datasource.entity.QuicklyChecklist
import wottrich.github.io.tools.base.KotlinResultUseCase
import wottrich.github.io.tools.base.Result

class ConvertQuicklyChecklistIntoJsonUseCase : KotlinResultUseCase<QuicklyChecklist, String>() {
    override suspend fun execute(params: QuicklyChecklist): Result<String> {
        return try {
            Result.success(Gson().toJson(params))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}