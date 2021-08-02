package com.example.projectx

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.todo_item.view.*


class CompletedAdapter(
    var completedList: MutableList<Completed>
) : RecyclerView.Adapter<CompletedAdapter.CompletedViewHolder>() {

    inner class CompletedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompletedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return CompletedViewHolder(view)
    }

    override fun getItemCount(): Int {
        return completedList.size
    }

    override fun onBindViewHolder(holder: CompletedViewHolder, position: Int) {
        holder.itemView.apply {
            taskTitle.text = completedList[position].task
            taskCheckBox.isChecked = completedList[position].equals(true)
        }
    }

    fun addTask(completed: Completed, db: AppDatabase) {
        completedList.add(completed)
        notifyItemInserted(completedList.size-1)
    }
}