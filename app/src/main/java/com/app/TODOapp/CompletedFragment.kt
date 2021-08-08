package com.app.TODOapp

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_completed.*

class CompletedFragment : Fragment(R.layout.fragment_completed) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val completedAdapter = CompletedAdapter(requireContext(), mutableListOf())
        val database = AppDatabase.getDatabase(requireContext().applicationContext)

        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)

        database.completedDao().setAllToChecked()
        completedAdapter.completedList = database.completedDao().getAll()

        completedList.adapter = completedAdapter
        completedList.layoutManager = LinearLayoutManager(parentFragment?.context)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val database = AppDatabase.getDatabase(requireContext())
        val completedAdapter = CompletedAdapter(requireContext(), mutableListOf())
        return when (item.itemId) {
            R.id.settings_all -> {
                completedAdapter.deleteAll()
                true
            }
            R.id.settings_selected -> {
                database.completedDao().deleteSelected()
                true
            }
            R.id.settings_move_back -> {
                completedAdapter.moveToTaskList()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}