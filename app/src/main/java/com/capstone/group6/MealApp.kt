package com.capstone.group6

import android.app.Application
import com.capstone.group6.feature_meal.domain.util.Prefs
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MealApp : Application() {

    val prefs: Prefs by lazy {
        prefs1!!
    }
    companion object {
        var prefs1: Prefs? = null
        lateinit var instance: MealApp


    }
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        prefs1 = Prefs(applicationContext)
        instance = this
    }
}