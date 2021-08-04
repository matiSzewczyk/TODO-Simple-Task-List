package com.example.projectx

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    val taskFragment = TaskFragment()
    val completedFragment = CompletedFragment()
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
            else -> super.onOptionsItemSelected(item)
        }
    }
}