package com.example.whatsapp.Fragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsapp.HomeActivity
import com.example.whatsapp.Modal.UserModal
import com.example.whatsapp.ViewPageAdapter.FriendAdapter
import com.example.whatsapp.databinding.FragmentFriendBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FriendFragment : Fragment() {

    lateinit var binding:FragmentFriendBinding
    lateinit var userList:ArrayList<UserModal>
    private var progressDialog: ProgressDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFriendBinding.inflate(layoutInflater)

        progressDialog(true)
        userList = ArrayList()
        FirebaseDatabase.getInstance().reference.child("users")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("MissingInflatedId")
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (snapshot1 in snapshot.children){
                        val user = snapshot1.getValue(UserModal::class.java)
                        if (user!!.uid != FirebaseAuth.getInstance().uid){
                            userList.add(user)
                        }
                    }

                    binding.friendRecyclerview.layoutManager = LinearLayoutManager(requireContext())
                    binding.friendRecyclerview.adapter = FriendAdapter(userList,requireContext())
                    progressDialog(false)
                }


                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "DatabaseError", Toast.LENGTH_SHORT).show()
                    return
                }

            })
        return binding.root
    }


    private fun progressDialog(check: Boolean) {
        if (check) {
            if (progressDialog == null || progressDialog!!.isShowing) {
                progressDialog = ProgressDialog(requireContext())
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