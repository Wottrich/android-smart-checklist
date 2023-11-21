package wottrich.github.io.smartchecklist.presentation.task.model

import androidx.annotation.StringRes
import wottrich.github.io.smartchecklist.datasource.entity.NewTask

enum class BaseTaskListItemType {
    TASK, SECTION;
}

sealed class BaseTaskListItem(val type: BaseTaskListItemType) {
    fun getTaskItemOrNull(): TaskItem? = this as? TaskItem

    data class TaskItem(val task: NewTask) : BaseTaskListItem(BaseTaskListItemType.TASK)

    data class SectionItem(@StringRes val sectionName: Int) : BaseTaskListItem(BaseTaskListItemType.SECTION)

}
