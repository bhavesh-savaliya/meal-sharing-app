package com.capstone.group6.feature_meal.domain.util

sealed class MealOrder(val orderType: OrderType) {

//    class ItemCount(orderType: OrderType) : MealOrder(orderType)
//    class OrderQuantity(orderType: OrderType) : MealOrder(orderType)
    class Time(orderType: OrderType) : MealOrder(orderType)
//    class Location(orderType: OrderType) : MealOrder(orderType)
}
