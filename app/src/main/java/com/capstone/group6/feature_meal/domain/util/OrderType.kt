package com.capstone.group6.feature_meal.domain.util

sealed class OrderType {
    object Ascending : OrderType()
    object Descending : OrderType()
}
