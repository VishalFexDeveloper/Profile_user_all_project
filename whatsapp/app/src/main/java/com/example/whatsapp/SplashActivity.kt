package com.example.whatsapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.whatsapp.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val animation = TranslateAnimation(0f, 0f, 0f, 100f) // Translate vertically from 0 to 100 pixels down
        animation.duration = 1000
        animation.repeatCount = Animation.INFINITE
        animation.repeatMode = Animation.REVERSE

        binding.splashIcon.startAnimation(animation)

        val auth = FirebaseAuth.getInstance()
        Handler().postDelayed({
                 if (auth.currentUser == null){
                     startActivity(Intent(this,AuthActivity::class.java))
                 }else{
                     startActivity(Intent(this,HomeActivity::class.java))
                 }
            finish()

        },4000)

    }
}