package wottrich.github.io.smartchecklist.quicklychecklist.domain

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.quicklychecklist.constants.QuicklyChecklistConstants

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