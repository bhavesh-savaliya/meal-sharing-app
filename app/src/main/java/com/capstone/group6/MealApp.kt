package com.capstone.group6

import android.app.Application
import android.app.UiModeManager
import androidx.appcompat.app.AppCompatDelegate
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
        var theme = prefs1!!.theme
        val uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
        val currentNightMode = uiModeManager.nightMode
        if (currentNightMode == UiModeManager.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            // setTheme(R.style.AppThemeNight);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            //setTheme(R.style.AppTheme);
        }

        if (theme.equals("dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);


        } else if (theme.equals("light")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }
    }
}