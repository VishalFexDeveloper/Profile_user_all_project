package com.example.profileuser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth

class SpaleshScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spalesh_screen)

        Handler().postDelayed({

            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        },300)


    }
}