package wottrich.github.io.smartchecklist.presentation.task.model

import androidx.annotation.StringRes
import wottrich.github.io.smartchecklist.datasource.data.model.Task

sealed class BaseTaskListItem {
    fun getTaskItemOrNull(): TaskItem? = this as? TaskItem

    data class TaskItem(val task: Task) : BaseTaskListItem()

    data class SectionItem(@StringRes val sectionName: Int) : BaseTaskListItem()

}
