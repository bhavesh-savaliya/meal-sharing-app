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
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.domain.model.User


@Database(entities = [Meal::class,User::class], version = 2)
@TypeConverters(MealConverters::class,MealListConverters::class,ArrayListConverter::class,ReferenceConverter::class)
abstract  class MealDatabase :RoomDatabase() {

    abstract val mealDao:MealDao

    companion object{
        const val  DATABASE_NAME ="meals_db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Meal ADD COLUMN isLike INTEGER NOT NULL DEFAULT 0")
            }
        }
    }

}