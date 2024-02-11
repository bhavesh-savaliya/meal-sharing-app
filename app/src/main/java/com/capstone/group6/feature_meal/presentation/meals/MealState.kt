/**Created By Nirali Pandya
 * Date :2024-01-30
 * Time :3:20 p.m.
 * Project Name :group6
 *
 */
package com.capstone.group6.feature_meal.presentation.meals

import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.domain.util.MealOrder
import com.capstone.group6.feature_meal.domain.util.OrderType

class MealState(
    val meals: List<Meal> = emptyList(),
    val mealOrder: MealOrder = MealOrder.Time(OrderType.Descending),
    val isOrderSelectionVisible: Boolean = false
)