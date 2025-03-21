package com.example.teacher_assistant

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navController = findNavController(R.id.fragmentContainerView)

        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.scheduleFragment,
            R.id.lessonsFragment,
            R.id.studentsFragment,
            R.id.dataFragment
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)

        bottomNavigationView.setupWithNavController(navController)

        // Handle reselection
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.scheduleFragment -> {
                    navController.navigate(R.id.scheduleFragment, null,
                        NavOptions.Builder().setPopUpTo(R.id.scheduleFragment, inclusive = true).build()
                    )
                    true
                }
                R.id.lessonsFragment -> {
                    navController.navigate(R.id.lessonsFragment, null,
                        NavOptions.Builder().setPopUpTo(R.id.lessonsFragment, inclusive = true).build()
                    )
                    true
                }
                R.id.studentsFragment -> {
                    navController.navigate(R.id.studentsFragment, null,
                        NavOptions.Builder().setPopUpTo(R.id.studentsFragment, inclusive = true).build()
                    )
                    true
                }
                R.id.dataFragment -> {
                    navController.navigate(R.id.dataFragment, null,
                        NavOptions.Builder().setPopUpTo(R.id.dataFragment, inclusive = true).build()
                    )
                    true
                }
                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
