/**Created By Nirali Pandya
 * Date :2024-01-20
 * Time :4:25 p.m.
 * Project Name :MVVMNewsApp
 *
 */
package com.capstone.group6.feature_meal.data.data_source_meal

import androidx.room.TypeConverter
import com.capstone.group6.feature_meal.domain.model.User
import com.google.gson.Gson

class MealListConverters {

    @TypeConverter
    fun fromString(value: String?): User? {
        return if (value != null) {
            Gson().fromJson(value, User::class.java)
        } else {
            null
        }
    }

    @TypeConverter
    fun fromList(user: User?): String? {
        return if (user != null) {
            Gson().toJson(user)
        } else {
            null
        }
    }
}
