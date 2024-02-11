package com.capstone.group6.feature_meal.domain.repository

import android.icu.text.CaseMap.Title
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.domain.model.User
import kotlinx.coroutines.flow.Flow

interface MealRepository {

    fun getMeals(): Flow<List<Meal>>

    suspend fun getMealById(id: String): Meal?

    suspend fun insertMeal(meal: Meal)

    suspend fun delete(meal: Meal)

    suspend fun insertUser(user: User)

     fun searchQuery(title: String):Flow<List<Meal>>
}