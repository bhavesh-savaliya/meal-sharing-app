/**Created By Nirali Pandya
 * Date :2024-01-30
 * Time :3:36 p.m.
 * Project Name :group6
 *
 */
package com.capstone.group6.feature_meal.data.data_source_meal

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.domain.model.User

@Database(entities = [Meal::class,User::class], version = 1)
@TypeConverters(MealConverters::class,MealListConverters::class,ArrayListConverter::class)
abstract  class MealDatabase :RoomDatabase() {

    abstract val mealDao:MealDao

    companion object{
        const val  DATABASE_NAME ="meals_db"
    }

}