package com.example.realtimedatabase

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SpelceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.spelce_activity)
        val sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        val check = sharedPreferences.getBoolean("check",false)
        Handler().postDelayed({
            if (check){
                startActivity(Intent(this,ChatBotActivity::class.java))

            }else{
                startActivity(Intent(this,Assistant_Activity::class.java))
            }
            finish()
        },3000)
    }
}