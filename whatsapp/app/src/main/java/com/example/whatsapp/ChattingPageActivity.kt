package com.example.whatsapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.whatsapp.Modal.MessageModal
import com.example.whatsapp.Modal.UserModal
import com.example.whatsapp.ViewPageAdapter.MessageAdapter
import com.example.whatsapp.databinding.ActivityChattingpageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Date

class ChattingPageActivity : AppCompatActivity() {
    lateinit var binding: ActivityChattingpageBinding
    private lateinit var senderUid: String
    private lateinit var receiverUid: String
    private lateinit var senderRoom: String
    private lateinit var receiverRoom: String
    private lateinit var database: FirebaseDatabase
    private lateinit var name :String
    private lateinit var country:String
    private lateinit var imageUri:String
    lateinit var list:ArrayList<MessageModal>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingpageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        database = FirebaseDatabase.getInstance()

        senderUid = FirebaseAuth.getInstance().uid.toString()
        receiverUid = intent.getStringExtra("uid")!!
        name = intent.getStringExtra("name")!!
        country = intent.getStringExtra("country")!!
        imageUri = intent.getStringExtra("image_key")!!
        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid
        list = ArrayList()

        Glide.with(this).load(imageUri).placeholder(R.drawable.baseline_account_circle_24).into(binding.chattingICon)
        binding.userChattingName.text = name
        binding.chattingCountry.text = country

        binding.chattingBtn.setOnClickListener {
            onBackPressed()
        }




        binding.sendMessageBtn.setOnClickListener {
            sendMessage()
        }

        database.reference.child("chats").child(senderRoom).child("message")
            .addValueEventListener(object :ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()

                    for (snapshot1 in snapshot.children){
                        val data = snapshot1.getValue(MessageModal::class.java)
                        if (data != null) {
                            list.add(data)
                        }
                    }

                    binding.chatRecyclerView.adapter = MessageAdapter(list,this@ChattingPageActivity)
                    binding.chatRecyclerView.layoutManager = LinearLayoutManager(this@ChattingPageActivity)
                }


                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ChattingPageActivity, "error : ${error.message}", Toast.LENGTH_SHORT).show()
                }

            })


    }

    private fun sendMessage() {
        if (binding.editTextMessage.text.isEmpty()) {
            binding.editTextMessage.error = "please enter your message"
        } else {
            val message = MessageModal(
                binding.editTextMessage.text.toString(),
                senderUid,
                Date().time
            )
            val randomKey = database.reference.push().key

            database.reference.child("chats").child(senderRoom).child("message").child(randomKey!!)
                .setValue(message)
                .addOnSuccessListener {
                    database.reference.child("chats").child(receiverRoom).child("message")
                        .child(randomKey).setValue(message)
                        .addOnSuccessListener {
                           binding.editTextMessage.text.clear()
                        }
                }
        }
    }

}