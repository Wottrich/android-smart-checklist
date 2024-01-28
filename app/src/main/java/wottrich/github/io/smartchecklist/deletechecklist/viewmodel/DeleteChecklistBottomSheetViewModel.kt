package wottrich.github.io.smartchecklist.deletechecklist.viewmodel

import kotlinx.coroutines.flow.Flow
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.checklist.domain.DeleteChecklistUseCase
import wottrich.github.io.smartchecklist.checklist.domain.GetSelectedChecklistUseCase
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import wottrich.github.io.smartchecklist.kotlin.SingleShotEventBus

class DeleteChecklistBottomSheetViewModel(
    private val getSelectedChecklistUseCase: GetSelectedChecklistUseCase,
    private val deleteChecklistUseCase: DeleteChecklistUseCase
) : BaseViewModel() {

    private val _uiEffect = SingleShotEventBus<DeleteChecklistUiEffect>()
    val uiEffect: Flow<DeleteChecklistUiEffect> = _uiEffect.events

    fun onDeleteChecklistClicked() {
        launchIO {
            val checklist = checkNotNull(getSelectedChecklistUseCase().getOrNull())
            deleteChecklistUseCase(checklist.uuid).onSuccess {
                _uiEffect.emit(DeleteChecklistUiEffect.DeletedWithSuccess)
            }
        }
    }
}

sealed class DeleteChecklistUiEffect {
    data object DeletedWithSuccess : DeleteChecklistUiEffect()
}