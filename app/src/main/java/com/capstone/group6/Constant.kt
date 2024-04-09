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
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.domain.model.User
import com.capstone.group6.ui.MainActivity
import com.capstone.group6.ui.MealPlannerActivity
import com.capstone.group6.ui.adapters.IngredientsAdapter
import com.capstone.group6.ui.interfaces.AdapterOnClick
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class Constant {
    companion object {

        val VIEW_TYPE_LIST: Int = 0
        val VIEW_TYPE_GRID: Int = 1
        val VIEW_TYPE_DETAILS: Int = 2

        val languageCodes = arrayOf(
            "en",
            "hi", // English
            "ar", // German
            "de", // Spanish
            "ru", // French
            "pt" // Italian

        )

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
                adapterOnClick.onClickIng("", bottomSheetDialog, 0)
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

        fun showDietaryTagsDialog(
            etDietaryTags: EditText, context: Context,
            onData: (String) -> Unit
        ) {
            val dialogView =
                LayoutInflater.from(context).inflate(R.layout.dietary_tags_dialog, null)

            val checkboxVegetarian = dialogView.findViewById<CheckBox>(R.id.checkbox_vegetarian)
            val checkboxVegan = dialogView.findViewById<CheckBox>(R.id.checkbox_vegan)
            val checkboxGlutenFree = dialogView.findViewById<CheckBox>(R.id.checkbox_gluten_free)
            val checkboxDairy = dialogView.findViewById<CheckBox>(R.id.checkbox_dairy)
            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setTitle("Select Dietary Tags")
                .setCancelable(true)
                .create()

            dialogView.findViewById<Button>(R.id.btn_apply).setOnClickListener {
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
                if (checkboxDairy.isChecked) {
                    selectedDietaryTags.add("Dairy-Free")
                }


                val selectedTagsString = selectedDietaryTags.joinToString(", ")
                onData(selectedTagsString)
                etDietaryTags.setText(selectedTagsString)

                dialog.dismiss()
            }

            dialog.show()
        }

        fun uploadImage(
            imageUri: Uri, storage: FirebaseStorage,
            onSuccess: (String) -> Unit,
            onFailure: (Exception) -> Unit
        ) {
            val userName = MealApp.prefs1?.isname
            val filename = "${userName}.jpg"
            val storageRef = storage.reference.child("images/$filename")

            storageRef.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    onSuccess("")
//                    storageRef.downloadUrl.addOnSuccessListener { uri ->
//                        val downloadUrl = uri.toString()
//                        Log.d("TAG", "uploadImage: $downloadUrl")
//                    }

                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        onSuccess(downloadUrl)
                        Log.d("TAG", "uploadImage: $downloadUrl")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("TAG", "uploadImage: ${exception.message}")
                    onFailure(exception)
                }
        }

        fun Activity.saveUserToFirestore(user: User, onSuccess: (String) -> Unit) {
            Firebase.firestore.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    val userDoc = documentReference.path
                    onSuccess(userDoc)
                    Log.d("TAG", "User document added with ID: ${userDoc}")
                }
                .addOnFailureListener { e ->
                    Log.w("TAG", "Error adding user document", e)
                }
        }

    }
}