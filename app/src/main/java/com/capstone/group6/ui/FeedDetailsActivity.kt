package com.capstone.group6.ui


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.capstone.group6.R
import com.capstone.group6.databinding.ActivityFeedDetailsBinding
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.presentation.MealsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedDetailsBinding
    private val mealsViewModel: MealsViewModel by viewModels()
    lateinit var feed: Meal

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }

        val position = intent.getIntExtra("position", -1)
        setUpData(position)
    }


    private fun setUpData(position: Int) {
        lifecycleScope.launch {
            mealsViewModel.mealRepository.getMeals(true).collect { meals ->

                feed = meals[position]
                setUiData()

            }
        }
    }

    private fun setUiData() {
        Glide.with(this).load(feed.image).placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder).into(binding.ivFeedImage)

        binding.tvTitle.text = feed.title
        binding.tvDescription.text = feed.description
        binding.tvCuisineType.text = "Cuisine Type : " + feed.cuisineType.name.lowercase()
        binding.tvDietaryType.text = "Dietary Tags : " + feed.dietarytags.name.lowercase()
        binding.totalLikes.text = feed.likes.toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle back button click
        if (item.itemId == android.R.id.home) {
            onBackPressed()

            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}