package com.capstone.group6.feature_meal.data.data_source_meal

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.domain.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    @Query("SELECT * FROM Meal WHERE isLocal =:isLocal")
    fun getMeals(isLocal: Boolean): Flow<List<Meal>>

    @Query("SELECT * FROM Meal WHERE isLike =:isLike")
    fun getFav(isLike: Boolean): Flow<List<Meal>>

    @Query("SELECT * FROM Meal WHERE title = :id")
    suspend fun getMealById(id: String): Meal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meal: Meal)

    @Delete
    suspend fun delete(meal: Meal)

    @Update
    suspend fun update(meal: Meal)

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

    @Query("SELECT * FROM Meal WHERE LOWER(ingredients) LIKE '%' || LOWER(:ingredient) || '%'")
    fun getMealsByTypeAndIngredients(ingredient: String): Flow<List<Meal>>


    @Query("SELECT * FROM Meal WHERE Type = :mealType")
    fun getMealsByType(mealType: String): Flow<List<Meal>>


    @Query(
        "SELECT * FROM Meal WHERE " +
                "(:vegan = 1 AND dietarytags = 'Vegan') OR " +
                "(:vegetarian = 1 AND dietarytags = 'Vegetarian') OR " +
                "(:glutenFree = 1 AND dietarytags = 'Glutenfree') OR " +
                "(:dairyFree = 1 AND dietarytags = 'Dairyfree')"
    )
    fun getFilteredMeals(
        vegetarian: Boolean,
        vegan: Boolean,
        glutenFree: Boolean,
        dairyFree: Boolean
    ): Flow<List<Meal>>

}