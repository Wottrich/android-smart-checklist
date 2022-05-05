package github.io.wottrich.ui.support.domain

import github.io.wottrich.ui.support.R
import github.io.wottrich.ui.support.data.HelpOverviewItem
import wottrich.github.io.tools.base.KotlinResultUseCase
import wottrich.github.io.tools.base.Result
import wottrich.github.io.tools.base.UseCase
import wottrich.github.io.tools.base.UseCase.None

class GetHelpOverviewItemsUseCase : KotlinResultUseCase<UseCase.None, List<HelpOverviewItem>>() {
    override suspend fun execute(params: None): Result<List<HelpOverviewItem>> {
        return Result.success(
            listOf(
                HelpOverviewItem(
                    label = R.string.support_help_overview_tutorial_how_delete_tasks,
                    description = R.string.support_help_overview_tutorial_how_delete_tasks_explain,
                    HOW_DELETE_TASK_DEEP_LINK
                )
            )
        )
    }

    companion object {
        private const val HOW_DELETE_TASK_DEEP_LINK = "smart://how_delete_task"
    }
}