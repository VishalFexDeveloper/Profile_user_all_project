package com.example.profileuser

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.profileuser.databinding.ActivityUpDateBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UpDateActivity : AppCompatActivity() {

    var id: String? = null
    var childName: String? = null
    var childNameVideo: String? = null
    lateinit var binding: ActivityUpDateBinding
    val storage = FirebaseStorage.getInstance().reference
    val db = FirebaseFirestore.getInstance()
    private val REQUEST_CODE = 12
    private val REQUEST_CODEVideo = 14
    private var imagePath: Uri? = null
    private var videoPath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpDateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.getStringExtra("id")
        childName = intent.getStringExtra("childName")
        childNameVideo = intent.getStringExtra("childNameVideo")

        binding.upDateImageBtn.setOnClickListener {
            openGallery()
        }

        binding.upDateAccBtn.setOnClickListener {
            upDate()
        }

        binding.updateVideo.setOnClickListener {
            openVideo()
        }

        getData()
        downloadImg()


    }


    private fun openVideo() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODEVideo)
    }

    @SuppressLint("SuspiciousIndentation")
    fun getData(){
        db.collection("profileData")
            .document(id!!)
            .get()
            .addOnSuccessListener {
                val list = arrayListOf<ProfileModal>()
                val allData = it.toObject(ProfileModal::class.java)
                    list.add(allData!!)

                if (list.size == 1) {
                    binding.upDateBio.setText(list[0].bio)
                    binding.upDateFirstName.setText(list[0].first)
                    binding.upDatelastName.setText(list[0].lastName)
                    binding.upDatecountryName.setText(list[0].countryName)
                }
                Toast.makeText(this, "user details Success", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to retrieve user details: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    @SuppressLint("IntentReset")
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE)
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
                        binding.upDateImageBtn.setImageURI(imagePath)
                        upDateImg(imagePath!!,childName)
                    }
                }

                REQUEST_CODEVideo ->{
                    if (imageUri != null){
                        videoPath = imageUri
                        upDateVideo(videoPath!!,childNameVideo)
                    }
                }

            }

        }

    }

    fun upDateVideo(newImagePath: Uri, childName: String?) {
        if (childName != null) {
            storage.child(childName)
                .putFile(newImagePath)
                .addOnSuccessListener {

                    Toast.makeText(this, "video updated successfully", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                .addOnFailureListener {

                    Toast.makeText(this, "Failed to update video", Toast.LENGTH_LONG).show()
                    return@addOnFailureListener
                }
        }
    }


    fun upDateImg(newImagePath: Uri, childName: String?) {
        if (childName != null) {
             storage.child(childName)
            .putFile(newImagePath)
                .addOnSuccessListener {

                    Toast.makeText(this, "Image updated successfully", Toast.LENGTH_SHORT).show()
                   return@addOnSuccessListener
                }
                .addOnFailureListener {

                    Toast.makeText(this, "Failed to update image", Toast.LENGTH_SHORT).show()
                    return@addOnFailureListener
                }
        }
    }


    private fun downloadImg() {
        childName?.let { it ->
            storage.child(it)
                .downloadUrl
                .addOnSuccessListener {
                    Glide.with(this)
                        .load(it)
                        .apply(RequestOptions().placeholder(R.drawable.profile_icon)) // Optional placeholder image
                        .into(binding.upDateImageBtn)
                    Toast.makeText(this, "downloadImg Success", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                .addOnFailureListener {
                    Toast.makeText(this, "downloadImg Failure", Toast.LENGTH_SHORT).show()
                    return@addOnFailureListener
                }
        }
    }

    fun upDate(){
        showProgressDialog(true)
        val firstName = binding.upDateFirstName.text.toString()
        val lastName = binding.upDatelastName.text.toString()
        val bio = binding.upDateBio.text.toString()
        val country = binding.upDatecountryName.text.toString()

        val hasMap = hashMapOf("first" to firstName,"lastName" to lastName,"bio" to bio,"countryName" to country)

        id?.let {
            db.collection("profileData")
                .document(it)
                .update(hasMap as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this, "update Success", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,HomeActivity::class.java))
                    finish()
                    showProgressDialog(false)
                    return@addOnSuccessListener
                }
                .addOnFailureListener {
                    Toast.makeText(this, "update Success", Toast.LENGTH_SHORT).show()
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