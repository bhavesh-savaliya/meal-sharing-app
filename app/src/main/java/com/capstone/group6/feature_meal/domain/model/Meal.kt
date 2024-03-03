package com.capstone.group6.feature_meal.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.lang.Exception

@Entity
data class Meal(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val access: Boolean,
//    var userId: String? = null,
    var user: User?=null,
    var title: String? = null,
    var image:String?=null,
    var description: String,
    var cuisineType: CuisineType =CuisineType.Gujarati,
    var ingredients: String? = null,
    var dietarytags: DietaryTag = DietaryTag.Vegetarian,
    var likes: Long? = null,
    var count: String = "",
    var quantity: Int? = 0,
    var timestamp: Long,
    var mealType:String,
    var isPrivate:Boolean=false,
    var isLocal :Boolean

) {
    constructor() : this(
        null,
        false,

        null,
        null,
        null,
        "",
        CuisineType.Gujarati,
        null,
        DietaryTag.Vegetarian,
        null,
        "0",
        0,
        0,
        "",
        false,true
    )
}



    enum class CuisineType {
        Italian,
        Mexican,
        Chinese,
        Gujarati,
        Punjabi,
        SouthIndian,
    }

enum class DietaryTag {
    Vegetarian,
    Vegan,
    Glutenfree,
    Dairyfree,

}

class InvalidMealException(msg: String) : Exception(msg)

data class Location(val latitude: Double, val longitude: Double)
