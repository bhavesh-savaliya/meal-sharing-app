/**Created By Nirali Pandya
 * Date :2024-01-20
 * Time :4:25 p.m.
 * Project Name :MVVMNewsApp
 *
 */
package com.capstone.group6.feature_meal.data.data_source_meal

import androidx.room.TypeConverter
import com.capstone.group6.feature_meal.domain.model.CuisineType
import com.capstone.group6.feature_meal.domain.model.DietaryTag
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.domain.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MealListConverters {

    @TypeConverter
    fun fromString(value: String): User {
        val listType = object : TypeToken<User>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(user: User): String {
        return Gson().toJson(user)
    }
}