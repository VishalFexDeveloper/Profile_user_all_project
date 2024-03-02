package com.example.users

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.users.databinding.ActivityHomeBinding
import com.example.users.modal.UserModal
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter:UserAdapter
    private lateinit var userList :ArrayList<UserModal>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getUserList()

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this@HomeActivity, MainActivity::class.java))
            finish()
        }



    }

    private fun getUserList() {
        binding.getDataProgress.visibility = View.VISIBLE
        db.collection("userList")
            .get()
            .addOnSuccessListener { documents ->
                 userList = ArrayList<UserModal>()
                for (document in documents) {
                    val user = document.toObject(UserModal::class.java)
                    userList.add(user)
                }
                adapter =UserAdapter(userList, this)
                binding.recyclerView.layoutManager = LinearLayoutManager(this)
                binding.recyclerView.adapter = adapter
                binding.getDataProgress.visibility = View.GONE
            }
            .addOnFailureListener {
                Toast.makeText(this, "data Failure", Toast.LENGTH_SHORT).show()
            }
    }



}


