package com.metacoders.blood_donation.fragment


import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.metacoders.blood_donation.HelperClass
import com.metacoders.blood_donation.R
import com.metacoders.blood_donation.databinding.FragmentAccountSetUpBinding


class AccountSetUpFragment : Fragment() {
    lateinit var binding: FragmentAccountSetUpBinding
    private lateinit var mAuth: FirebaseAuth
    private var uri: Uri? = null
    private var uid: String = ""
    private var fileURL: String = ""
    var progressDialog: ProgressDialog? = null
    var someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            // There are no request codes
            val data = result.data
            if (data != null) {
                uri = data.data
                uri?.let { uploadImage(it) }
            } else {
                Toast.makeText(context, "Image No Found", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    //lateinit var ctx: Context
    var bg: String = " "

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance()
        binding = FragmentAccountSetUpBinding.inflate(inflater, container, false)
        uid = mAuth.uid.toString()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = HelperClass.createProgressDialog(requireActivity(), "Sending Image...")
        val bloods = resources.getStringArray(R.array.blood_group)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, bloods)
        binding.bloodGrp.adapter = adapter

        binding.bloodGrp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                bg = bloods[position].toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
                bg = ""
            }

        }
        binding.mail.text = arguments?.getString("email").toString()
        binding.name.text = arguments?.getString("username").toString()

        binding.profileImage.setOnClickListener {
            openImagePicker()
        }

        binding.submitBtn.setOnClickListener {
            val phone = binding.ph.text.toString()
            val address = binding.location.text.toString()
            val isdone = binding.checkbox.isSelected
            val uid = mAuth.uid.toString()

            if (phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(context, "Please Check Data", Toast.LENGTH_SHORT).show()
            } else {
                val dialog = HelperClass.createProgressDialog(requireActivity(), "Updating Profile")
                dialog.show()
                val hashMap: HashMap<String, Any> = HashMap()
                hashMap["mobile"] = phone
                hashMap["address"] = address
                hashMap["isDonate"] = isdone
                hashMap["bg"] = bg
                hashMap["uid"] = uid
                hashMap["user_image"] = fileURL
                hashMap["email"] = arguments?.getString("email").toString()
                hashMap["userName"] = arguments?.getString("name").toString()

                val ref = FirebaseDatabase.getInstance().getReference("userProfile")
                ref.child(uid).setValue(hashMap).addOnCompleteListener {
                    dialog.dismiss()
                    Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_accountSetUpFragment_to_homeFragment)
                }.addOnFailureListener {
                    dialog.dismiss()
                    Toast.makeText(context, "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }


        }

    }

    private fun openImagePicker() {
        ImagePicker.with(this)
            .createIntent { intent ->
                someActivityResultLauncher.launch(intent)
            }
    }

    private fun uploadImage(imageUri: Uri) {
        progressDialog?.show()
        val fileId_local = uid + System.currentTimeMillis()
        val myStorage: StorageReference =
            FirebaseStorage.getInstance().getReference("Uploads").child(uid.toString())
                .child(fileId_local)
        myStorage.putFile(imageUri).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                myStorage.downloadUrl.addOnSuccessListener { uri ->
                    fileURL = uri.toString()
                    progressDialog?.dismiss()
                }
            }
        }
    }

}