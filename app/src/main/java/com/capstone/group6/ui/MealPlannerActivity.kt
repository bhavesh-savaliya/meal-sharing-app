package com.capstone.group6.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capstone.group6.Constant.Companion.showBottomSheetDialog
import com.capstone.group6.Constant.Companion.showDietaryTagsDialog
import com.capstone.group6.MealApp
import com.capstone.group6.R
import com.capstone.group6.databinding.ActivityMealPlanBinding
import com.capstone.group6.feature_meal.domain.model.CuisineType
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.domain.model.User
import com.capstone.group6.feature_meal.domain.util.MealOrder
import com.capstone.group6.feature_meal.presentation.MealsViewModel
import com.capstone.group6.ui.interfaces.AdapterOnClick
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MealPlannerActivity : AppCompatActivity(), AdapterOnClick {
    lateinit var binding: ActivityMealPlanBinding
    private val mealsViewModel: MealsViewModel by viewModels()
    val mealTypes =
        arrayOf("Breakfast", "Lunch", "Dinner", "Snack", "Dessert", "Brunch", "Appetizer")
    var selectedMeal = -1
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
            showDietaryTagsDialog(binding.etDietaryTags, this)
        }


        val btnDecrement = binding.btnDecrement
        val btnIncrement = binding.btnIncrement

        var quantity = 1

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
        binding.btnUploadImage.setOnClickListener {
            val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            changeImage.launch(pickImg)
        }
        val spinnerMealType = binding.spinnerMealType


        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mealTypes)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerMealType.adapter = adapter

        spinnerMealType.setSelection(0)
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
                // Handle the selected meal type
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        binding.btnSaveRecipe.setOnClickListener {
            save()
        }


    }

    private val changeImage =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val imgUri = data?.data
                binding.ivRecipeImage.setImageURI(imgUri)
                binding.ivCamera.visibility = View.GONE
                binding.imgAlt.visibility = View.GONE
            }
        }

    private fun setUpCusineType() {
        var adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, CuisineType.values())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCuisineType.adapter = adapter
        binding.spinnerCuisineType.setSelection(0)
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
        val selectedCuisine = binding.spinnerCuisineType.getSelectedItem()
        var cuisineType: CuisineType = CuisineType.Gujarati
        when (selectedCuisine) {
            "Italian" -> cuisineType = CuisineType.Italian
            "Mexican" -> cuisineType = CuisineType.Mexican
            "Chinese" -> cuisineType = CuisineType.Chinese
            "Gujarati" -> cuisineType = CuisineType.Gujarati
            "Punjabi" -> cuisineType = CuisineType.Punjabi
            "South Indian" -> cuisineType = CuisineType.SouthIndian
            else -> {}
        }
        meal.cuisineType = cuisineType
        meal.count = binding.tvQuantity.text.toString()


        meal.mealType = mealTypes[selectedMeal]
        val id: String = "" + System.currentTimeMillis()
        var name = "Nirali"
        val user = User(name = name, userId = id)
        meal.user=user
        meal.image = ""
        meal.ingredients = "onion"
        meal.likes = 5
        meal.timestamp = System.currentTimeMillis()
        meal.isLocal =true


        lifecycleScope.launch {
            mealsViewModel.mealRepository.insertMeal(meal)
        }
    }


    override fun onClick(item: String, position: Int) {
        MealApp.prefs1!!.ingredient = item
        MealApp.prefs1!!.positionTone = position
    }

    override fun onClickIng(item: String, bottomSheetDialog: BottomSheetDialog, position: Int) {

    }
}