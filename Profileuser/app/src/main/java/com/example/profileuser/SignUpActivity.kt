package com.example.profileuser

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentUris
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.profileuser.databinding.ActivityMainBinding
import com.example.profileuser.databinding.ActivitySignUpBinding
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.util.UUID

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference
    private val REQUEST_CODE = 12
    private val REQUEST_CODEVideo = 14
    private var imagePath: Uri? = null
    private var videoPath: Uri? = null
    private val db = FirebaseFirestore.getInstance()

    val childName = "profile/${UUID.randomUUID()}.jpg"
    val childNameVideo = "video/${UUID.randomUUID()}.mp4"

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)

        binding.addImageBtn.setOnClickListener {
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
                    REQUEST_CODE
                )
            }
        }

        binding.createBtn.setOnClickListener {
            addData()
        }

        binding.videoUpload.setOnClickListener {
            openVideo()
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ( resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            when(requestCode){
                REQUEST_CODE -> {
                    if (imageUri != null) {
                        imagePath = imageUri
                        binding.addImageBtn.setImageURI(imagePath)
                        Upload()
                    }
                }

                REQUEST_CODEVideo ->{
                    if (imageUri != null){
                        videoPath = imageUri
                        upLoadVideo()
                    }
                }

            }

        }
    }



    @SuppressLint("IntentReset")
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun openVideo() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODEVideo)
    }


    private fun Upload() {
        showProgressDialog(true)

        if (imagePath == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            return
        }

        val fileRef = storageRef.child(childName)
        val uploadTask = fileRef.putFile(imagePath!!)
        uploadTask.addOnSuccessListener {
            Toast.makeText(this, "Upload Success", Toast.LENGTH_SHORT).show()
            showProgressDialog(false)
            return@addOnSuccessListener
        }.addOnFailureListener addOnSuccessListener@{ _ ->
            Toast.makeText(this, "Upload Failure", Toast.LENGTH_SHORT).show()
            return@addOnSuccessListener
        }
    }

    private fun upLoadVideo(){
        showProgressDialog(true)
        if (videoPath == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            return
        }
        val fileRef = storageRef.child(childNameVideo)
        val uploadTask = fileRef.putFile(videoPath!!)
        uploadTask.addOnSuccessListener {
            Toast.makeText(this, "Upload Success video", Toast.LENGTH_SHORT).show()
            showProgressDialog(false)
            return@addOnSuccessListener
        }.addOnFailureListener addOnSuccessListener@{ _ ->
            Toast.makeText(this, "Upload Failure video", Toast.LENGTH_LONG).show()
            return@addOnSuccessListener
        }
    }

    private fun addData(){

        val fistName = binding.firstName.text.toString()
        val lastName = binding.lastName.text.toString()
        val bio = binding.bio.text.toString()
        val countryName = binding.countryName.text.toString()
        if (fistName.isBlank() || lastName.isBlank() || bio.isBlank()||countryName.isBlank() ){
            Toast.makeText(this, "please enter filed", Toast.LENGTH_SHORT).show()
            return
        }else {
            showProgressDialog(true)
            val id = UUID.randomUUID().toString()

            val data = hashMapOf(
                "first" to fistName,
                "lastName" to lastName,
                "bio" to bio,
                "countryName" to countryName,
                "id" to id,
                "childName" to childName,
                "childNameVideo" to childNameVideo
            )

            db.collection("profileData")
                .document(id)
                .set(data)
                .addOnSuccessListener addOnFailureListener@{
                    Toast.makeText(this, "data save Success", Toast.LENGTH_SHORT).show()
                    showProgressDialog(false)
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                    return@addOnFailureListener
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failure data", Toast.LENGTH_SHORT).show()
                    return@addOnFailureListener
                }
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