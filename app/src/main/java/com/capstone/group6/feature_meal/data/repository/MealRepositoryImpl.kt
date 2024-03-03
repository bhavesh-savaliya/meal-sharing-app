/**Created By Nirali Pandya
 * Date :2024-01-30
 * Time :3:38 p.m.
 * Project Name :group6
 *
 */
package com.capstone.group6.feature_meal.data.repository

import com.capstone.group6.feature_meal.data.data_source_meal.MealDao
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.domain.model.User
import com.capstone.group6.feature_meal.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow

class MealRepositoryImpl(private val mealDao: MealDao) : MealRepository {

    override fun getMeals(isLocal:Boolean): Flow<List<Meal>> {
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

    override  fun searchQuery(searchQuery: String): Flow<List<Meal>> {
        return mealDao.searchQuery(searchQuery)
    }
}