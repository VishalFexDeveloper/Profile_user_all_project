package com.example.whatsapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.whatsapp.Modal.UserModal
import com.example.whatsapp.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.Date

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val TAG = "ProfileActivity"
    private val storage = FirebaseStorage.getInstance()
    private val rdb = FirebaseDatabase.getInstance()
    private var imagePath: Uri? = null
    private var progressDialog: ProgressDialog? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.profileAddImg.setOnClickListener {
            checkPermissions()
        }

        binding.profileBtn.setOnClickListener {
            if (imagePath == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            } else {
                uploadImage(imagePath!!)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openGallery()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                12
            )
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 12)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 12 && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            if (imageUri != null) {
                imagePath = imageUri
                Glide.with(this@ProfileActivity)
                    .load(imageUri)
                    .placeholder(R.drawable.img_gril)
                    .error(R.drawable.ic_launcher_background)
                    .into(binding.profileImg)
                binding.profileAddImg.visibility = View.GONE
            }
        }
    }

    private fun uploadImage(uri: Uri) {
        progressDialog(true)
        val chaild = "users/${Date().time}.png"
        val imageRef = storage.reference.child(chaild)
        imageRef.putFile(uri)
            .addOnCompleteListener { it ->
                if (it.isSuccessful){
                    imageRef.downloadUrl.addOnSuccessListener {
                        uploadInfo(it.toString(), chaild)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "IMAGE UPLOADING Failure", e)
            }
    }

    private fun uploadInfo(imageUri: String,chaild:String) {
        val auth = FirebaseAuth.getInstance()
        val id = auth.currentUser?.uid
        val name = binding.profileName.text.toString()
        val bio = binding.profileBio.text.toString()
        val country = binding.profileCountry.text.toString()

        val user = UserModal(id,name,bio,country,auth.currentUser!!.phoneNumber,imageUri,chaild)

        if (binding.profileBio.text.isEmpty() || binding.profileCountry.text.isEmpty() || binding.profileName.text.isEmpty()) {
            if (binding.profileName.text.isEmpty()) {
                binding.profileName.error = "enter bio text"
            } else if (binding.profileBio.text.isEmpty()) {
                binding.profileBio.error = "enter profile name "
            } else if (binding.profileCountry.text.isEmpty()) {
                binding.profileCountry.error = "enter Country name "
            } else {
                Toast.makeText(this, "Please enter text", Toast.LENGTH_SHORT).show()
                return
            }
        } else {

            rdb.reference.child("users").child(id!!).setValue(user)
                .addOnSuccessListener {
                    progressDialog(false)
                    Toast.makeText(this, "date inserted", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@ProfileActivity, HomeActivity::class.java))
                    finish()
                    return@addOnSuccessListener
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
                    progressDialog(false)
                    Log.e(TAG, "User UPLOADING Failure", e)
                    return@addOnFailureListener
                }
        }
    }



    private fun progressDialog(check: Boolean) {
        if (check) {
            if (progressDialog == null || progressDialog!!.isShowing) {
                progressDialog = ProgressDialog(this)
                progressDialog!!.setMessage("LOADING...")
                progressDialog!!.setCancelable(false)
                progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                progressDialog!!.show()
            }
        } else {
            progressDialog?.dismiss()
        }
    }


}