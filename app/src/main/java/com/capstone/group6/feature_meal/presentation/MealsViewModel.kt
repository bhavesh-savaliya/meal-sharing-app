package com.capstone.group6.feature_meal.presentation


import android.R.string
import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.group6.Constant.Companion.saveUserToFirestore
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.domain.model.User
import com.capstone.group6.feature_meal.domain.repository.MealRepository
import com.capstone.group6.feature_meal.presentation.meals.MealState
import com.capstone.group6.ui.MainActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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

    fun fetchFavMeals(isLike: Boolean): Flow<List<Meal>> {
        return mealRepository.getFav(isLike)
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
                    var user: User? = null
                    val userId = mealData["user"] as? String
                    Log.d("TAG", "UserId: ${userId}")

                    if (userId != null) {
                        Firebase.firestore.collection("users").document(userId).get()
                            .addOnSuccessListener { userDocument ->
                                if (userDocument.exists()) {
                                    val name = userDocument.getString("name")
                                    user = User(name = name)
                                    Log.d("TAG", "User: $user")
                                    var meal = document.toObject(Meal::class.java)
                                    meal.isLocal = false



                                    viewModelScope.launch {
                                        meal.userData = user
//                                        updateData(meal)

                                        meal.image = meal.title?.lowercase(Locale.ROOT)
                                            ?.let { it1 -> getImages(it1.trim()) }

                                        user?.let { it1 -> insertUserToRoom(it1) }
                                        if (!isMealAlreadyInRoom(meal)) {
                                            addMeal(meal)
                                        }
                                    }

                                } else {
                                    Log.e("TAG", "User document does not exist")
                                }
                            }


                    }
                }

            }

    }


    fun writeData(activity: MainActivity, meal: Meal) {
        activity.saveUserToFirestore(meal.userData!!) { userDocumentPath ->
            val path: List<String> = userDocumentPath.split("/")
            meal.user =path[1]
            Log.d("TAG", "writeData: ${path[1]}")
            updateData(meal)

            Firebase.firestore.collection("meals")
                .add(meal)
                .addOnSuccessListener { documentReference ->
                    Log.d("TAG", "DocumentSnapshot added with ID: ${documentReference.id}")
                    Toast.makeText(activity, "Data Successfully Shared..", Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding document", e)
                }
        }


    }

    fun updateData(feed: Meal) {
        viewModelScope.launch {
            updateMealToRoom(feed)
        }
    }

    private suspend fun getImages(title: String): String? {
        return withContext(Dispatchers.IO) {
            val storageReference = FirebaseStorage.getInstance().reference
            val imageReference =
                storageReference.child("${removeCharactersAfterSpace(title.lowercase())}.jpeg")

            try {
                // Fetch the download URL of the image
                val imageUrl = imageReference.downloadUrl.await().toString()
                imageUrl
            } catch (e: Exception) {
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

    private suspend fun updateMealToRoom(meal: Meal) {
        mealRepository.update(meal)
    }

    override fun onCleared() {
        disposable?.dispose()
    }

}