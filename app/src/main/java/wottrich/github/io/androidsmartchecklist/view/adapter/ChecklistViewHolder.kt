package wottrich.github.io.androidsmartchecklist.view.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.androidsmartchecklist.databinding.RowChecklistBinding
import wottrich.github.io.database.entity.Checklist

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 12/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */
 
class ChecklistViewHolder (private val binding: RowChecklistBinding) : RecyclerView.ViewHolder(binding.root) {

    fun Checklist.bind(onClick: (Checklist) -> Unit) {
        val checklist = this
        binding.apply {
            this.checklist = checklist
        }
        itemView.setOnClickListener { onClick(this) }
    }

}