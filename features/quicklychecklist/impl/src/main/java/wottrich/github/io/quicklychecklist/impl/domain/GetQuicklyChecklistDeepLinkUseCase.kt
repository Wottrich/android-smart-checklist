package wottrich.github.io.quicklychecklist.impl.domain

import wottrich.github.io.quicklychecklist.impl.constants.QuicklyChecklistConstants
import wottrich.github.io.tools.base.KotlinResultUseCase
import wottrich.github.io.tools.base.Result

class GetQuicklyChecklistDeepLinkUseCase(
    private val encodeQuicklyChecklistUseCase: EncodeQuicklyChecklistUseCase
) : KotlinResultUseCase<String, String>() {
    override suspend fun execute(params: String): Result<String> {
        val result = encodeQuicklyChecklistUseCase(params)
        val encodedQuicklyChecklist = result.getOrNull()
        return if (encodedQuicklyChecklist != null) {
            Result.success(
                QuicklyChecklistConstants.buildQuicklyChecklistDeepLinkUrl(encodedQuicklyChecklist)
            )
        } else {
            Result.failure(
                result.exceptionOrNull() ?: defaultGetQuicklyChecklistDeepLinkException
            )
        }
    }

    companion object {
        private val defaultGetQuicklyChecklistDeepLinkException = Throwable(
            "Failure create quickly checklist deeplink"
        )
    }
}