package com.capstone.group6.ui

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.group6.Constant.Companion.startActivity
import com.capstone.group6.MealApp.Companion.prefs1
import com.capstone.group6.databinding.ActivityProfileBinding


class ProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (prefs1?.isname.equals("")) {
            binding.btnApply.visibility = View.GONE
        } else {
            startActivity(MainActivity::class.java)
            finish()
        }

        setUser()

    }

    private fun setUser() {
        binding.etName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.length >= 2) {
                    binding.btnApply.visibility = View.VISIBLE
                    prefs1?.isname =binding.etName.text.toString()
                }else{
                    binding.btnApply.visibility = View.GONE
                }
            }
        });

        binding.submit.setOnClickListener {
            if (prefs1?.isname.equals("")) {
                Toast.makeText(this,"Please enter your name",Toast.LENGTH_SHORT).show()

            } else {
                prefs1?.isname = binding.etName.text.toString()
                startActivity(MainActivity::class.java)
                finish()
            }


        }

    }
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase)
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }
}