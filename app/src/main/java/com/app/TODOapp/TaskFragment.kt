package com.app.TODOapp

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.android.synthetic.main.todo_item.*

class TaskFragment : Fragment(R.layout.fragment_task) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        val todoAdapter = TodoAdapter(
            requireContext().applicationContext,
            mutableListOf(),
            fragment = TaskFragment()
        )
        val database = AppDatabase.getDatabase(requireContext().applicationContext)
        database.taskDao().setAllToUnchecked()
        todoAdapter.todoList = database.taskDao().getAll()

        taskList.adapter = todoAdapter
        taskList.layoutManager = LinearLayoutManager(parentFragment?.context)

        taskInput.visibility = View.INVISIBLE
        showDescriptionInput.visibility = View.INVISIBLE
        showTaskInput.visibility = View.INVISIBLE
        descriptionInput.visibility = View.INVISIBLE

        if (todoAdapter.itemCount == 0) {
            emptyTaskHint.visibility = View.VISIBLE
        } else {
            emptyTaskHint.visibility = View.INVISIBLE
        }

        addTask.setOnClickListener {
            addTask.visibility = View.INVISIBLE
            taskInput.visibility = View.VISIBLE
            showDescriptionInput.visibility = View.VISIBLE
            taskInput.requestFocus()
            imm.showSoftInput(taskInput, InputMethodManager.SHOW_IMPLICIT)
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
                addTask.visibility = View.VISIBLE
                descriptionInput.visibility = View.INVISIBLE
                taskInput.visibility = View.INVISIBLE
                showTaskInput.visibility = View.INVISIBLE
                imm.hideSoftInputFromWindow(view.windowToken, 0)
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
                    taskInput.clearFocus()
                    addTask.visibility = View.VISIBLE
                    taskInput.visibility = View.INVISIBLE
                    showDescriptionInput.visibility = View.INVISIBLE
                    emptyTaskHint.visibility = View.INVISIBLE
                    // Force the soft keyboard to hide
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    return@setOnEditorActionListener true
                }
            }
            false
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_edit_desc -> {
                Toast.makeText(context, "edit was pressed", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_delete_task -> {
                Toast.makeText(context, "delete was pressed", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
}

