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


class MealConverters {

    @TypeConverter
    fun fromCuisineType(value: CuisineType): String {
        return value.name
    }

    @TypeConverter
    fun toCuisineType(value: String): CuisineType {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromDietaryTag(value: DietaryTag): String {
        return value.name
    }

    @TypeConverter
    fun toDietaryTag(value: String): DietaryTag {
        return enumValueOf(value)
    }
}