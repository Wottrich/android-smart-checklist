package wottrich.github.io.quicklychecklist.impl.domain

import android.util.Base64
import wottrich.github.io.tools.base.KotlinResultUseCase
import wottrich.github.io.tools.base.Result

class EncodeQuicklyChecklistUseCase : KotlinResultUseCase<String, String>() {
    override suspend fun execute(params: String): Result<String> {
        return try {
            val encodedQuicklyChecklist =
                Base64.encodeToString(params.toByteArray(), Base64.DEFAULT).removeEnter()
            Result.success(encodedQuicklyChecklist)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    private fun String.removeEnter() = this.replace("\n", "")
}