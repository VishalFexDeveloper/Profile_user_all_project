package com.example.firebasestorage

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.firebasestorage.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val db = FirebaseFirestore.getInstance()

    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.saveBtn.setOnClickListener {
            saveData()
            Toast.makeText(this, "click", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveData() {
        val userName = binding.userName.text.toString().trim()
        val userNumber = binding.userNumber.text.toString().trim()
        val userEmail = binding.userEmail.text.toString().trim()
        val userPassword = binding.userPassword.text.toString().trim()

        binding.progressBar.visibility = View.VISIBLE

        if (userEmail.isBlank() || userName.isBlank() || userNumber.isBlank() || userPassword.isBlank()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val user = hashMapOf(
            "name" to userName,
            "number" to userNumber,
            "email" to userEmail,
            "password" to userPassword
        )

        currentUser?.let { user["uid"] = it.uid }

        db.collection("userid")
            .add(user)
            .addOnSuccessListener {
                Toast.makeText(this, "User data saved successfully", Toast.LENGTH_SHORT).show()
                clearFields()
                binding.progressBar.visibility = View.GONE
                return@addOnSuccessListener
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }
    }

    private fun clearFields() {
        binding.userName.text.clear()
        binding.userNumber.text.clear()
        binding.userEmail.text.clear()
        binding.userPassword.text.clear()
    }
}
