package com.capstone.group6.feature_meal.domain.util

import android.content.Context
import android.content.SharedPreferences

class Prefs (context: Context)
{
    private var APP_PREF_INT_EXAMPLE = "com.capstone.group6";
    private var IsLanguageShown= "IsLanguageShown";
    private var isName="userName"
    private var IntroScreen= "IntroScreen";

    private val preferences: SharedPreferences = context.getSharedPreferences(APP_PREF_INT_EXAMPLE,Context.MODE_PRIVATE)

    var isIntroShown: Boolean
        get() = preferences.getBoolean(IntroScreen, false)
        set(value) = preferences.edit().putBoolean(IntroScreen, value).apply()

    var isLangugaeShown: Boolean
        get() = preferences.getBoolean(IsLanguageShown, false)
        set(value) = preferences.edit().putBoolean(IsLanguageShown, value).apply()

    var isname: String?
        get() = preferences.getString(isName, "")
        set(value) = preferences.edit().putString(isName, value).apply()

    var languageCode: String?
        get() = preferences.getString("languageCode", "en")
        set(value) = preferences.edit().putString("languageCode", value!!).apply()

    var outputlanguageCode: String?
        get() = preferences.getString("outputlanguageCode", "en")
        set(value) = preferences.edit().putString("outputlanguageCode", value!!).apply()

    var position: Int?
        get() = preferences.getInt("position", 0)
        set(value) = preferences.edit().putInt("position", value!!).apply()

    var positionOutput: Int?
    get() = preferences.getInt("positionOutput", 0)
    set(value) = preferences.edit().putInt("positionOutput", value!!).apply()

    var positionTone: Int?
        get() = preferences.getInt("positionTone", 0)
        set(value) = preferences.edit().putInt("positionTone", value!!).apply()

    var theme: String?
        get() = preferences.getString("theme", "light")
        set(value) = preferences.edit().putString("theme", value!!).apply()

    var size: Int?
        get() = preferences.getInt("size", 50)
        set(value) = preferences.edit().putInt("size", value!!).apply()

    var ingredient: String?
        get() = preferences.getString("ingredient", "Lemon")
        set(value) = preferences.edit().putString("ingredient", value!!).apply()

}