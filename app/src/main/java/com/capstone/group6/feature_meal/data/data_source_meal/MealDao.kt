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

    @Query("SELECT * FROM Meal WHERE isLocal =:isLocal")
    fun getMeals(isLocal: Boolean): Flow<List<Meal>>

    @Query("SELECT * FROM Meal WHERE title = :id")
    suspend fun getMealById(id: String): Meal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meal: Meal)

    @Delete
    suspend fun delete(meal: Meal)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM meal WHERE title LIKE :searchQuery OR description LIKE :searchQuery OR dietarytags LIKE :searchQuery OR ingredients LIKE :searchQuery")
     fun searchQuery(searchQuery: String): Flow<List<Meal>>


    @Query("SELECT * FROM Meal WHERE Type = :mealType")
    fun getMealsByMealType(mealType: String): Flow<List<Meal>>

    @Query("SELECT * FROM Meal WHERE ingredients LIKE '%' || :ingredients || '%'")
    fun getMealsByIngredients(ingredients: String): Flow<List<Meal>>

    @Query("SELECT * FROM Meal WHERE dietaryTags LIKE '%' || :dietaryTags || '%'")
    fun getMealsByDietaryTags(dietaryTags: String): Flow<List<Meal>>

    @Query("SELECT * FROM Meal WHERE Type = :mealType OR ingredients= :ingredient IN (SELECT ingredients FROM Meal)")
    fun getMealsByTypeAndIngredients(mealType: String, ingredient: String): Flow<List<Meal>>

    @Query("SELECT * FROM Meal WHERE Type = :mealType")
    fun getMealsByType(mealType: String): Flow<List<Meal>>



    @Query("SELECT * FROM Meal WHERE (dietarytags = :vegetarian OR :vegetarian = 0) " +
            "OR (dietarytags = :vegan OR :vegan = 0) " +
            "OR (dietarytags = :glutenFree OR :glutenFree = 0) " +
            "OR (dietarytags = :dairyFree OR :dairyFree = 0)")
    fun getFilteredMeals(
        vegetarian: Boolean,
        vegan: Boolean,
        glutenFree: Boolean,
        dairyFree: Boolean
    ): Flow<List<Meal>>

}