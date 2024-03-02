package com.example.users

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.users.databinding.ActivityUpdateBinding
import com.example.users.modal.UserModal
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class UpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra("Id")
        if (id != null) {
            db.collection("userList").document(id).get()
                .addOnSuccessListener {
                    val list = it.toObject(UserModal::class.java)
                    val arrayList = arrayOf(list)
                    binding.upDateName.setText(arrayList[0]?.name)
                    binding.upDateEmail.setText(arrayList[0]?.email)
                    binding.upDateNumber.setText(arrayList[0]?.number)
                    binding.upDateCity.setText(arrayList[0]?.city)

                }
                .addOnFailureListener {
                    Toast.makeText(this, "update get data Failure", Toast.LENGTH_SHORT).show()
                }
        }

        binding.updateBtn.setOnClickListener {
            if (id != null) {
                update(id)
                startActivity(Intent(this,HomeActivity::class.java))
                finish()
            }
        }

    }

    private fun update(id :String){
        binding.updateProgressBar.visibility = View.VISIBLE
        val name = binding.upDateName.text.toString()
        val email = binding.upDateEmail.text.toString()
        val number = binding.upDateNumber.text.toString()
        val city = binding.upDateCity.text.toString()

        val user = hashMapOf(
            "name" to name,
            "number" to number,
            "email" to email,
            "city" to city,
            "id" to id
        )

        db.collection("userList").document(id)
            .update(user as Map<String, Any>)
            .addOnSuccessListener {
                Toast.makeText(this, "update Success full", Toast.LENGTH_SHORT).show()
                binding.updateProgressBar.visibility = View.GONE
                return@addOnSuccessListener
            }
            .addOnFailureListener {
                Toast.makeText(this, "update Failure ", Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }

    }



}