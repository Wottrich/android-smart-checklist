package wottrich.github.io.smartchecklist.quicklychecklist.constants

object QuicklyChecklistConstants {
    const val QUICKLY_CHECKLIST_URL = "http://wottrich.github.io/quicklychecklist"
    fun buildQuicklyChecklistDeepLinkUrl(
        encodedQuicklyChecklist: String
    ): String {
        return "$QUICKLY_CHECKLIST_URL?checklist=$encodedQuicklyChecklist"
    }
}