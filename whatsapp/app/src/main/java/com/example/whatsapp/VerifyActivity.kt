package com.example.whatsapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.whatsapp.databinding.ActivityVerifyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class VerifyActivity : AppCompatActivity() {
    val auth =FirebaseAuth.getInstance()
    lateinit var binding: ActivityVerifyBinding
    lateinit var verificationId:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        verificationId = intent.getStringExtra("verificationId")!!


        binding.verifyBtn.setOnClickListener {
            verifyOTP()
        }

    }

    private fun verifyOTP(){

        val sendTOP = binding.verifyEdit.text.toString()
        if (sendTOP.length == 6){
            val credential = PhoneAuthProvider.getCredential(verificationId,sendTOP)
            auth.signInWithCredential(credential)
                .addOnSuccessListener {
                    checkUserPhone()
                    return@addOnSuccessListener
                }
                .addOnFailureListener {
                    Toast.makeText(this, "verify Failure", Toast.LENGTH_LONG).show()
                    return@addOnFailureListener

                }
        }else{
            Toast.makeText(this, "verify verificationId code ", Toast.LENGTH_SHORT).show()
        }

    }

    fun checkUserPhone(){
        FirebaseFirestore.getInstance().collection("users").whereEqualTo("phone", auth.currentUser?.phoneNumber ).get()
            .addOnSuccessListener {
                if (it.documents.isEmpty()){
                    startActivity(Intent(this,ProfileActivity::class.java))
                    Toast.makeText(this, "verify Success full", Toast.LENGTH_SHORT).show()
                    finish()
                }else {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "User UPLOADING Failure", e)
            }


    }

}