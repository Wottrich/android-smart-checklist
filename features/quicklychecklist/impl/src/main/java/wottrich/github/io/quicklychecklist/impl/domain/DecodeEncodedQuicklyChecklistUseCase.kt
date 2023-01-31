package wottrich.github.io.quicklychecklist.impl.domain

import android.util.Base64
import wottrich.github.io.tools.base.KotlinResultUseCase
import wottrich.github.io.tools.base.Result

class DecodeEncodedQuicklyChecklistUseCase : KotlinResultUseCase<String, String>() {
    override suspend fun execute(params: String): Result<String> {
        return try {
            val decodedQuicklyChecklistJson = Base64.decode(params, Base64.DEFAULT)
            Result.success(String(decodedQuicklyChecklistJson))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}