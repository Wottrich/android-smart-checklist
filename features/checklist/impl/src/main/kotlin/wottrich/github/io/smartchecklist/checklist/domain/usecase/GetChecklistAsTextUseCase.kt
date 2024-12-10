package wottrich.github.io.smartchecklist.checklist.domain.usecase

import wottrich.github.io.smartchecklist.checklist.domain.GetChecklistAsTextUseCase
import wottrich.github.io.smartchecklist.checklist.domain.GetSelectedChecklistUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result

class GetChecklistAsTextUseCaseImpl(
    private val getSelectedChecklistUseCase: GetSelectedChecklistUseCase
) : GetChecklistAsTextUseCase() {
    override suspend fun execute(params: UseCase.None): Result<String> {
        return try {
            val checklist = getSelectedChecklistUseCase()
            Result.success(checklist.toString())
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}