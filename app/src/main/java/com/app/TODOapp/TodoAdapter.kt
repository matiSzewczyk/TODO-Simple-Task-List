package com.app.TODOapp

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
    var todoList: MutableList<Task>,
    private val  myInterface: RecyclerViewInterface
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnLongClickListener {
                myInterface.myLongClickListener(absoluteAdapterPosition, itemView)
                true
            }
            itemView.setOnClickListener {
                myInterface.myClickListener(absoluteAdapterPosition, itemView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return TodoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    private val database = AppDatabase.getDatabase(context)

    private fun addToCompleted(taskTitle: TextView, taskDescription: TextView) {
        val done = Completed(taskTitle.text.toString(), taskDescription.text.toString(), true)
        database.completedDao().addCompleted(done)
        database.taskDao().deleteTask(taskTitle.text.toString())
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.itemView.apply {
            taskTitle.text = todoList[position].task
            taskCheckBox.isChecked = todoList[position].checked
            taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    taskDescription.text = todoList[holder.absoluteAdapterPosition].description
                    addToCompleted(taskTitle, taskDescription)
                    todoList.removeAt(holder.absoluteAdapterPosition)
                    notifyItemRemoved(holder.absoluteAdapterPosition)
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