package com.capstone.group6.feature_meal.presentation.util

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.LocaleList
import java.util.*

class LanguageContextWrapper(base: Context) : ContextWrapper(base) {
    companion object {
        fun wrap(context: Context, languageCode: String): ContextWrapper {
            val configuration = context.resources.configuration
            val locale = Locale(languageCode)
            Locale.setDefault(locale)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.setLocale(locale)
                val localeList = LocaleList(locale)
                LocaleList.setDefault(localeList)
                configuration.setLocales(localeList)
            } else {
                configuration.locale = locale
            }

            val updatedContext = context.createConfigurationContext(configuration)
            return LanguageContextWrapper(updatedContext)
        }
    }
}
