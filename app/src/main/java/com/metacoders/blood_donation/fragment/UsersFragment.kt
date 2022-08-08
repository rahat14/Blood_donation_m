package com.metacoders.blood_donation.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.metacoders.blood_donation.databinding.FragmentUsersBinding
import com.metacoders.blood_donation.model.ChatHistoryModel
import com.metacoders.blood_donation.model.Users


class UsersFragment : Fragment(), UserListAdapter.Interaction {
    var bg: String = "ALL"
    private lateinit var binding: FragmentUsersBinding
    private lateinit var mAdapter: UserListAdapter
    private var userlist: MutableList<Users> = mutableListOf()
    val bloodList = arrayListOf<String>("ALL", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    private var filertedUserlist: MutableList<Users> = mutableListOf()
    val userId = FirebaseAuth.getInstance().uid.toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUsersBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter = UserListAdapter(this)
        binding.list.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        val aa = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, bloodList)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.bloodGrp.adapter = aa

        binding.bloodGrp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                bg = bloodList[position].toString()
                filterList()

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    override fun onItemSelected(position: Int, item: Users) {
        searchChatHistory(item.uid.toString())
    }

    override fun onResume() {
        loadUser()
        super.onResume()
    }

    private fun loadUser() {
        userlist.clear()

        val mref = FirebaseDatabase.getInstance().reference

        mref.child("userProfile").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userlist.clear()
                for (ds in dataSnapshot.children) {
                    val eventModel: Users? = ds.getValue(Users::class.java)

                    if (eventModel != null) {
                        userlist.add(eventModel)
                    }

                }

                mAdapter.submitList(userlist)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun filterList() {
        filertedUserlist.clear()
        for (item in userlist) {

            if (bg == "ALL") {
                filertedUserlist.add(item)
            } else if (bg == item.bg) {
                filertedUserlist.add(item)
            }

        }

        val newList: MutableList<Users> = mutableListOf()
        newList.addAll(filertedUserlist)


        mAdapter.submitList(null)
        mAdapter.submitList(ArrayList(newList))

    }

    private fun searchChatHistory(uid: String) {

        val ref = FirebaseDatabase.getInstance().getReference("ChatHistory")
        ref.child(userId + uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // found the data
                    GotoChatPage(userId + uid , uid)
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

    private fun GotoChatPage(key: String, uid: String) {
        val intent = Intent(context, ChatActivity::class.java)
        intent.putExtra("chatID", key)
        intent.putExtra("otherUserID", uid)
        startActivity(intent)
    }
}