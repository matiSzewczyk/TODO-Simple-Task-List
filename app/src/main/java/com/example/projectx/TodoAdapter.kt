package com.example.projectx

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.todo_item.view.*


class TodoAdapter(
    val context: Context,
    var todoList: MutableList<Task>
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return TodoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val database = AppDatabase.getDatabase(context)
        val completedAdapter = CompletedAdapter(mutableListOf())
        completedAdapter.completedList = database.completedDao().getAll()
        holder.itemView.apply {
            taskTitle.text = todoList[position].task
            taskCheckBox.isChecked = todoList[position].checked
            taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    val done = Completed(taskTitle.text.toString())
                    database.completedDao().addCompleted(done)
                    database.taskDao().deleteTask(taskTitle.text.toString())
                }
                notifyItemRemoved(position)
            }
        }
    }

    fun addTask(task: Task, db: AppDatabase) {
        todoList.add(task)
        db.taskDao().insertTask(task)
        notifyItemInserted(todoList.size-1)
    }
}
//                for (task in todoList) {
//                    val checked: Boolean = database.taskDao().getChecked(task.task)
//                    println(checked)
//                    if (checked) {
//                        val done = Completed(task.task)
//                        completedAdapter.addTask(done, database)
//                        println("shoudl be working")
//                        database.taskDao().deleteTask(task.task)
//                    }
//                }
