package com.capstone.group6.di

import android.content.Context
import androidx.room.Room
import com.capstone.group6.feature_meal.data.data_source_meal.MealDatabase
import com.capstone.group6.feature_meal.data.repository.MealRepositoryImpl
import com.capstone.group6.feature_meal.domain.repository.MealRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideMealDataBase(@ApplicationContext applicationContext: Context): MealDatabase {
        return Room.databaseBuilder(
            applicationContext,
            MealDatabase::class.java,
            MealDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideMealRepository(db: MealDatabase): MealRepository {
        return MealRepositoryImpl(db.mealDao)
    }






}