package wottrich.github.io.smartchecklist.domain.usecase

import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.presentation.task.model.BaseTaskListItem

class ReverseTasksIfNeededUseCase :
    KotlinResultUseCase<ReverseTasksIfNeededUseCase.Params, List<BaseTaskListItem>>() {
    override suspend fun execute(params: Params): Result<List<BaseTaskListItem>> {
        val tasks = if (params.shouldReverseList) {
            params.tasks.reversed()
        } else {
            params.tasks
        }
        return Result.success(tasks)
    }

    class Params(
        val shouldReverseList: Boolean,
        val tasks: List<BaseTaskListItem>
    )
}