package com.metacoders.blood_donation.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.metacoders.blood_donation.R
import com.metacoders.blood_donation.databinding.FragmentSignInBinding

class SignInFragment : Fragment() {
    lateinit var binding: FragmentSignInBinding
    lateinit var ctx: Context
    lateinit var mAuth: FirebaseAuth

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

        binding.signIn.setOnClickListener {
            val email = binding.email.editText.toString()
            val pass = binding.pass.editText.toString()

            if(email.isNotEmpty()||pass.isNotEmpty()){
                LogInUser(email,pass)
            }else Toast.makeText(ctx, "ERROR!!!", Toast.LENGTH_SHORT).show()
        }
        binding.signUp.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }
    }

    private fun LogInUser(email: String, pass: String) {
        mAuth.signInWithEmailAndPassword(email,pass)
            .addOnSuccessListener {task ->
                val uid = task.user?.uid
                findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
            }
            .addOnFailureListener {
                Toast.makeText(ctx, "ERROR!!! ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }

    }

}
