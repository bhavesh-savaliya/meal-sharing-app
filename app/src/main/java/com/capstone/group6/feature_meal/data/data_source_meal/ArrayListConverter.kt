/**Created By Nirali Pandya
 * Date :2024-03-04
 * Time :9:29 p.m.
 * Project Name :meal-sharing-app
 *
 */
package com.capstone.group6.feature_meal.data.data_source_meal

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class ArrayListConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromString(value: String?): List<String>? {
        if (value == null) {
            return null
        }
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(value: List<String>?): String? {
        return gson.toJson(value)
    }
}