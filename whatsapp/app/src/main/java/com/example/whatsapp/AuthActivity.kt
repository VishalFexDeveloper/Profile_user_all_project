package com.example.whatsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.whatsapp.databinding.ActivityAuthBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class AuthActivity : AppCompatActivity() {

    lateinit var binding: ActivityAuthBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.sendPhoBtn.setOnClickListener {
            checkNumber()
        }



    }


    private fun checkNumber(){

        val number = "+91" + binding.enterNumber.text.toString()

        val provider = PhoneAuthProvider.getInstance()
        provider.verifyPhoneNumber(number,60,TimeUnit.SECONDS,this,object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInCredential(credential)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(this@AuthActivity, "number number block", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(otp: String, task: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(otp, task)
                val intent = Intent(this@AuthActivity,VerifyActivity::class.java).apply {
                    putExtra("verificationId",otp)
                }
                startActivity(intent)
                finish()
            }
        })

    }

    private fun signInCredential(credential:PhoneAuthCredential){
        FirebaseAuth.getInstance()
            .signInWithCredential(credential)
            .addOnSuccessListener {
                Toast.makeText(this, "phone number verify Success", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }
            .addOnFailureListener {
                Toast.makeText(this, "phone number verify Failure", Toast.LENGTH_LONG).show()
                return@addOnFailureListener
            }

    }
}