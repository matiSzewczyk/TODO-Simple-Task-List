package com.app.TODOapp

import android.content.Context
import android.os.Build
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.android.synthetic.main.todo_item.view.*


class TodoAdapter(
    val context: Context, // Get the context of the app for the db
    var todoList: MutableList<Task>,
    val fragment: Fragment = TaskFragment()
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>(), View.OnLongClickListener,
    PopupMenu.OnMenuItemClickListener {
    private val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    private val menuInflater = MenuInflater(context)

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            apply {
                itemView.setOnLongClickListener {
                    showPopupMenu(itemView).setOnMenuItemClickListener {
                        val index = layoutPosition
                        when (it?.itemId) {
                            R.id.menu_edit_desc -> {
//                                fragment.test(index)
                                true
                            }
                            R.id.menu_delete_task -> {
                                val task = todoList[index].task.toString()
                                database.taskDao().deleteTask(task)
                                todoList.removeAt(index)
                                notifyItemRemoved(index)
                                true
                            }
                            else -> false
                        }
                    }
                    true
                }
            }
        }
    }

    private fun showPopupMenu(v: View): PopupMenu {
        val menu = PopupMenu(v.context, v)
        menu.inflate(R.menu.context_menu)
        menu.setOnMenuItemClickListener(this)
        menu.show()
        return menu
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        view.setOnLongClickListener(this)
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

    override fun onLongClick(p0: View?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        TODO("Not yet implemented")
    }
}