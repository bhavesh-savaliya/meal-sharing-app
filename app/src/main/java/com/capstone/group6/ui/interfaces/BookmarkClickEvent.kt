
package com.capstone.group6.ui.interfaces

import com.capstone.group6.feature_meal.domain.model.Meal
import com.google.android.material.bottomsheet.BottomSheetDialog

interface BookmarkClickEvent {
    fun onBookMarkSaved(position:Int,feed:Meal)
    fun feedShare(feed: Meal)
}