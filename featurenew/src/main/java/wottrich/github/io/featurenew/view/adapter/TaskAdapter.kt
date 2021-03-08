package wottrich.github.io.featurenew.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import wottrich.github.io.database.entity.Task

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 04/10/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

class TaskAdapter(
    private var tasks: List<Task>
) : RecyclerView.Adapter<TaskViewHolder>() {

    private var onTaskAction: ((TaskViewHolderAction) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.new(parent)
    }

    private fun getItem(position: Int) =
        tasks.asReversed()[position]

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item) { onTaskAction?.invoke(it) }
    }

    override fun getItemCount(): Int = tasks.size

    fun updateItems(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    fun setOnTaskAction(onTaskAction: (TaskViewHolderAction) -> Unit) {
        this.onTaskAction = onTaskAction
    }

}