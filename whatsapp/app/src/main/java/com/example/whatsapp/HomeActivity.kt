package com.example.whatsapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.whatsapp.Modal.UserModal
import com.example.whatsapp.ViewPageAdapter.ViewPagerAdapter
import com.example.whatsapp.databinding.ActivityHomeBinding
import com.example.whatsapp.databinding.NavHeaderBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    lateinit var binding:ActivityHomeBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.homeViewPager2.adapter = ViewPagerAdapter(supportFragmentManager,lifecycle)
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open_nav, R.string.close_nav)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.menuBtn.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }


        TabLayoutMediator(binding.HomeTAbLayout,binding.homeViewPager2){ tab,position ->
            when(position){
                0 -> {tab.text = "FRIENDS"
                    binding.floatingBtn.setIconResource(R.drawable.add_contact)
                }
                1 -> {tab.text = "CHATTING"
                    binding.floatingBtn.setIconResource(R.drawable.baseline_edit_24)
                }
                2 ->{
                    tab.text = "STATUS"
                    binding.floatingBtn.setIconResource(R.drawable.baseline_camera_24)

                }
            }
        }.attach()

        binding.homeViewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        binding.floatingBtn.setIconResource(R.drawable.add_contact)
                    }
                    1 -> {
                        binding.floatingBtn.setIconResource(R.drawable.baseline_edit_24)
                    }
                    2 ->{
                        binding.floatingBtn.setIconResource(R.drawable.baseline_camera_24)

                    }
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        FirebaseDatabase.getInstance().reference.child("users")
            .addValueEventListener(object :ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (a in snapshot.children){
                        val data = a.getValue(UserModal::class.java)
                        if (data!!.uid == FirebaseAuth.getInstance().uid){
                            val userData = data
                            val navHeaderBinding = NavHeaderBinding.inflate(layoutInflater)
                            Glide.with(this@HomeActivity).load(userData.imageUri).placeholder(R.drawable.img_gril).into(navHeaderBinding.drawerICon)
                            navHeaderBinding.drawerBio.text = userData.bio
                            navHeaderBinding.drawerName.text = userData.name

                            // Set navigation header
                            binding.navView.addHeaderView(navHeaderBinding.root)
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@HomeActivity, "DatabaseError", Toast.LENGTH_SHORT).show()
                    return
                }

            })
    }
}


