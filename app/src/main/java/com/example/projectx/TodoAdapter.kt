package com.example.projectx

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.todo_item.view.*


class TodoAdapter(
    val context: Context, // Get the context of the app for the db
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

    private val database = AppDatabase.getDatabase(context)
    private val completedAdapter = CompletedAdapter(context, mutableListOf())

    private fun addToCompleted(taskTitle: TextView) {
        val done = Completed(taskTitle.text.toString(), null, true)
        database.completedDao().addCompleted(done)
        database.taskDao().deleteTask(taskTitle.text.toString())
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.itemView.apply {// This let's me directly call taskTitle instead of writing holder.itemView.taskTitle
            taskTitle.text = todoList[position].task
            taskCheckBox.isChecked = todoList[position].checked
            taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    addToCompleted(taskTitle)
                }
                todoList.removeAt(position)
                notifyItemRemoved(position)
            }
            taskTitle?.setOnClickListener {
                if(!todoList[position].description.isNullOrEmpty()) {
                    if (taskDescription.text == "") {
                        taskTitle.text = ""
                        taskDescription.text = todoList[position].description
                    } else {
                        taskTitle.text = todoList[position].task
                        taskDescription.text = ""
                    }
                } else {
                    Toast.makeText(context, "No description for task.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun addTask(task: Task, db: AppDatabase) {
        todoList.add(task)
        db.taskDao().insertTask(task)
        notifyItemInserted(todoList.size - 1)
    }
}