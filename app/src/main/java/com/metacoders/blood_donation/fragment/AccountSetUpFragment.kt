package com.metacoders.blood_donation.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.metacoders.blood_donation.R
import com.metacoders.blood_donation.databinding.FragmentAccountSetUpBinding


class AccountSetUpFragment : Fragment() {
    lateinit var binding: FragmentAccountSetUpBinding
    private lateinit var mAuth: FirebaseAuth
    //lateinit var ctx: Context
    var bg: String = " "
    var list_of_items = arrayListOf<String>("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance()
        binding = FragmentAccountSetUpBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}