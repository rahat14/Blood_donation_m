package com.metacoders.blood_donation.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.metacoders.blood_donation.ReqAdapter
import com.metacoders.blood_donation.databinding.FragmentReqListBinding
import com.metacoders.blood_donation.model.BloodReq
import com.metacoders.blood_donation.model.ChatHistoryModel


class ReqListFragment : Fragment(), ReqAdapter.Interaction {

    private lateinit var binding: FragmentReqListBinding
    private lateinit var mAdapter: ReqAdapter
    private var list: MutableList<BloodReq> = ArrayList()
    val userId = FirebaseAuth.getInstance().uid.toString()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentReqListBinding.inflate(layoutInflater, container, false)
        mAdapter = ReqAdapter(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.list.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }


    }

    override fun onResume() {
        super.onResume()
        loadList()
    }

    private fun loadList() {
        list.clear()
        val mref = FirebaseDatabase.getInstance().getReference("Blood_Req")
        mref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (item in snapshot.children) {

                        val model: BloodReq? = item.getValue(BloodReq::class.java)
                        if (model != null) {
                            list.add(model)
                        }

                    }
                    list.reverse()
                    mAdapter.submitList(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error : ${error.message}", 1).show()
            }

        })
    }

    override fun onItemSelected(position: Int, item: BloodReq) {
        searchChatHistory(item.userId.toString() )

    }

    private fun GotoChatPage(key: String, uid: String) {
        val intent = Intent(context, ChatActivity::class.java)
        intent.putExtra("chatID", key)
        intent.putExtra("otherUserID", uid)
        startActivity(intent)
    }


    private fun searchChatHistory(uid: String) {

        val ref = FirebaseDatabase.getInstance().getReference("ChatHistory")
        ref.child(userId + uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // found the data
                    GotoChatPage(userId + uid , uid )
                } else {
                    ref.child(uid + userId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    // found the chat
                                    GotoChatPage(uid + userId, uid)
                                } else {
                                    // no chat have to create
                                    createChat(uid + userId, uid)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun createChat(key: String, uid: String) {
        //user1 , user2  , lsatMsgSender , lastMsg ;
        //    long timestamp  ;
        Log.d("TAG", "createChat: $key")
        val historyModel = ChatHistoryModel(
            uid, userId, "", "", key,
            System.currentTimeMillis() / 1000
        )
        val ref = FirebaseDatabase.getInstance().getReference("ChatHistory")
        ref.child(key).setValue(historyModel).addOnCompleteListener { task: Task<Void?> ->
            if (task.isSuccessful) {
                GotoChatPage(key, uid)
            }
        }
    }


}