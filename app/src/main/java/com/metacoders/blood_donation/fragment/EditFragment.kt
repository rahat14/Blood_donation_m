package com.metacoders.blood_donation.fragment

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.metacoders.blood_donation.HelperClass
import com.metacoders.blood_donation.R
import com.metacoders.blood_donation.databinding.FragmentEditBinding
import com.metacoders.blood_donation.databinding.FragmentProfileBinding
import com.metacoders.blood_donation.model.Users


class EditFragment : Fragment() {


    private lateinit var binding: FragmentEditBinding

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        uid = FirebaseAuth.getInstance().uid.toString()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadOtherUserData()
        binding.saveIcon.setOnClickListener {
            val phone = binding.phone.text.toString()
            val address = binding.address.text.toString()
            val uid = FirebaseAuth.getInstance().uid.toString()

            if (phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(context, "Please Check Data", Toast.LENGTH_SHORT).show()
            } else {
                val dialog = HelperClass.createProgressDialog(requireActivity(), "Updating Profile")
                dialog.show()
                val hashMap: HashMap<String, Any> = HashMap()
                hashMap["mobile"] = phone
                hashMap["address"] = address
                hashMap["user_image"] = fileURL
                hashMap["userName"] = binding.name.text.toString()

                val ref = FirebaseDatabase.getInstance().getReference("userProfile")
                ref.child(uid).updateChildren(hashMap).addOnCompleteListener {
                    dialog.dismiss()
                    Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                    val navOptions: NavOptions =
                        NavOptions.Builder().setPopUpTo(R.id.profileFragment, true).build()
                    findNavController().navigate(R.id.profileFragment, null, navOptions)
                }.addOnFailureListener {
                    dialog.dismiss()
                    Toast.makeText(context, "Error : ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }


        }
        binding.profileImage.setOnClickListener {
            openImagePicker()
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
                    val name = users?.userName.toString()
                    Glide.with(requireActivity())
                        .load(users?.user_image.toString())
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(binding.profileImage)
                    binding.name.setText(name)
                    fileURL = users?.user_image.toString()
                    binding.bloodGrp.setText(users?.bg.toString())
                    binding.phone.setText(users?.mobile.toString())
                    binding.address.setText(users?.address.toString())

                    Log.d("TAG", "onDataChange:  $name")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Error: ${error.message}")
            }
        })
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