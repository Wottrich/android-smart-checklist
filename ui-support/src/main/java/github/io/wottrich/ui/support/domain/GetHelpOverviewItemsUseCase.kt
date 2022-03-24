package github.io.wottrich.ui.support.domain

import github.io.wottrich.ui.support.R
import github.io.wottrich.ui.support.data.HelpOverviewItem

class GetHelpOverviewItemsUseCase {

    operator fun invoke(): List<HelpOverviewItem> {
        return listOf(
            HelpOverviewItem(
                label = R.string.support_help_overview_tutorial_how_delete_tasks,
                description = R.string.support_help_overview_tutorial_how_delete_tasks_explain,
                HOW_DELETE_TASK_DEEP_LINK
            )
        )
    }

    companion object {
        private const val HOW_DELETE_TASK_DEEP_LINK = "smart://how_delete_task"
    }
}