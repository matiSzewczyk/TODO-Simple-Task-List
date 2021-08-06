package com.app.TODOapp

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.android.synthetic.main.todo_item.*

class TaskFragment : Fragment(R.layout.fragment_task) {

    private lateinit var item: RecyclerView
    private lateinit var database: AppDatabase
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var imm: InputMethodManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
         todoAdapter = TodoAdapter(
            requireContext().applicationContext,
            mutableListOf(),
            fragment = TaskFragment()
        )

        database = AppDatabase.getDatabase(requireContext().applicationContext)
        database.taskDao().setAllToUnchecked()
        todoAdapter.todoList = database.taskDao().getAll()

        taskList.adapter = todoAdapter
        taskList.layoutManager = LinearLayoutManager(parentFragment?.context)

        visibilityInit()

        addTask.setOnClickListener {
            visibilityAddTask()
            taskInput.requestFocus()
            imm.showSoftInput(taskInput, InputMethodManager.SHOW_IMPLICIT)
        }

        showTaskInput.setOnClickListener {
            visibilityToggleTask()
            taskInput.visibility = View.VISIBLE
        }

        showDescriptionInput.setOnClickListener {
            if (taskInput.text.isNotEmpty()) {
                visibilityToggleDescription()
                descriptionInput.requestFocus()
                imm.showSoftInput(descriptionInput, InputMethodManager.SHOW_IMPLICIT)
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
                descriptionInput.clearFocus()
                visibilityPostInput()
                imm.hideSoftInputFromWindow(view.windowToken, 0)
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
                    taskInput.clearFocus()
                    visibilityPostInput()
                    // Force the soft keyboard to hide
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    return@setOnEditorActionListener true
                }
            }
            false
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val index = item.order
        return when (item.itemId) {
            R.id.menu_edit_desc -> {
                descriptionInput.visibility = View.VISIBLE
                addTask.visibility = View.INVISIBLE
                descriptionInput.requestFocus()
                imm.showSoftInput(descriptionInput, InputMethodManager.SHOW_IMPLICIT)
                descriptionInput.setOnEditorActionListener { _, _, _ ->
                    val task = todoAdapter.todoList[index].task.toString()
                    val taskDescription = descriptionInput.text.toString()
                    database.taskDao().changeDescription(task, taskDescription)
                    imm.hideSoftInputFromWindow(view?.windowToken, 0)
                    descriptionInput.visibility = View.INVISIBLE
                    addTask.visibility = View.VISIBLE
                    todoAdapter.todoList[index] = Task(task, taskDescription)
                    todoAdapter.notifyItemChanged(index)
                    false
                }
                true
            }
            R.id.menu_delete_task -> {
                val task = todoAdapter.todoList[index].task.toString()
                database.taskDao().deleteTask(task)
                todoAdapter.todoList.removeAt(index)
                todoAdapter.notifyItemRemoved(index)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
    private fun visibilityInit() {
        taskInput.visibility = View.INVISIBLE
        showDescriptionInput.visibility = View.INVISIBLE
        showTaskInput.visibility = View.INVISIBLE
        descriptionInput.visibility = View.INVISIBLE

        if (todoAdapter.itemCount == 0) {
            emptyTaskHint.visibility = View.VISIBLE
        } else {
            emptyTaskHint.visibility = View.INVISIBLE
        }
    }

    private fun visibilityAddTask() {
        addTask.visibility = View.INVISIBLE
        taskInput.visibility = View.VISIBLE
        showDescriptionInput.visibility = View.VISIBLE
    }

    private fun visibilityToggleTask() {
        descriptionInput.visibility = View.INVISIBLE
        showDescriptionInput.visibility = View.VISIBLE
        showTaskInput.visibility = View.INVISIBLE
    }
    private fun visibilityToggleDescription() {
        showDescriptionInput.visibility = View.INVISIBLE
        showTaskInput.visibility = View.VISIBLE
        taskInput.visibility = View.INVISIBLE
        descriptionInput.visibility = View.VISIBLE
    }

    private fun visibilityPostInput() {
        addTask.visibility = View.VISIBLE
        descriptionInput.visibility = View.INVISIBLE
        taskInput.visibility = View.INVISIBLE
        showTaskInput.visibility = View.INVISIBLE
        emptyTaskHint.visibility = View.INVISIBLE
    }

}

