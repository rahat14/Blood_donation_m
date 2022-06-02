package com.metacoders.blood_donation.fragment


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

        loadBloodGroup()
        binding.mail.text = arguments?.getString("email").toString()
        binding.name.text = arguments?.getString("username").toString()

        binding.submitBtn.setOnClickListener {
            val phone = binding.ph.text.toString()
            val address = binding.location.text.toString()
            val isdone = binding.checkbox.isSelected
            val uid = mAuth.uid.toString()

            val hashMap: HashMap<String,Any> = HashMap()
            hashMap["phone"] = phone
            hashMap["address"] = address
            hashMap["lat"] = 0.0
            hashMap["lon"] = 0.0
            hashMap["isDonate"] = isdone
            hashMap["bg"] = bg
            hashMap["uid"] = uid
            hashMap["mail"] = arguments?.getString("mail").toString()
            hashMap["name"] = arguments?.getString("name").toString()
        }

    }

    private fun loadBloodGroup() {

    }

}