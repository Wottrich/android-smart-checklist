package wottrich.github.io.androidsmartchecklist.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.androidsmartchecklist.R
import wottrich.github.io.database.entity.Checklist

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 12/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */
 
class ChecklistAdapter(
    context: Context,
    private val items: LiveData<List<Checklist>>,
    private val onClick: (Checklist) -> Unit,
    private val inflater: LayoutInflater = LayoutInflater.from(context)
) : RecyclerView.Adapter<ChecklistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistViewHolder {
        return ChecklistViewHolder(
            DataBindingUtil.inflate(inflater, R.layout.row_checklist, parent, false)
        )
    }

    override fun getItemCount(): Int = items.value.orEmpty().size

    private fun getItem (position: Int): Checklist?
            = items.value?.get(position)

    override fun onBindViewHolder(holder: ChecklistViewHolder, position: Int) {
        holder.apply {
            getItem(position)?.bind {
                onClick(it)
            }
        }
    }

}