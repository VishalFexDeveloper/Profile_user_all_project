package com.example.profileuser

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.profileuser.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        getData()

        binding.backBtn.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun getData() {
        showProgressDialog(true)

        db.collection("profileData")
            .get()
            .addOnSuccessListener {document ->
                val list = arrayListOf<ProfileModal>()
                for (a in document){
                    val data = a.toObject(ProfileModal::class.java)
                    list.add(data)
                }
               val adapter = ProfileAdapter(list,this)
                binding.profileRecyclerList.layoutManager = LinearLayoutManager(this)
                binding.profileRecyclerList.adapter = adapter
                Toast.makeText(this, "user details Success", Toast.LENGTH_SHORT).show()
                showProgressDialog(false)
                return@addOnSuccessListener
            }
            .addOnFailureListener {
                Toast.makeText(this, "user details Failure", Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }
    }

    private var progressDialog: ProgressDialog? = null

    private fun showProgressDialog(show: Boolean) {
        if (show) {
            if (progressDialog == null || !progressDialog!!.isShowing) {
                progressDialog = ProgressDialog(this)
                progressDialog!!.setMessage("Loading...")
                progressDialog!!.setCancelable(false)
                progressDialog!!.show()
            }
        } else {
            progressDialog?.dismiss()
            progressDialog = null
        }
    }


}