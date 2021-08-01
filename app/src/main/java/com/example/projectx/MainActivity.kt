package com.example.projectx

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val taskFragment = TaskFragment()
        val completedFragment = CompletedFragment()
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
            replace(R.id.flFragment, fragment)
            addToBackStack(null)
            commit()
        }
}