package com.example.firebasetest

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.firebasetest.databinding.ActivityUpDateBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class UpDateActivity : AppCompatActivity() {
    lateinit var binding:ActivityUpDateBinding
    val db = FirebaseFirestore.getInstance()
    val stroage = FirebaseStorage.getInstance()
    var imagePath: Uri?= null
    var userId:String? = null
    var chaildName:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpDateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("userid")
        chaildName = intent.getStringExtra("childName")

        binding.userbioUP.setText(intent.getStringExtra("bio"))
        binding.userCountryUP.setText(intent.getStringExtra("country"))
        binding.usernameUp.setText(intent.getStringExtra("name"))
        binding.useremailUP.setText(intent.getStringExtra("email"))

        FirebaseStorage.getInstance().getReference("users").child(chaildName!!).downloadUrl
            .addOnSuccessListener {
                Glide.with(this)
                    .load(it)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(binding.userImgUP)
            }

        binding.userImgUP.setOnClickListener {
                openGallery()

        }

        binding.nextBtnUP.setOnClickListener {
            if (imagePath == null){
                Toast.makeText(this, "please image select", Toast.LENGTH_SHORT).show()
            }else{

                updateImage()
                upLoadData()
            }

        }
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,12)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12 && resultCode == RESULT_OK){
            val imageUri = data?.data
            if (data != null){
                imagePath = imageUri
                Glide.with(this)
                    .load(imagePath)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(binding.userImgUP)
            }
        }
    }

    private fun updateImage(){
        stroage.getReference("users").child(chaildName!!).putFile(imagePath!!)
            .addOnSuccessListener {
                Toast.makeText(this, "upLoadImg Success full", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }
            .addOnFailureListener {
                Toast.makeText(this, "upLoadImg Failure ", Toast.LENGTH_LONG).show()
                return@addOnFailureListener
            }
    }

    private fun upLoadData(){
        val name = binding.usernameUp.text.toString()
        val email = binding.useremailUP.text.toString()
        val country = binding.userCountryUP.text.toString()
        val bio = binding.userbioUP.text.toString()
        if (name.isBlank()||email.isBlank()||country.isBlank()||bio.isBlank()){
            Toast.makeText(this, "please all null", Toast.LENGTH_SHORT).show()
            return
        }else {

            val hashMap = hashMapOf(
                "name" to name,
                "email" to email,
                "bio" to bio,
                "country" to country
            )
            db.collection("users").document(userId!!).update(hashMap as Map<String, Any>)
                .addOnSuccessListener {
                    startActivity(Intent(this,HomeActivity::class.java))
                    finish()
                    Toast.makeText(this, "data upload Success full", Toast.LENGTH_SHORT).show()
                    binding.userbioUP.text.clear()
                    binding.userCountryUP.text.clear()
                    binding.useremailUP.text.clear()
                    binding.usernameUp.text.clear()
                    finish()
                    return@addOnSuccessListener
                }
                .addOnFailureListener {
                    Toast.makeText(this, "data update Failure ", Toast.LENGTH_SHORT).show()
                    return@addOnFailureListener
                }

        }
    }


}