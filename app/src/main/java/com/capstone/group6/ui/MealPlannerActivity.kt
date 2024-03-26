package com.capstone.group6.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capstone.group6.Constant.Companion.saveUserToFirestore
import com.capstone.group6.Constant.Companion.showBottomSheetDialog
import com.capstone.group6.Constant.Companion.showDietaryTagsDialog
import com.capstone.group6.Constant.Companion.uploadImage
import com.capstone.group6.MealApp
import com.capstone.group6.R
import com.capstone.group6.databinding.ActivityMealPlanBinding
import com.capstone.group6.feature_meal.domain.model.CuisineType
import com.capstone.group6.feature_meal.domain.model.DietaryTag
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.domain.model.User
import com.capstone.group6.feature_meal.presentation.MealsViewModel
import com.capstone.group6.ui.adapters.IngredientsAdapter
import com.capstone.group6.ui.interfaces.AdapterOnClick
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Random


@AndroidEntryPoint
class MealPlannerActivity : AppCompatActivity(), AdapterOnClick {
    lateinit var binding: ActivityMealPlanBinding
    private val mealsViewModel: MealsViewModel by viewModels()
    val mealTypes =
        arrayOf("Breakfast", "Lunch", "Dinner", "Snack", "Dessert", "Brunch", "Appetizer")
    var selectedMeal = -1
    var imgUri: Uri? = null
    var quantity: Int = 1
    var dietaryTag: String = ""
    var uploadedUri: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealPlanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpCusineType()
        setUpView()
        setIngredients()


    }


    private fun setIngredients() {
        binding.etIngredients.setOnClickListener {
            showBottomSheetDialog(this)
        }
        binding.etDietaryTags.setOnClickListener {
            showDietaryTagsDialog(binding.etDietaryTags, this, onData = {
                dietaryTag = it

            })
        }


        val btnDecrement = binding.btnDecrement
        val btnIncrement = binding.btnIncrement

        quantity = 1

        binding.tvQuantity.text = quantity.toString()

        btnDecrement.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.tvQuantity.text = quantity.toString()
            }
        }

        btnIncrement.setOnClickListener {
            quantity++
            binding.tvQuantity.text = quantity.toString()
        }


    }

    private fun setUpView() {
        binding.back.setOnClickListener { finish() }
        binding.ivCamera.setOnClickListener {
            val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage.launch(pickImg)
        }
        val storage = Firebase.storage
        binding.btnUploadImage.setOnClickListener {
            imgUri?.let {
                uploadImage(it, storage, onSuccess = { downloadUrl ->
                    Log.d("TAG", "Upload successful. Download URL: $downloadUrl")
                    uploadedUri = downloadUrl
                    Toast.makeText(this, "Uploaded Successfully..", Toast.LENGTH_SHORT).show()
                    binding.btnUploadImage.text = "Change Image"
                },
                    onFailure = { exception ->
                        Log.e("TAG", "Upload failed: $exception")
                    }


                )
            }
        }
        val spinnerMealType = binding.spinnerMealType


        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mealTypes)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerMealType.adapter = adapter

        //spinnerMealType.setSelection(0)
        spinnerMealType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedMeal = position
                (parent?.getChildAt(0) as TextView).setTextColor(Color.BLACK)
                (parent.getChildAt(0) as TextView)
                val selectedMealType = mealTypes[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        binding.btnSaveRecipe.setOnClickListener {
            if (binding.etRecipeTitle.text?.isEmpty()!!) {
                // Recipe title is empty, show error
                binding.etRecipeTitle.error = "Recipe title cannot be empty"
                return@setOnClickListener
            }
            if (binding.etDescription.text.isEmpty()) {
                // Description is empty, show error
                binding.etDescription.error = "Description cannot be empty"
                return@setOnClickListener
            }
            val selectedItem = binding.spinnerCuisineType.selectedItem?.toString()
            if (selectedItem.isNullOrEmpty()) {
                val errorText = "Please select an option"
                (binding.spinnerCuisineType.selectedView as? TextView)?.error = errorText
                return@setOnClickListener
            } else {

            }
            save()
        }


    }

    private val changeImage =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                imgUri = data?.data
                binding.ivRecipeImage.setImageURI(imgUri)
                binding.ivCamera.visibility = View.GONE
                binding.imgAlt.visibility = View.GONE
            }
        }

    private fun setUpCusineType() {
        var adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, CuisineType.values())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCuisineType.adapter = adapter
//        binding.spinnerCuisineType.setSelection(0)
        binding.spinnerCuisineType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?, position: Int, id: Long
                ) {
                    (parent.getChildAt(0) as TextView).setTextColor(Color.BLACK)
                    (parent.getChildAt(0) as TextView)
                    val selectedObject = binding.spinnerCuisineType.selectedItem as CuisineType

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun save() {
        var meal: Meal = Meal()
        meal.title = binding.etRecipeTitle.text.toString()

        var isOption1Selected = false
        binding.radioGroupPrivacy.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_private -> {
                    isOption1Selected = true
                    meal.isPrivate = true
                }

                R.id.radio_public -> {
                    isOption1Selected = false
                    meal.isPrivate = false
                }
            }


        }
        meal.description = binding.etDescription.text.toString()
        val selectedCuisine = binding.spinnerCuisineType.selectedItem as CuisineType
        val mealType = binding.spinnerMealType.selectedItem as String
        meal.cuisineType = selectedCuisine
        meal.type = mealType

        val dietary: DietaryTag = when (dietaryTag) {
            "Vegan" -> DietaryTag.Vegan
            "Vegetarian" -> DietaryTag.Vegetarian
            "Dairy-Free" -> DietaryTag.Dairyfree
            "Gluten-Free" -> DietaryTag.Glutenfree
            else -> DietaryTag.Vegetarian
        }
        meal.dietarytags = dietary

        meal.count = binding.tvQuantity.text.toString()


        meal.type = mealTypes[selectedMeal]
        val id: String = "" + System.currentTimeMillis()
        var name = MealApp.prefs1?.isname
        val user = User(name = name)
        meal.userData = user

        meal.image = uploadedUri
        val savedIngredients =
            MealApp.prefs1?.getStringArray("selectedIngredients", arrayListOf())
        val selectedIngredients = savedIngredients
        meal.ingredients = selectedIngredients
        val randomNumber = kotlin.random.Random.nextInt(1, 101)
        meal.likes = randomNumber
        meal.timestamp = System.currentTimeMillis()
        meal.isLocal = true


        lifecycleScope.launch {
            mealsViewModel.mealRepository.insertMeal(meal)
            Log.d("Saved", "save: ${meal}")
            finish()
        }

    }

    override fun onClick(item: ArrayList<String>, position: Int) {
        Log.d("onClick", "onClick: ${item}")
    }

    override fun onClickIng(item: String, bottomSheetDialog: BottomSheetDialog, position: Int) {
        val selectedIngredients =
            MealApp.prefs1?.getStringArray("selectedIngredients", arrayListOf()) ?: emptyList()
        val textToShow = selectedIngredients.distinct().joinToString(", ")
        Log.d("onClickIng", "onClickIng: ${textToShow.lowercase()}")

        binding.etIngredients.text = textToShow.lowercase()

        IngredientsAdapter.selectedIngredients.clear()
    }
}