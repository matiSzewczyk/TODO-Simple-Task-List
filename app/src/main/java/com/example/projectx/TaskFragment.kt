package com.example.projectx

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.android.synthetic.main.todo_item.*

class TaskFragment : Fragment(R.layout.fragment_task) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Input method manager
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // Init Database
        val database = AppDatabase.getDatabase(requireContext().applicationContext)

        val todoAdapter = TodoAdapter(requireContext(), mutableListOf())
        todoAdapter.todoList = database.taskDao().getAll()

        taskList.adapter = todoAdapter
        taskList.layoutManager = LinearLayoutManager(parentFragment?.context)

        taskInput.visibility = View.INVISIBLE

        addTask.setOnClickListener {
            addTask.visibility = View.INVISIBLE
            taskInput.visibility = View.VISIBLE
            taskInput.requestFocus()
            imm.showSoftInput(taskInput, InputMethodManager.SHOW_IMPLICIT)
        }
        taskInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val taskTitle = taskInput.text.toString()
                if (taskTitle.isNotEmpty()) {
                    val task = Task(taskTitle, false)
                    todoAdapter.addTask(task, database)
                    taskInput.text.clear()
                    taskInput.clearFocus()
                    addTask.visibility = View.VISIBLE
                    taskInput.visibility = View.INVISIBLE
                    // Force the soft keyboard to hide
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                    return@setOnEditorActionListener true
                }
            }
            false
        }
    }
}
