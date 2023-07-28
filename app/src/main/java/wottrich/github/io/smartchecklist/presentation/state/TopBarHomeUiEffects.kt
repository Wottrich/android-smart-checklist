package wottrich.github.io.smartchecklist.presentation.state

sealed class TopBarHomeUiEffects {
    data class ShareChecklistAsText(val checklistAsText: String) : TopBarHomeUiEffects()
}