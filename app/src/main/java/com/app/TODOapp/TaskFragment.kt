package com.app.TODOapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.android.synthetic.main.todo_item.*

class TaskFragment : Fragment(R.layout.fragment_task), RecyclerViewInterface {

    private lateinit var database: AppDatabase
    private lateinit var todoAdapter: TodoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todoAdapter = TodoAdapter(
            requireContext().applicationContext,
            mutableListOf(),
            this
        )

        database = AppDatabase.getDatabase(requireContext().applicationContext)
        database.taskDao().setAllToUnchecked()
        todoAdapter.todoList = database.taskDao().getAll()

        taskList.adapter = todoAdapter
        taskList.layoutManager = LinearLayoutManager(parentFragment?.context)

        taskInput.visibility = View.INVISIBLE
        showDescriptionInput.visibility = View.INVISIBLE
        showTaskInput.visibility = View.INVISIBLE
        descriptionInput.visibility = View.INVISIBLE

        registerForContextMenu(taskList)

        if (todoAdapter.itemCount == 0) {
            emptyTaskHint.visibility = View.VISIBLE
        } else {
            emptyTaskHint.visibility = View.INVISIBLE
        }

        addTask.setOnClickListener {
            addTask.visibility = View.INVISIBLE
            taskInput.visibility = View.VISIBLE
            showDescriptionInput.visibility = View.VISIBLE
            showSoftKeyboard(taskInput)
        }

        showTaskInput.setOnClickListener {
            descriptionInput.visibility = View.INVISIBLE
            showDescriptionInput.visibility = View.VISIBLE
            showTaskInput.visibility = View.INVISIBLE
            taskInput.visibility = View.VISIBLE
        }

        showDescriptionInput.setOnClickListener {
            if (taskInput.text.isNotEmpty()) {
                showDescriptionInput.visibility = View.INVISIBLE
                showTaskInput.visibility = View.VISIBLE
                taskInput.visibility = View.INVISIBLE
                descriptionInput.visibility = View.VISIBLE
                showSoftKeyboard(descriptionInput)
            } else {
                Toast.makeText(context, "Please add a task first :)", Toast.LENGTH_SHORT).show()
            }
        }

        descriptionInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val taskTitle = taskInput.text.toString()
                val taskDescription = descriptionInput.text.toString()
                val task = Task(taskTitle, taskDescription,  false)
                todoAdapter.addTask(task, database)
                taskInput.text.clear()
                taskInput.clearFocus()
                descriptionInput.text.clear()
                addTask.visibility = View.VISIBLE
                descriptionInput.visibility = View.INVISIBLE
                taskInput.visibility = View.INVISIBLE
                showTaskInput.visibility = View.INVISIBLE
                hideSoftKeyboard(descriptionInput)
                emptyTaskHint.visibility = View.INVISIBLE
                return@setOnEditorActionListener true
            }
            false
        }

        taskInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val taskTitle = taskInput.text.toString()
                if (taskTitle.isNotEmpty()) {
                    val task = Task(taskTitle, null,  false)
                    todoAdapter.addTask(task, database)
                    taskInput.text.clear()
                    addTask.visibility = View.VISIBLE
                    taskInput.visibility = View.INVISIBLE
                    showDescriptionInput.visibility = View.INVISIBLE
                    emptyTaskHint.visibility = View.INVISIBLE
                    // Force the soft keyboard to hide
                    hideSoftKeyboard(taskInput)
                    return@setOnEditorActionListener true
                }
            }
            false
        }
    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideSoftKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }

    private fun showPopupMenu(v: View, index: Int): PopupMenu {
        val menu = PopupMenu(v.context, v)
        menu.inflate(R.menu.context_menu)
        menu.setOnMenuItemClickListener {
            val task = todoAdapter.todoList[index].task
            when (it?.itemId) {
                R.id.menu_edit_desc -> {
                    descriptionInput.visibility = View.VISIBLE
                    addTask.visibility = View.INVISIBLE
                    showSoftKeyboard(descriptionInput)
                    descriptionInput.setOnEditorActionListener { _, _, _ ->
                        val taskDescription = descriptionInput.text.toString()
                        database.taskDao().changeDescription(task, taskDescription)
                        descriptionInput.text.clear()
                        hideSoftKeyboard(descriptionInput)
                        descriptionInput.visibility = View.INVISIBLE
                        addTask.visibility = View.VISIBLE
                        todoAdapter.todoList[index] = Task(task, taskDescription)
                        todoAdapter.notifyItemChanged(index)
                        false
                    }
                    true
                }
                R.id.menu_delete_task -> {
                    database.taskDao().deleteTask(task)
                    todoAdapter.todoList.removeAt(index)
                    todoAdapter.notifyItemRemoved(index)
                    true
                }
                else -> false
            }

        }
        menu.show()
        return menu
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun myLongClickListener(position: Int, view: View?) {
        showPopupMenu(view!!, position)
    }
}

