package com.capstone.group6.ui

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.capstone.group6.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import com.capstone.group6.Constant
import com.capstone.group6.Constant.Companion.startActivity
import com.capstone.group6.R
import com.capstone.group6.feature_meal.domain.repository.MealRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    @Inject
    lateinit var repository: MealRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigationSetUp()
        bottomNavigationClick()

    }

    private fun navigationSetUp() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.mainContainer.id) as NavHostFragment
        navController = navHostFragment.navController;
        setupWithNavController(binding.bottomNavigationView, navController)
    }

    private fun bottomNavigationClick() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.userFragment -> startActivity(MealPlannerActivity::class.java)
            }
            true
        }
    }


}



