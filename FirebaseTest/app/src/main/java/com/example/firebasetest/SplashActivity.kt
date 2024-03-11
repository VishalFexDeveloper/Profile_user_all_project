package com.example.firebasetest

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val firebaseAuth = FirebaseAuth.getInstance()
        Handler().postDelayed({
            if (firebaseAuth.currentUser == null){
                startActivity(Intent(this,MainActivity::class.java))
            }else{
                startActivity(Intent(this,AddDataActivity::class.java))
            }
            finish()

        },2000)
    }
}