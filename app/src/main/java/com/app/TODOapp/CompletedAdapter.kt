package com.app.TODOapp

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.todo_item.view.*


class CompletedAdapter(
    val context: Context, // Get the context of the app for the db
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

    private val database = AppDatabase.getDatabase(context)
    override fun onBindViewHolder(holder: CompletedViewHolder, position: Int) {
        holder.itemView.apply {
            taskTitle.text = completedList[position].task
            taskCheckBox.isChecked = completedList[position].checked
            val checked: Boolean = completedList[position].checked
            taskCheckBox.setOnCheckedChangeListener { _, _ ->
                if (checked) {
                    println("The item count is: $itemCount")
                    database.completedDao()
                        .changeChecked(completedList[position].task, false)
                } else {
                    database.completedDao()
                        .changeChecked(completedList[position].task, true)
                }
            }
        }
    }

    fun moveToTaskList() {
        database.completedDao().moveToTaskList()
        database.completedDao().deleteSelected()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteAll() {
        database.completedDao().deleteAllTasks()
        completedList.clear()
        notifyDataSetChanged()
    }
}