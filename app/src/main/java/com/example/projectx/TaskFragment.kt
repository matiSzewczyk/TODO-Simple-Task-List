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
//    private lateinit var todoAdapter: TodoAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val todoAdapter = TodoAdapter(requireContext(), mutableListOf())
        // db stuff
        val database = AppDatabase.getDatabase(requireContext().applicationContext)
        todoAdapter.todoList = database.taskDao().getAll()


        taskInput.visibility = View.INVISIBLE

        taskList.adapter = todoAdapter
        taskList.layoutManager = LinearLayoutManager(parentFragment?.context)

        addTask.setOnClickListener {
            addTask.visibility = View.INVISIBLE
            taskInput.visibility = View.VISIBLE
            taskInput.requestFocus()
            taskInput.showSoftInputOnFocus

            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(taskInput, InputMethodManager.SHOW_IMPLICIT)
        }
        taskInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val taskTitle = taskInput.text.toString()
                if (!taskTitle.isEmpty()) {
                    val task = Task(taskTitle, false)
                    todoAdapter.addTask(task, database)
                    taskInput.text.clear()
                    addTask.visibility = View.VISIBLE
                    taskInput.visibility = View.INVISIBLE
                    taskInput.clearFocus()
                    return@setOnEditorActionListener true
                }
            }
            false
        }
    }
}
