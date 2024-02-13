package com.capstone.group6

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.capstone.group6.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.capstone.group6.feature_meal.domain.repository.MealRepository
import com.capstone.group6.ui.fragments.FeedFragment
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

        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.mainContainer.id) as NavHostFragment
        navController = navHostFragment.navController;
        setupWithNavController(binding.bottomNavigationView, navController)

    }




}



