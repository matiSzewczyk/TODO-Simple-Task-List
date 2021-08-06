package com.app.TODOapp

import android.content.Context
import android.os.Build
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.android.synthetic.main.todo_item.view.*


class TodoAdapter(
    val context: Context, // Get the context of the app for the db
    var todoList: MutableList<Task>,
    val fragment: Fragment = TaskFragment()
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>(), View.OnCreateContextMenuListener {
    private val menuInflater = MenuInflater(context)

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        view.setOnCreateContextMenuListener(this)
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

    @RequiresApi(Build.VERSION_CODES.M)
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
        holder.itemView.setOnClickListener {
            holder.itemView.apply {
                if (!todoList[position].description.isNullOrEmpty()) {
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateContextMenu(
        p0: ContextMenu?,
        p1: View?,
        p2: ContextMenu.ContextMenuInfo?
    ) {
        val inflater = menuInflater
        inflater.inflate(R.menu.context_menu, p0)
    }

}