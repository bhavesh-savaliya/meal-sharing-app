package com.capstone.group6.feature_meal.presentation


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.domain.model.User
import com.capstone.group6.feature_meal.domain.repository.MealRepository
import com.capstone.group6.feature_meal.presentation.meals.MealState
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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


    fun filterMeals(
        mealType: String,
        ingredients: String,
        isVegetarian: Boolean,
        isVegan: Boolean,
        isDairy: Boolean,
        isGluten: Boolean,
        isLocal: Boolean
    ): Flow<List<Meal>> {
        return mealRepository.filterMeals(
            mealType,
            ingredients,
            isVegetarian,
            isVegan,
            isDairy,
            isGluten,
            isLocal
        )
    }



    private fun addMeal(meal: Meal) {
        viewModelScope.launch {
            mealRepository.insertMeal(meal)
        }
    }

    fun fetchMeals(isLocal: Boolean): Flow<List<Meal>> {
        return mealRepository.getMeals(isLocal)

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
                        meal.isLocal =false
                        meal?.let {
                            // Add userId manually
//                            it.userId = id
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
            val imageReference =
                storageReference.child("${removeCharactersAfterSpace(title.lowercase())}.jpeg")

            try {
                Log.d("TAG", "getImages: ${imageReference}")
                // Fetch the download URL of the image
                val imageUrl = imageReference.downloadUrl.await().toString()
                Log.d("TAG", "getImages: $imageUrl")
                imageUrl
            } catch (e: Exception) {
                Log.d("TAG", "getImages: ${e.message}")
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