package com.capstone.group6.feature_meal.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.lang.Exception

@Entity
data class Meal(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val access: Boolean,
    var userId: String? = null,
    var user: User?=null,
    val title: String? = null,
    var image:String?=null,
    val description: String,
    val cuisineType: CuisineType = CuisineType.GUJARATI,
    val ingredients: String? = null,
    val dietarytags: DietaryTag = DietaryTag.VEGETARIAN,
    val likes: Long? = null,
    val count: Int? = 0,
    val quantity: Int? = 0,
    val timestamp: Long

) {
    constructor() : this(
        null,
        false,
        null,
        null,
        null,
        null,
        "",
        CuisineType.GUJARATI,
        null,
        DietaryTag.VEGETARIAN,
        null,
        0,
        0,
        0
    )
}



enum class CuisineType {
    ITALIAN,
    MEXICAN,
    CHINESE,
    GUJARATI,
    PUNJABI,
    SOUTHINDIAN,
}

enum class DietaryTag {
    VEGETARIAN,
    VEGAN,
    GLUTEN_FREE,
    DAIRY_FREE,

}

class InvalidMealException(msg: String) : Exception(msg)

data class Location(val latitude: Double, val longitude: Double)
