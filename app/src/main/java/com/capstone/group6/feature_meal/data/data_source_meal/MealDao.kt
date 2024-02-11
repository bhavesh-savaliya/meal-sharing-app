package com.capstone.group6.feature_meal.data.data_source_meal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.domain.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    @Query("SELECT * FROM Meal")
    fun getMeals(): Flow<List<Meal>>

    @Query("SELECT * FROM Meal WHERE userId = :id")
    suspend fun getMealById(id: String): Meal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meal: Meal)

    @Delete
    suspend fun delete(meal: Meal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM meal WHERE title LIKE :searchQuery OR description LIKE :searchQuery OR dietarytags LIKE :searchQuery OR ingredients LIKE :searchQuery")
     fun searchQuery(searchQuery: String): Flow<List<Meal>>

}