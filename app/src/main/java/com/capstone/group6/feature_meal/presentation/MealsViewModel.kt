package com.capstone.group6.feature_meal.presentation


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.group6.MealApp
import com.capstone.group6.feature_meal.domain.model.CuisineType
import com.capstone.group6.feature_meal.domain.model.DietaryTag
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.domain.model.User
import com.capstone.group6.feature_meal.domain.repository.MealRepository
import com.capstone.group6.feature_meal.domain.util.MealOrder
import com.capstone.group6.feature_meal.domain.util.OrderType
import com.capstone.group6.feature_meal.presentation.meals.MealEvents
import com.capstone.group6.feature_meal.presentation.meals.MealState
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealsViewModel @Inject constructor(
    val app: Application,
    val mealRepository: MealRepository
) : AndroidViewModel(app) {

    private var disposable: Disposable? = null

    private val _state = MutableStateFlow(MealState())
    val state: StateFlow<MealState> = _state

    private var recentlyDeleted: Meal? = null

    private var getNoteJob: Job? = null


    fun onEvent(event: MealEvents) {
        when (event) {
            is MealEvents.Order -> {

                if (state.value.mealOrder::class == event.mealOrder::class &&
                    state.value.mealOrder.orderType == event.mealOrder.orderType
                ) {
                    return
                }
                getMeals(event.mealOrder)
            }

            is MealEvents.DeleteMeal -> {
                viewModelScope.launch {
                    mealRepository.delete(event.meal)
                    recentlyDeleted = event.meal
                }
            }

//            is MealEvents.RestoteMeal -> {
//                val mealToAdd = event.meal
//                viewModelScope.launch {
//
//                    mealRepository.insertMeal(recentlyDeleted ?: return@launch)
//                    recentlyDeleted = null
//                }
//            }

            is MealEvents.ToggleOrderSelection -> {
                val currentState = state.value
                val updatedState = MealState(
                    meals = currentState.meals,
                    mealOrder = currentState.mealOrder,
                    isOrderSelectionVisible = !currentState.isOrderSelectionVisible
                )
                _state.value = updatedState
            }
        }
    }

    private fun getMeals(mealOrder: MealOrder) {
        getNoteJob?.cancel()
        mealRepository.getMeals().onEach { meal ->
            val updatedValue = MealState(
                meals = meal,
                mealOrder = mealOrder
            )
            _state.value = updatedValue
        }
            .launchIn(viewModelScope)

    }

    fun addMeal(meal: Meal) {
        viewModelScope.launch {
            mealRepository.insertMeal(meal)
        }
    }

    fun fetchMeals(): Flow<List<Meal>> {
        return mealRepository.getMeals()

    }

    fun searchMeals(searchQuery: String): Flow<List<Meal>> {
        return mealRepository.searchQuery(searchQuery)
    }

    fun readFireStoreData() {
        Firebase.firestore.collection("meals")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val mealData = document.data

                    val userRef = mealData["User"] as? DocumentReference
                    userRef?.get()?.addOnSuccessListener { userSnapshot ->
                        val id = userSnapshot.id
                        val userRefPath = mealData["User"] as? String
                        val name = userSnapshot["Name"] as? String
                        val user = User(name = name, userId = id)


                        var meal = document.toObject(Meal::class.java)
                        meal?.let {
                            // Add userId manually
                            it.userId = id
                            it.user = user
                            viewModelScope.launch {
                                insertUserToRoom(user)
                                if (!isMealAlreadyInRoom(meal)) {
                                    addMeal(meal)
                                }
                            }

                        }

                    }

                }

            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents.", exception)
            }
    }

    private suspend fun isMealAlreadyInRoom(meal: Meal): Boolean {
        val existingMeal = meal.userId?.let { mealRepository.getMealById(it) }
        return existingMeal != null
    }

    suspend fun insertUserToRoom(user: User) {
        mealRepository.insertUser(user)
    }

    override fun onCleared() {
        disposable?.dispose()
    }
}