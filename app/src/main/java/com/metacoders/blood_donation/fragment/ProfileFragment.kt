package com.metacoders.blood_donation.fragment

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.metacoders.blood_donation.databinding.FragmentProfileBinding
import com.metacoders.blood_donation.model.Users


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadOtherUserData()

        //check box listener
        binding.isChecked.setOnCheckedChangeListener { _, isChecked ->
            val uid = FirebaseAuth.getInstance().uid.toString()
            val mref = FirebaseDatabase.getInstance().getReference("userProfile")
            mref.child(uid).child("isDonate").setValue(isChecked).addOnCompleteListener {
                Toast.makeText(requireContext(), "Updated !!", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Error : ${it.message}", Toast.LENGTH_SHORT).show()
            }


        }


        binding.logout.setOnClickListener {
            try {
                FirebaseAuth.getInstance().signOut()
            }catch (e : Exception){

            }finally {
                val navOptions: NavOptions = NavOptions.Builder().setPopUpTo(com.metacoders.blood_donation.R.id.signInFragment, true).build()
                findNavController().navigate(com.metacoders.blood_donation.R.id.signInFragment , null , navOptions)

            }
        }

    }

    private fun loadOtherUserData() {
        val uid = FirebaseAuth.getInstance().uid.toString()
        val databaseReference =
            FirebaseDatabase.getInstance().getReference("userProfile").child(uid)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val users: Users? = snapshot.getValue(Users::class.java)
                    var name = users?.userName.toString()
                    Glide.with(requireActivity())
                        .load(users?.user_image.toString())
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(binding.profileImage)
                    binding.proName.setText(name)
                    binding.mail.setText(users?.email.toString())
                    binding.bloodGrp.setText(users?.bg.toString())
                    binding.phone.setText(users?.mobile.toString())
                    binding.address.setText(users?.address.toString())
                    users?.isDonate.let {
                        if (it != null) {
                            binding.isChecked.isChecked = it
                        }
                    }
                    Log.d("TAG", "onDataChange:  $name")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Error: ${error.message}")
            }
        })
    }


}