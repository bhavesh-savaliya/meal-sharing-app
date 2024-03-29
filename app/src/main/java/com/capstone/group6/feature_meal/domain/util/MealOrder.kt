package com.capstone.group6.feature_meal.domain.util

sealed class MealOrder(val orderType: OrderType) {

    class Time(orderType: OrderType) : MealOrder(orderType)
    class MealType(orderType: OrderType) :MealOrder(orderType)
    class Ingredients(orderType: OrderType) : MealOrder(orderType)
    class Dietary(orderType: OrderType) : MealOrder(orderType)
}
