package com.example.firebasetest

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.firebasetest.databinding.ActivityDatabaseBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class AddDataActivity : AppCompatActivity() {
    lateinit var binding: ActivityDatabaseBinding
    val db = FirebaseFirestore.getInstance()
    val stroage = FirebaseStorage.getInstance()
    var imagePath:Uri?= null
    val childName = "image/${UUID.randomUUID()}.png"

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatabaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.userImg.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED){
                openGallery()
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),12)
            }
        }

        binding.nextBtn.setOnClickListener {
            if (imagePath == null){
                Toast.makeText(this, "please image select", Toast.LENGTH_SHORT).show()
            }else{
                upLoadData()
                upLoadImg()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
                    .into(binding.userImg)
            }
        }
    }

    private fun upLoadImg(){
        stroage.getReference("users").child(childName).putFile(imagePath!!)
            .addOnSuccessListener {
                Toast.makeText(this, "upLoadImg Success full", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }
            .addOnFailureListener {
                Toast.makeText(this, "upLoadImg Failure ", Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }
    }

    private fun upLoadData(){
        val id = UUID.randomUUID().toString()
        val name = binding.username.text.toString()
        val email = binding.useremail.text.toString()
        val country = binding.userCountry.text.toString()
        val bio = binding.userbio.text.toString()
        if (name.isBlank()||email.isBlank()||country.isBlank()||bio.isBlank()){
            Toast.makeText(this, "please all null", Toast.LENGTH_SHORT).show()
            return
        }else {
            val hashMap = hashMapOf(
                "name" to name,
                "email" to email,
                "bio" to bio,
                "country" to country,
                "userid" to id,
                "childName" to childName
            )
            db.collection("users").document(id).set(hashMap)
                .addOnSuccessListener {
                    startActivity(Intent(this,HomeActivity::class.java))
                    Toast.makeText(this, "data upload Success full", Toast.LENGTH_SHORT).show()
                    binding.userbio.text.clear()
                    binding.userCountry.text.clear()
                    binding.useremail.text.clear()
                    binding.username.text.clear()
                    finish()
                    return@addOnSuccessListener
                }
                .addOnFailureListener {
                    Toast.makeText(this, "data upload Failure ", Toast.LENGTH_SHORT).show()
                    return@addOnFailureListener
                }

        }
    }


}