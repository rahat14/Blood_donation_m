package com.metacoders.blood_donation.fragment

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.metacoders.blood_donation.HelperClass
import com.metacoders.blood_donation.R
import com.metacoders.blood_donation.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {
    lateinit var binding: FragmentSignInBinding
    lateinit var ctx: Context
    lateinit var mAuth: FirebaseAuth
    var dialog : ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        ctx = binding.root.context
        mAuth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog = HelperClass.createProgressDialog(requireActivity() , "Logging In...")

        val user = FirebaseAuth.getInstance().currentUser
        if(user != null){
            findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
        }

        binding.signIn.setOnClickListener {
            val email = binding.email.editText?.text.toString()
            val pass = binding.pass.editText?.text.toString()

            if(email.isNotEmpty()||pass.isNotEmpty()){
                LogInUser(email,pass)
            }else Toast.makeText(ctx, "ERROR!!!", Toast.LENGTH_SHORT).show()
        }
        binding.signUp.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }

    private fun LogInUser(email: String, pass: String) {
        dialog?.show()
        mAuth.signInWithEmailAndPassword(email,pass)
            .addOnSuccessListener {task ->
                checkUser()
            }
            .addOnFailureListener {
                dialog?.dismiss()
                Toast.makeText(ctx, "ERROR!!! ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }

    }

    private  fun checkUser(){
        val uid = FirebaseAuth.getInstance().uid.toString()
        val mref = FirebaseDatabase.getInstance().getReference("userProfile")

        mref.child(uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                dialog?.dismiss()
                if(snapshot.exists()){

                    findNavController().navigate(R.id.action_signInFragment_to_homeFragment)

                }else {
                    // user data not found
                    findNavController()
                        .navigate(R.id.action_signInFragment_to_account_setup)


                }

            }

            override fun onCancelled(error: DatabaseError) {
                dialog?.dismiss()
                Toast.makeText(context , "Error : ${error.message}" , Toast.LENGTH_SHORT).show()
            }

        })
    }


}
