package wottrich.github.io.featurenew.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import wottrich.github.io.database.entity.Task
import wottrich.github.io.featurenew.databinding.RowTaskBinding
import wottrich.github.io.tools.extensions.inflater

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 04/10/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

sealed class TaskViewHolderAction {
    data class CheckTask(val task: Task) : TaskViewHolderAction()
    data class DeleteTask(val task: Task) : TaskViewHolderAction()
}

class TaskViewHolder(
    private val binding: RowTaskBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(task: Task, onAction: (TaskViewHolderAction) -> Unit) {
        binding.apply {
            this.task = task
            this.imgBtnCheck.setOnClickListener { onAction(TaskViewHolderAction.CheckTask(task)) }
            this.imgBtnDelete.setOnClickListener { onAction(TaskViewHolderAction.DeleteTask(task)) }
        }

    }

    companion object {

        fun new(parent: ViewGroup): TaskViewHolder {
            val inflater = parent.inflater
            return TaskViewHolder(
                RowTaskBinding.inflate(inflater, parent, false)
            )
        }

    }

}