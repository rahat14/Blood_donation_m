package com.metacoders.blood_donation.fragment

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.metacoders.blood_donation.ChatAdapter
import com.metacoders.blood_donation.HelperClass
import com.metacoders.blood_donation.databinding.ActivityChatBinding
import com.metacoders.blood_donation.model.Users
import com.metacoders.blood_donation.model.chatMsgModel
import de.hdodenhof.circleimageview.CircleImageView

class ChatActivity : AppCompatActivity() {
    var uid: String = ""
    var otherUserId = ""
    var sendBtn: ImageView? = null
    var name = ""

    var mref: DatabaseReference? = null
    lateinit var chatAdapter: ChatAdapter
    var chatID: String? = null
    var loadedChat: MutableList<chatMsgModel> = ArrayList()
    var llm: LinearLayoutManager? = null
    var uri: Uri? = null
    var userImage: CircleImageView? = null
    var userName: TextView? = null
    var someActivityResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            // There are no request codes
            val data = result.data
            if (data != null) {
                uri = data.data
                uri?.let { uploadImage(it) }
            } else {
                Toast.makeText(applicationContext, "Image No Found", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    var progressDialog: ProgressDialog? = null
    lateinit var binding: ActivityChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressDialog = HelperClass.createProgressDialog(this@ChatActivity, "Sending Image...")
        uid = FirebaseAuth.getInstance().uid.toString()
        intent = intent
        chatID = intent.getStringExtra("chatID").toString()
        otherUserId = intent.getStringExtra("otherUserID").toString()
        val llm = LinearLayoutManager(applicationContext)
        llm.stackFromEnd = true
        chatAdapter = ChatAdapter(applicationContext, loadedChat)
        binding.list.adapter = chatAdapter

        binding.list.setHasFixedSize(true)

        binding.list.layoutManager = llm
        mref = FirebaseDatabase.getInstance().getReference("ChatHistory").child(chatID!!)

        binding.cameraBtn.setOnClickListener { openImagePicker() }

        binding.messageSendBtn.setOnClickListener {
            val msg = binding.messageInput.text.toString()
            if (msg.isNotEmpty()) {
                sendTheMsg(msg.trim { it <= ' ' })
            }
        }


    }

    private fun downloadMsg() {
        loadedChat = chatAdapter.sendList()
        mref!!.child("Chats").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                loadedChat.clear()
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        val chat = ds.getValue(chatMsgModel::class.java)

                        // Log.d("MSGESDSS" , chat.getMsg()  );
                        if (chat != null) {
                            loadedChat.add(chat)
                        }
                    }
                    //

                    // set adapter
                    chatAdapter.notifyDataSetChanged()
                    try {
                        binding.list.smoothScrollToPosition(loadedChat.size - 1)
                    } catch (exception: Exception) {
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun sendTheMsg(msg: String) {
        val msg_ID = mref?.push()?.key.toString()
        // String msg , msg_id , uid , time   ;
        val msgModel = chatMsgModel(
            msg.trim { it <= ' ' },
            msg_ID,
            uid,
            "null",
            "null",
            System.currentTimeMillis() / 1000
        )
        mref!!.child("Chats").child(msg_ID).setValue(msgModel).addOnCompleteListener {
            binding.messageInput.setText("")
            writeChatHistory(msg)
        }
    }

    private fun writeChatHistory(msg: String) {
        val map = HashMap<String, Any>()
        map["lsatMsgSender"] = uid!!
        map["lastMsg"] = msg
        map["timestamp"] = System.currentTimeMillis() / 1000
        mref?.updateChildren(map)
    }


    private fun uploadImage(imageUri: Uri) {
        progressDialog!!.show()
        val fileId_local = uid + System.currentTimeMillis()
        val myStorage: StorageReference =
            FirebaseStorage.getInstance().getReference("Uploads").child(uid.toString())
                .child(fileId_local)
        myStorage.putFile(imageUri).addOnCompleteListener { task ->
            if (task.isSuccessful()) {
                myStorage.getDownloadUrl().addOnSuccessListener { uri ->
                    val fileUrl: String = java.lang.String.valueOf(uri)
                    progressDialog!!.dismiss()
                    sendMediaMsg(fileUrl, "")
                }
            }
        }
    }

    private fun sendMediaMsg(msg: String, str: String) {
        val msg_ID = mref?.push()?.key.toString()
        // String msg , msg_id , uid , time   ;
        val msgModel = chatMsgModel(
            str, msg_ID, uid, "image",
            "" + msg, System.currentTimeMillis() / 1000
        )
        mref?.child("Chats")?.child(msg_ID.toString())?.setValue(msgModel)?.addOnCompleteListener {
            binding.messageInput.setText("")
            writeChatHistory(msg)
        }
    }

    override fun onResume() {
        super.onResume()
        loadOtherUserData()
        downloadMsg()
    }

    private fun loadOtherUserData() {
        val databaseReference =
            FirebaseDatabase.getInstance().getReference("userProfile").child(otherUserId)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val users: Users? = snapshot.getValue(Users::class.java)
                    name = users?.userName.toString()
                    Glide.with(applicationContext)
                        .load(users?.user_image.toString())
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(binding.userImage)
                    binding.headerTitle.text = name
                    Log.d("TAG", "onDataChange:  $name")
                }else {
                    Log.d("TAG", "not found: $otherUserId")
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
}