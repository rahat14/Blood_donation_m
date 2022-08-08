package com.metacoders.blood_donation.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.metacoders.blood_donation.ContianerPagerAdapter
import com.metacoders.blood_donation.R
import com.metacoders.blood_donation.SharedPrefManager
import com.metacoders.blood_donation.databinding.ActivityMainBinding
import com.metacoders.blood_donation.databinding.FragmentHomeBinding
import com.metacoders.blood_donation.model.Users

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ContianerPagerAdapter

    private val navigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            //   Fragment selected Fragment = null ;
            when (menuItem.itemId) {

                R.id.feeds -> binding.fragContainer.setCurrentItem(0, false)
                R.id.profile -> binding.fragContainer.setCurrentItem(2, false)


            }
            true
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ContianerPagerAdapter(this)
        binding.fragContainer.adapter = adapter
        binding.fragContainer.isUserInputEnabled = false

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_blood_requestFragment)
        }

        binding.profile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
        loadOtherUserData()
    }
    private fun loadOtherUserData() {
        val uid = FirebaseAuth.getInstance().uid.toString()
        val databaseReference =
            FirebaseDatabase.getInstance().getReference("userProfile").child(uid)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user : Users = snapshot.getValue(Users::class.java) as Users
                    //saving user in the local
                    SharedPrefManager.put(user , "user")


                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Error: ${error.message}")
            }
        })
    }
}