package com.capstone.group6.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ai.emailassistant.adapters.LanguageAdapter
import com.capstone.group6.Constant.Companion.languageCodes
import com.capstone.group6.MealApp
import com.capstone.group6.MealApp.Companion.prefs1
import com.capstone.group6.R
import com.capstone.group6.databinding.ActivityMainBinding
import com.capstone.group6.databinding.LayoutLanguageBinding
import com.capstone.group6.feature_meal.domain.model.Language
import com.capstone.group6.ui.interfaces.AdapterOnClick
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.murgupluoglu.flagkit.FlagKit

class LanguageActivity : AppCompatActivity(), AdapterOnClick {

    private lateinit var languageList: ArrayList<Language>
    private lateinit var languageAdapter: LanguageAdapter
    private lateinit var binding: LayoutLanguageBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setAdapter()
        onClick()


    }


    private fun onClick() {
        binding.done!!.setOnClickListener {
            prefs1!!.isLangugaeShown = true
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        binding.back.setOnClickListener { onBackPressed() }
    }

    private fun setAdapter() {
        languageList = ArrayList()
        foodListItems()

        languageAdapter = LanguageAdapter(this, languageList, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = languageAdapter

    }
    val languageCodes = arrayOf(
        "en",
        "ar",
        "de",
        "es",
        "fr",
        "gu",
        "hi",
        "pt",
        "ru",
        "vi",
        "zh",

        )
    private fun foodListItems() {
        val resourceId = FlagKit.getResId(this, "us")

        languageList.add(Language(resourceId, "USA"))
        languageList.add(Language(FlagKit.getResId(this, "ar"), "Arabic"))
        languageList.add(Language(FlagKit.getResId(this, "de"), "German"))
        languageList.add(Language(FlagKit.getResId(this, "es"), "Spanish"))
        languageList.add(Language(FlagKit.getResId(this, "fr"), "France"))
        languageList.add(Language(FlagKit.getResId(this, "in"), "Gujarati"))
        languageList.add(Language(FlagKit.getResId(this, "in"), "Hindi"))
        languageList.add(Language(FlagKit.getResId(this, "pt"), "Portuguese"))
        languageList.add(Language(FlagKit.getResId(this, "ru"), "Russian"))
        languageList.add(Language(FlagKit.getResId(this, "vi"), "Vietnamese"))
        languageList.add(Language(FlagKit.getResId(this, "cn"), "China"))

    }

    override fun onClick(item: ArrayList<String>, position: Int) {
    }

    override fun onClickIng(item: String, bottomSheetDialog: BottomSheetDialog, position: Int) {
    }


    override fun onClickLanguage(item: String,position:Int) {
        MealApp.prefs1!!.languageCode = languageCodes[position]
        prefs1!!.position=position


    }


}