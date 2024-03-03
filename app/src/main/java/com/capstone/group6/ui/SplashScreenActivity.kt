package com.capstone.group6.ui

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.capstone.group6.Constant.Companion.startActivity
import com.capstone.group6.R
import com.capstone.group6.databinding.ActivitySplashScreenBinding
import com.google.android.material.elevation.SurfaceColors

class SplashScreenActivity : AppCompatActivity() {

    private val SPLASH_DURATION: Long = 2500
    lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        window.navigationBarColor = SurfaceColors.SURFACE_2.getColor(this);
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.splashImage.startAnimation(animation)

        Handler(Looper.myLooper()!!).postDelayed({
            startActivity(MainActivity::class.java)
            val intent = Intent(this, MainActivity::class.java)
            val options = ActivityOptions.makeCustomAnimation(
                this,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )

            startActivity(intent, options.toBundle())
            finish()
        }, SPLASH_DURATION)
    }


}