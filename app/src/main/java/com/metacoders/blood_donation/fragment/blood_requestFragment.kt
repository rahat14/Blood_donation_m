package com.metacoders.blood_donation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.metacoders.blood_donation.R
import com.metacoders.blood_donation.databinding.FragmentBloodRequestBinding


class blood_requestFragment : Fragment() {
    private lateinit var binding: FragmentBloodRequestBinding
    var bloodGroup = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBloodRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bloods = resources.getStringArray(R.array.blood_group)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, bloods)
        binding.bg.adapter = adapter

        binding.bg.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                bloodGroup = bloods[position].toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
                bloodGroup = ""
            }

        }


        binding.submitBtn.setOnClickListener {

            val phn = binding.ph.text.toString()
            val addres = binding.locationEt.text.toString()

            if (phn.isEmpty() || addres.isEmpty() || bloodGroup.contains("Blood")) {
                Toast.makeText(context, "Please Check Input Data ", Toast.LENGTH_SHORT).show()
            } else {
                uploadData(phn, addres)
            }


        }


    }

    private fun uploadData(phn: String, addres: String) {
        val mref = FirebaseDatabase.getInstance().getReference("Blood_Req")
        val key = mref.push().key.toString()
        val map = HashMap<String, Any>()
        map["userId"] = "d"
        map["userName"] = "d"
        map["address"] = addres
        map["phn"] = phn
        map["reqID"] = key
        map["reqBlood"] = bloodGroup
        map["time"] = System.currentTimeMillis() / 1000

        mref.child(key).setValue(map).addOnCompleteListener {
            Toast.makeText(context, "Data Requested", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(context, "Error : ${it.message}", Toast.LENGTH_LONG  ).show()
        }

    }
}