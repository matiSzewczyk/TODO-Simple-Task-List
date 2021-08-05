package com.example.projectx

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_completed.*

class CompletedFragment : Fragment(R.layout.fragment_completed) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val completedAdapter = CompletedAdapter(requireContext(), mutableListOf())
        val database = AppDatabase.getDatabase(requireContext().applicationContext)
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)

        database.completedDao().setAllToChecked()
        completedAdapter.completedList = database.completedDao().getAll()

        completedList.adapter = completedAdapter
        completedList.layoutManager = LinearLayoutManager(parentFragment?.context)
    }

}