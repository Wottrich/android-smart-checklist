package wottrich.github.io.androidsmartchecklist.presentation.state

sealed class TopBarHomeUiEffects {
    data class ShareChecklistAsText(val checklistAsText: String) : TopBarHomeUiEffects()
}