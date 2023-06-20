package wottrich.github.io.androidsmartchecklist.presentation.state

import androidx.annotation.StringRes
import wottrich.github.io.datasource.entity.NewTask

interface HomeUiActions {
 fun sendAction(action: Action)

 sealed class Action {
  object OnChangeEditModeAction : Action()
  data class OnShowTaskChangeStatusSnackbar(val task: NewTask) : Action()
  object DeleteChecklistAction : Action()
  object OnShareQuicklyChecklistAction : Action()
  data class OnSnackbarError(@StringRes val message: Int) : Action()
 }
}