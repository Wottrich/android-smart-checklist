package wottrich.github.io.quicklychecklist.impl.domain

import github.io.wottrich.coroutines.KotlinResultUseCase
import github.io.wottrich.coroutines.base.Result
import wottrich.github.io.quicklychecklist.impl.constants.QuicklyChecklistConstants

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