package com.metacoders.blood_donation.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.metacoders.blood_donation.R
import com.metacoders.blood_donation.databinding.FragmentAccountSetUpBinding
import com.metacoders.blood_donation.databinding.FragmentSignUpBinding


class SignUpFragment : Fragment() {

    lateinit var binding: FragmentSignUpBinding
    lateinit var mAuth: FirebaseAuth
    lateinit var ctx: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater,container,false)
        ctx = binding.root.context
        mAuth = FirebaseAuth.getInstance()

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signUp.setOnClickListener {
            val name = binding.username.editText?.text.toString()
            val email = binding.email.editText?.text.toString()
            val pass = binding.pass.editText?.text.toString()
            if (name.isNotEmpty()&& email.isNotEmpty()&&pass.isNotEmpty()){
                Log.d("TAG", "onViewCreated: $email")
                RegisterUser(name, email, pass)
            }
            else Toast.makeText(ctx, "ERROR!!!", Toast.LENGTH_SHORT).show()
        }

        binding.signIn.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
        }
    }

    private fun RegisterUser(name: String, email: String, pass: String) {
         mAuth.createUserWithEmailAndPassword(
             email,pass
         ).addOnSuccessListener {
             createUserDetails()
         }
             .addOnFailureListener {
                 Toast.makeText(ctx, "ERROR!!! ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
             }

    }

    private fun createUserDetails() {

        val bundle = bundleOf(
            "name" to binding.username.editText?.text.toString(),
            "email" to binding.email.editText?.text.toString()
        )
        findNavController().navigate(R.id.action_signUpFragment_to_accountSetUpFragment , bundle)
    }
}