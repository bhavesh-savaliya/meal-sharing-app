package com.capstone.group6.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.capstone.group6.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import com.capstone.group6.Constant
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.domain.repository.MealRepository
import com.capstone.group6.feature_meal.presentation.MealsViewModel

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val mealsViewModel:  MealsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigationSetUp()
    }

    private fun navigationSetUp() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.mainContainer.id) as NavHostFragment
        navController = navHostFragment.navController;
        setupWithNavController(binding.bottomNavigationView, navController)
    }

    fun update(meal: Meal) {
        mealsViewModel.updateData(meal)
    }

    fun feedShare(feed: Meal) {
        mealsViewModel.writeData(this,feed)

    }
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }

}



