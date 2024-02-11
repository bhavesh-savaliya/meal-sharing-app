package com.capstone.group6

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MealApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}