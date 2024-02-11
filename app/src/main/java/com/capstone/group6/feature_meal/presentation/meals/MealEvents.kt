/**Created By Nirali Pandya
 * Date :2024-01-30
 * Time :3:20 p.m.
 * Project Name :group6
 *
 */
package com.capstone.group6.feature_meal.presentation.meals

import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.domain.util.MealOrder

sealed class MealEvents {
    data class Order(val mealOrder:MealOrder) :MealEvents()
    data class DeleteMeal(val meal: Meal) :MealEvents()
    object ToggleOrderSelection : MealEvents()
}