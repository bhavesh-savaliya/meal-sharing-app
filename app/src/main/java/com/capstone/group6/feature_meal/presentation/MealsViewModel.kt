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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
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

    private fun addMeal(meal: Meal) {
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
            .addSnapshotListener { result, error ->
                if (error != null) {
                    Log.e("TAG", "Listen failed", error)
                    return@addSnapshotListener
                }
                for (document in result!!) {
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
                                it.image = meal.title?.lowercase(Locale.ROOT)
                                    ?.let { it1 -> getImages(it1.trim()) }
                                Log.d("TAG", "readFireStoreData: ${it}")
                                insertUserToRoom(user)
                                if (!isMealAlreadyInRoom(meal)) {
                                    addMeal(meal)
                                }
                            }

                        }

                    }

                }

            }

    }


    private suspend fun getImages(title: String): String? {
        return withContext(Dispatchers.IO) {
            val storageReference = FirebaseStorage.getInstance().reference
            val imageReference = storageReference.child("${removeCharactersAfterSpace(title)}.jpeg")

            try {
                Log.d("TAG", "getImages: ${title}")
                // Fetch the download URL of the image
                val imageUrl = imageReference.downloadUrl.await().toString()
                Log.d("TAG", "getImages: $imageUrl")
                imageUrl
            } catch (e: Exception) {
                Log.d("TAG", "getImages: ${e.message}")
                // Handle any errors that may occur while getting the download URL
                null
            }
        }
    }

    private fun removeCharactersAfterSpace(input: String): String {
        val spaceIndex = input.indexOf(' ')
        return if (spaceIndex != -1) {
            input.substring(0, spaceIndex)
        } else {
            input
        }
    }

    private suspend fun isMealAlreadyInRoom(meal: Meal): Boolean {
        val existingMeal = meal.title?.let { mealRepository.getMealById(it) }
        return existingMeal != null
    }

    private suspend fun insertUserToRoom(user: User) {
        mealRepository.insertUser(user)
    }

    override fun onCleared() {
        disposable?.dispose()
    }
}