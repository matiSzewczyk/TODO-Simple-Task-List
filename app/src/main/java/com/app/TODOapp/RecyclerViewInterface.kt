package com.app.TODOapp

import android.view.View

interface RecyclerViewInterface {
    fun myLongClickListener(position: Int, view: View?)
    fun myClickListener(position: Int, view: View?)
}