package com.example.TODOapp

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_task.*

class MainActivity : AppCompatActivity() {
    private val taskFragment = TaskFragment()
    private val completedFragment = CompletedFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Not using the function to avoid adding the very first instance to the backStack
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, taskFragment)
            commit()
        }

        // set the on menu item click
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.tasks -> {
                    setCurrentFragment(taskFragment)
                    true
                }
                R.id.completed -> {
                    setCurrentFragment(completedFragment)
                    true
                }
                else -> false
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_SYSTEM_NAVIGATION_DOWN) {

            taskInput.visibility = View.INVISIBLE
            Toast.makeText(applicationContext, "hello :)", Toast.LENGTH_SHORT).show()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        taskInput.visibility = View.INVISIBLE
        Toast.makeText(applicationContext, "hello :)", Toast.LENGTH_SHORT).show()
    }
//    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        if (ev?.action == MotionEvent.ACTION_DOWN) {
//            val view: View? = currentFocus
//            if (view is EditText || view is Button) {
//                val outRect = Rect()
//                view.getGlobalVisibleRect(outRect)
//                if (!outRect.contains(ev.rawX.toInt(), ev.rawX.toInt())) {
//                    view.clearFocus()
//                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    imm.hideSoftInputFromWindow(view.windowToken, 0)
//                }
//            }
//        }
//        return super.dispatchTouchEvent(ev)
//    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out,
            )
            replace(R.id.flFragment, fragment)
            addToBackStack(null)
            commit()
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val database = AppDatabase.getDatabase(applicationContext)
        val completedAdapter = CompletedAdapter(applicationContext, mutableListOf())
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