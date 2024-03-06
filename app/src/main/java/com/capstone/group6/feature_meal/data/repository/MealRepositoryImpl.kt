/**Created By Nirali Pandya
 * Date :2024-01-30
 * Time :3:38 p.m.
 * Project Name :group6
 *
 */
package com.capstone.group6.feature_meal.data.repository

import android.util.Log
import com.capstone.group6.feature_meal.data.data_source_meal.MealDao
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.domain.model.User

import com.capstone.group6.feature_meal.domain.repository.MealRepository
import com.capstone.group6.feature_meal.domain.util.MealOrder

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class MealRepositoryImpl(private val mealDao: MealDao) : MealRepository {

    override fun getMeals(isLocal: Boolean): Flow<List<Meal>> {
        return mealDao.getMeals(isLocal)
    }

    override suspend fun getMealById(id: String): Meal? {
        return mealDao.getMealById(id)
    }

    override suspend fun insertMeal(meal: Meal) {
        return mealDao.insert(meal)
    }

    override suspend fun delete(meal: Meal) {
        return mealDao.delete(meal)
    }

    override suspend fun insertUser(user: User) {
        return mealDao.insertUser(user)
    }

    override fun searchQuery(searchQuery: String): Flow<List<Meal>> {
        return mealDao.searchQuery(searchQuery)
    }
    override fun filterMeals(
        mealType: String,
        ingredients: String,
        isVegetarian: Boolean,
        isVegan: Boolean,
        isDairy: Boolean,
        isGluten: Boolean,
        isLocal: Boolean
    ): Flow<List<Meal>> {
        return if (mealType.isNotBlank() && ingredients.isNotBlank()) {
            mealDao.getMealsByTypeAndIngredients(mealType, ingredients.lowercase())
        } else if (mealType.isNotBlank()) {
            Log.d("filterMeals", "filterMeals: $mealType")
            mealDao.getMealsByType(mealType)
        } else if (isVegan || isVegetarian || isDairy || isGluten) {
            mealDao.getFilteredMeals(isVegetarian, isVegan, isDairy, isGluten)
        } else if (ingredients.isNotBlank()) {
            mealDao.getMealsByIngredients(ingredients.lowercase())
        } else {
            mealDao.getMeals(isLocal)
        }
    }


}