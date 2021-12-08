package wottrich.github.io.featurenew.view.screens.checklistdetail

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 21/10/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

sealed class TaskListState {
    object Overview : TaskListState()
    object Edit : TaskListState()
}