package com.example.realtimedatabase

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.realtimedatabase.databinding.ActivityChatBotBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID

class ChatBotActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatBotBinding
    lateinit var chatingAdapter :ChatingAdapter
    lateinit var  chatingList:ArrayList<ChatModal>

    private val storageRef = FirebaseDatabase.getInstance().getReference("cheating Database")

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBotBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)



//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        binding.backBtn.setOnClickListener {
           finish()
        }
        
        binding.sendBtn.setOnClickListener {
            if (binding.editTextMessage.text.toString().isNotBlank() ){
                writeCheating()
            }else{
                binding.editTextMessage.error = "enter Message"
            }
        }
        read()
       
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

    private fun writeCheating(){

        val value = binding.editTextMessage.text.toString()

        val cheating_id = "cheating"+generateRandomString(20)
        val chating = hashMapOf("cheating" to value,"childName" to cheating_id)

        storageRef.child(cheating_id)
            .setValue(chating)
            .addOnSuccessListener {
                Toast.makeText(this, "add Success full", Toast.LENGTH_SHORT).show()
                binding.editTextMessage.text.clear()
                return@addOnSuccessListener
            }
            .addOnFailureListener {
                Toast.makeText(this, "add Success full", Toast.LENGTH_SHORT).show()
                return@addOnFailureListener
            }
    }



    private fun read() {
        showProgressDialog(true)
        storageRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val tempList = ArrayList<ChatModal>()
                    for (postSnapshot in dataSnapshot.children) {
                        val value = postSnapshot.getValue(ChatModal::class.java)
                        value?.let { tempList.add(it) }
                    }
                    // Initialize chatingList before assigning value
                    chatingList = tempList.reversed() as ArrayList<ChatModal>
                    chatingAdapter = ChatingAdapter(chatingList, this@ChatBotActivity)
                    binding.chatRecyclerView.layoutManager = LinearLayoutManager(this@ChatBotActivity)
                    binding.chatRecyclerView.adapter = chatingAdapter
                    showProgressDialog(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
    }


    companion object {
        private const val TAG = "ChatBotActivity"
    }




    private fun generateRandomString(length: Int): String {
        val uuid = UUID.randomUUID()
        return uuid.toString().substring(0, length)
    }

    

}