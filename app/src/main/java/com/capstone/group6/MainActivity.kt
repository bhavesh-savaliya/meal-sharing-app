package com.capstone.group6

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.capstone.group6.databinding.ActivityMainBinding
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capstone.group6.feature_meal.domain.repository.MealRepository
import com.capstone.group6.feature_meal.presentation.MealsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val TAG: String = "MainActivity"
    private val mealsViewModel by viewModels<MealsViewModel>()

    @Inject
    lateinit var repository: MealRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.mainContainer.id) as NavHostFragment
        navController = navHostFragment.navController;

        setupWithNavController(binding.bottomNavigationView, navController)

        mealsViewModel.readFireStoreData()

        lifecycleScope.launch {
            mealsViewModel.fetchMeals().collect { meals ->
                if (meals.size > 1)
                    Log.d(TAG, "I am getting all data here: ${meals[1].user?.name}")

            }
        }
    }


}