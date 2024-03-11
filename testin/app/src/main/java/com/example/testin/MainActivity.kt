package com.example.testin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var verificationId: String
    var numberMy:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val number :EditText = findViewById(R.id.number)
        val sendBtn:Button = findViewById(R.id.sendBtn)
        val veryPass:EditText = findViewById(R.id.veryPass)
        val veryBtn :Button = findViewById(R.id.veryBtn)


        sendBtn.setOnClickListener {
            numberMy = "+91"+number.text.toString()
            sendOtp()
        }
        veryBtn.setOnClickListener {
            verifyCode(veryPass.text.toString())
        }

    }

    private fun sendOtp(){
        val phoneAuthProvider = PhoneAuthProvider.getInstance()
        phoneAuthProvider.verifyPhoneNumber(numberMy!!,60,TimeUnit.SECONDS,this,object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(this@MainActivity, "Verification Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(otp: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(otp, p1)
                this@MainActivity.verificationId = otp

            }

        })

    }

    private fun verifyCode(Code:String){

        val provider = PhoneAuthProvider.getCredential(verificationId,Code)

        signInWithPhoneAuthCredential(provider)
    }

    private fun signInWithPhoneAuthCredential(provider: PhoneAuthCredential) {
    FirebaseAuth.getInstance().signInWithCredential(provider)
        .addOnSuccessListener {
            Toast.makeText(this,"Success",Toast.LENGTH_LONG).show()
            return@addOnSuccessListener
        }
    }


}