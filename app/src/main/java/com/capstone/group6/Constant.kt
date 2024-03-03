/**Created By Nirali Pandya
 * Date :2024-03-01
 * Time :1:09 p.m.
 * Project Name :meal-sharing-app
 *
 */
package com.capstone.group6

import android.app.Activity
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.group6.ui.MealPlannerActivity
import com.capstone.group6.ui.adapters.IngredientsAdapter
import com.capstone.group6.ui.interfaces.AdapterOnClick
import com.google.android.material.bottomsheet.BottomSheetDialog

class Constant {
    companion object {
        fun Activity.startActivity(destination: Class<*>, position: Int = 0) {
            val intent = Intent(this, destination).apply {
                putExtra("position", position)
            }
            val options = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )

            startActivity(intent, options.toBundle())
        }


        fun Activity.showBottomSheetDialog(
            adapterOnClick: AdapterOnClick
        ) {
            val bottomSheetDialog = BottomSheetDialog(this)
            bottomSheetDialog.setContentView(R.layout.layout_list_ingredients)
            bottomSheetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            bottomSheetDialog.findViewById<RecyclerView>(R.id.ingredientsRecyclerview)?.let {
                setAdapter(
                    it,
                    this as MealPlannerActivity,
                    adapterOnClick
                )
            }
            bottomSheetDialog.findViewById<ImageView>(R.id.close)?.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            bottomSheetDialog.show()
        }

        var iconsList = arrayOf(
            R.drawable.ic_lemon,
            R.drawable.ic_chilly,
            R.drawable.ic_tomato,
            R.drawable.ic_onion,
            R.drawable.ic_garlic,
            R.drawable.ic_potato,
            R.drawable.ic_lettus,
            R.drawable.ic_avacado,
            R.drawable.ic_milk,
            R.drawable.ic_butter,
            R.drawable.ic_cheese,
            R.drawable.ic_yogurt,
            R.drawable.ic_creme,
            R.drawable.ic_sourcreme,
            R.drawable.ic_cottagecheese,
        )

        private fun setAdapter(
            recyclerView: RecyclerView,
            activity: MealPlannerActivity, adapterOnClick: AdapterOnClick
        ) {
            val ingredients = arrayOf(
                "Lemon", "Green Chilli", "Tomato", "Onion", "Garlic",
                "Potato", "Lettuce", "Avocado",
                "Milk", "Butter", "Cheese", "Yogurt", "Cream", "Sour Cream",
                "Cottage Cheese"
            )
            val adapter =
                IngredientsAdapter(adapterOnClick, ingredients, iconsList, activity)

            recyclerView.layoutManager = GridLayoutManager(activity, 3)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter

        }
        // Inside your activity

        fun showDietaryTagsDialog(etDietaryTags:EditText,context: Context) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dietary_tags_dialog, null)

            val checkboxVegetarian = dialogView.findViewById<CheckBox>(R.id.checkbox_vegetarian)
            val checkboxVegan = dialogView.findViewById<CheckBox>(R.id.checkbox_vegan)
            val checkboxGlutenFree = dialogView.findViewById<CheckBox>(R.id.checkbox_gluten_free)
            // Initialize more checkboxes if needed

            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setTitle("Select Dietary Tags")
                .setCancelable(true)
                .create()

            dialogView.findViewById<Button>(R.id.btn_apply).setOnClickListener {
                // Handle apply button click
                val selectedDietaryTags = mutableListOf<String>()
                if (checkboxVegetarian.isChecked) {
                    selectedDietaryTags.add("Vegetarian")
                }
                if (checkboxVegan.isChecked) {
                    selectedDietaryTags.add("Vegan")
                }
                if (checkboxGlutenFree.isChecked) {
                    selectedDietaryTags.add("Gluten-Free")
                }
                // Add more dietary tags as needed

                // Handle the selected dietary tags here, for example:
                // You can update the MultiAutoCompleteTextView with the selected dietary tags
                val selectedTagsString = selectedDietaryTags.joinToString(", ")
                etDietaryTags.setText(selectedTagsString)

                dialog.dismiss()
            }

            dialog.show()
        }


    }
}