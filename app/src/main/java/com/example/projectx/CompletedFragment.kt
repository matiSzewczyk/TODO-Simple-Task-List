package com.example.projectx

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_completed.*

class CompletedFragment : Fragment(R.layout.fragment_completed) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val completedAdapter = CompletedAdapter(mutableListOf())
        val database = AppDatabase.getDatabase(requireContext().applicationContext)
        completedAdapter.completedList = database.completedDao().getAll()
        completedList.adapter = completedAdapter
        completedList.layoutManager = LinearLayoutManager(parentFragment?.context)
    }

}