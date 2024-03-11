package com.example.realtimedatabase

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.realtimedatabase.databinding.ActivityAssistantBinding

class Assistant_Activity : AppCompatActivity() {
    lateinit var binding: ActivityAssistantBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssistantBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.ContinueBtn.setOnClickListener {
            startActivity(Intent(this,ChatBotActivity::class.java))
            finish()
            getSharedPreferences("user", MODE_PRIVATE).edit()
            .putBoolean("check",true).apply()
        }

    }
}