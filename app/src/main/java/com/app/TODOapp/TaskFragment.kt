package com.app.TODOapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.android.synthetic.main.todo_item.*

class TaskFragment : Fragment(R.layout.fragment_task), RecyclerViewInterface {

    private lateinit var database: AppDatabase
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var completedAdapter: CompletedAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todoAdapter = TodoAdapter(
            requireContext().applicationContext,
            mutableListOf(),
            this
        )
        completedAdapter = CompletedAdapter(
            requireContext().applicationContext,
            mutableListOf()
        )

        database = AppDatabase.getDatabase(requireContext().applicationContext)
        database.taskDao().setAllToUnchecked()
        todoAdapter.todoList = database.taskDao().getAll()

        taskList.adapter = todoAdapter
        taskList.layoutManager = LinearLayoutManager(parentFragment?.context)

        taskInput.visibility = View.INVISIBLE
        showDetailsInput.visibility = View.INVISIBLE
        showTaskInput.visibility = View.INVISIBLE
        detailsInput.visibility = View.INVISIBLE

        registerForContextMenu(taskList)

        if (todoAdapter.itemCount == 0) {
            emptyTaskHint.visibility = View.VISIBLE
        } else {
            emptyTaskHint.visibility = View.INVISIBLE
        }

        addTask.setOnClickListener {
            displayTaskInput()
        }

        showTaskInput.setOnClickListener {
            displayTaskInput()
        }

        showDetailsInput.setOnClickListener {
            if (taskInput.text.isNotEmpty()) {
                displayDetailsInput()
            } else {
                Toast.makeText(context, "Please add a task first :)", Toast.LENGTH_SHORT).show()
            }
        }

        detailsInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                createTask()
                return@setOnEditorActionListener true
            }
            false
        }

        taskInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val taskTitle = taskInput.text.toString()
                if (taskTitle.isNotEmpty()) {
                    createTask()
                    return@setOnEditorActionListener true
                }
            }
            false
        }
    }

    private fun createTask() {
        val taskTitle = taskInput.text.toString()
        val taskDetails: String? = if (detailsInput.text.isNotEmpty()) {
            detailsInput.text.toString()
        } else {
            null
        }
        val task = Task(taskTitle, taskDetails,  false)
        todoAdapter.addTask(task, database)
        taskInput.text.clear()
        taskInput.clearFocus()
        detailsInput.text.clear()
        addTask.visibility = View.VISIBLE
        detailsInput.visibility = View.INVISIBLE
        taskInput.visibility = View.INVISIBLE
        showTaskInput.visibility = View.INVISIBLE
        hideSoftKeyboard(detailsInput)
        emptyTaskHint.visibility = View.INVISIBLE

    }

    private fun displayTaskInput() {
        addTask.visibility = View.INVISIBLE
        detailsInput.visibility = View.INVISIBLE
        taskInput.visibility = View.VISIBLE
        showDetailsInput.visibility = View.VISIBLE
        showSoftKeyboard(taskInput)

    }

    private fun displayDetailsInput() {
        showDetailsInput.visibility = View.INVISIBLE
        showTaskInput.visibility = View.VISIBLE
        taskInput.visibility = View.INVISIBLE
        detailsInput.visibility = View.VISIBLE
        showSoftKeyboard(detailsInput)

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

    private fun showPopupMenu(v: View, index: Int) {
        val menu = PopupMenu(v.context, v)
        menu.inflate(R.menu.context_menu)
        menu.setOnMenuItemClickListener {
            val task = todoAdapter.todoList[index].task
            when (it?.itemId) {
                R.id.menu_edit_title -> {
                    editTitle(task, index)
                    true
                }
                R.id.menu_edit_details -> {
                    editDetails(task, index)
                    true
                }
                R.id.menu_delete_task -> {
                    deleteTask(task, index)
                    true
                }
                else -> false
            }
        }
        menu.show()
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun myLongClickListener(position: Int, view: View?) {
        showPopupMenu(view!!, position)
    }

    override fun myClickListener(position: Int, view: View?) {
        val details = view?.findViewById<TextView>(R.id.taskDetails)

            if (!todoAdapter.todoList[position].details.isNullOrEmpty()) {
                if (details!!.visibility == View.GONE) {
                    details.visibility = View.VISIBLE
                } else {
                    details.visibility = View.GONE
                }
            } else {
                Toast.makeText(context, "No details for this task.", Toast.LENGTH_SHORT).show()
            }
    }

    override fun myCheckedChangeListener(position: Int, isChecked: Boolean, view: View) {
        val details = view.findViewById<TextView>(R.id.taskDetails)
        val title = view.findViewById<TextView>(R.id.taskTitle)

        if (isChecked) {
            details?.text = todoAdapter.todoList[position].details
            completedAdapter.addToCompleted(title, details)
            todoAdapter.todoList.removeAt(position)
            todoAdapter.notifyItemRemoved(position)
        }
    }

    private fun editTitle(task: String, index: Int) {
        taskInput.visibility = View.VISIBLE
        addTask.visibility = View.INVISIBLE
        showSoftKeyboard(detailsInput)
        taskInput.setText(database.taskDao().getTaskText(task))
        taskInput.setOnEditorActionListener { _, _, _ ->
            val taskTitle = taskInput.text.toString()
            database.taskDao().changeTitle(task, taskTitle)
            taskInput.text.clear()
            hideSoftKeyboard(taskInput)
            taskInput.visibility = View.INVISIBLE
            addTask.visibility = View.VISIBLE
            todoAdapter.todoList[index].task = taskTitle
            todoAdapter.notifyItemChanged(index)
            true
        }
    }
    private fun editDetails(task: String, index: Int) {
        detailsInput.visibility = View.VISIBLE
        addTask.visibility = View.INVISIBLE
        detailsInput.setText(database.taskDao().getDetailsText(task))
        showSoftKeyboard(detailsInput)
        detailsInput.setOnEditorActionListener { _, _, _ ->
            val taskDetails = detailsInput.text.toString()
            database.taskDao().changeDetails(task, taskDetails)
            detailsInput.text.clear()
            hideSoftKeyboard(detailsInput)
            detailsInput.visibility = View.INVISIBLE
            addTask.visibility = View.VISIBLE
            todoAdapter.todoList[index] = Task(task, taskDetails)
            todoAdapter.notifyItemChanged(index)
            false
        }
    }

    private fun deleteTask(task: String, index: Int) {
        database.taskDao().deleteTask(task)
        todoAdapter.todoList.removeAt(index)
        todoAdapter.notifyItemRemoved(index)
    }
}

