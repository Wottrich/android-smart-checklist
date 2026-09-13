package wottrich.github.io.smartchecklist.presentation.effect

import androidx.annotation.StringRes

sealed class TaskComponentViewModelUiEffect {
    data object OpenBottomSheet : TaskComponentViewModelUiEffect()
    data class OnError(@StringRes val stringRes: Int) : TaskComponentViewModelUiEffect()
}