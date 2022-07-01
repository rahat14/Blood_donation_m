package com.metacoders.blood_donation.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.metacoders.blood_donation.R
import com.metacoders.blood_donation.databinding.FragmentMsgBinding
import com.metacoders.blood_donation.model.ChatHistoryModel
import com.metacoders.blood_donation.model.Users
import de.hdodenhof.circleimageview.CircleImageView


class MsgFragment : Fragment() {
    private var firebaseRecyclerAdapter: FirebaseRecyclerAdapter<ChatHistoryModel, chatListingViewholdeers>? =
        null
    var options: FirebaseRecyclerOptions<ChatHistoryModel>? = null
    var uid = ""

    private lateinit var binding: FragmentMsgBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMsgBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uid = FirebaseAuth.getInstance().uid.toString()
        val llm = LinearLayoutManager(context)
        llm.reverseLayout = true
        llm.stackFromEnd = true
        binding.list.layoutManager = llm

    }

    private fun loadData() {
        val histroyref = FirebaseDatabase.getInstance().getReference("ChatHistory")
        val query = histroyref.orderByChild("timestamp")
        var fref = FirebaseDatabase.getInstance().getReference("userProfile")
        fref.keepSynced(true)
        histroyref.keepSynced(true)
        options = FirebaseRecyclerOptions.Builder<ChatHistoryModel>()
            .setQuery(query, ChatHistoryModel::class.java)
            .build()
        firebaseRecyclerAdapter =
            object : FirebaseRecyclerAdapter<ChatHistoryModel, chatListingViewholdeers>(
                options!!
            ) {
                protected override fun onBindViewHolder(
                    viewholder: chatListingViewholdeers,
                    i: Int,
                    chatHistoryModel: ChatHistoryModel
                ) {
                    var lastMsg: String = chatHistoryModel.lastMsg
                    if (lastMsg.isEmpty()) {
                        lastMsg = "Start Chat"
                    }
                    if (chatHistoryModel.user1.equals(uid)) {
                        //  user1 is me // user2 is my friend
                        // download the friend data
                        chatHistoryModel.user2.let {
                            fref.child(it)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val model: Users? = dataSnapshot.getValue(Users::class.java)
                                        viewholder.setNameTv(model?.userName.toString())
                                        viewholder.setPp(model?.user_image.toString(), context)
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {}
                                })
                        }
                        viewholder.setLastMsgTv(lastMsg)
                    }
                    else if (chatHistoryModel.user2.toString() == uid) {

                        //  user2  is me // user1 is my friend
                        // download the friend data
                        chatHistoryModel.user1?.let {
                            fref.child(it)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val model: Users? = dataSnapshot.getValue(Users::class.java)
                                        viewholder.setNameTv(model?.userName.toString())
                                        viewholder.setPp(model?.user_image.toString(), context)
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {}
                                })
                        }
                        viewholder.setLastMsgTv(lastMsg)
                    }
                    else {
                        //this row not mine
                        viewholder.container?.visibility = View.GONE
                        val params = viewholder.container?.layoutParams
                        params?.height = 0
                        viewholder.container?.layoutParams = params
                        viewholder.setHideCard()
                    }


                    viewholder.container?.setOnClickListener {
                        Log.d("TAG", "onBindViewHolder: ")
                        val chatID: String = chatHistoryModel.msg
                        if (chatHistoryModel.user1.equals(uid)) {
                            val friendUserID: String = chatHistoryModel.user2
                            val o = Intent(context, ChatActivity::class.java)
                            o.putExtra("chatID", chatID)
                            o.putExtra("otherUserID", friendUserID)
                            startActivity(o)
                        } else {
                            val friendUserID: String = getItem(i).user1.toString()
                            val o = Intent(context, ChatActivity::class.java)
                            o.putExtra("chatID", chatID)
                            o.putExtra("otherUserID", friendUserID)

                            startActivity(o)
                        }

                    }


                }

                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): chatListingViewholdeers {
                    val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_for_chat_display, parent, false)
                    return chatListingViewholdeers(view)
                }
            }
        val llm = LinearLayoutManager(context)
        llm.reverseLayout = true
        llm.stackFromEnd = true
        binding.list.layoutManager = llm
        firebaseRecyclerAdapter?.startListening()
        binding.list.adapter = firebaseRecyclerAdapter
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
}


class chatListingViewholdeers(var mView: View) :
    RecyclerView.ViewHolder(mView) {
    var nameTv: TextView? = null
    var lastMsg: TextView? = null
    var pp: CircleImageView? = null
    var container: ConstraintLayout? = null
    var card : MaterialCardView? = null

    fun  setHideCard(){
        container = mView.findViewById(R.id.full_layout)
        card = mView.findViewById(R.id.full_layout_Card)
        card?.visibility = View.GONE
    }

    fun setNameTv(name: String?) {
        container = mView.findViewById(R.id.full_layout)
        nameTv = mView.findViewById<View>(R.id.display_name) as TextView
        nameTv?.text = name
    }

    fun setLastMsgTv(lastMsgTv: String) {
        container = mView.findViewById(R.id.full_layout)
        lastMsg = mView.findViewById<View>(R.id.lastMsg) as TextView
        lastMsg!!.text = lastMsgTv + ""
    }

    fun setPp(link: String?, context: Context?) {
        container = mView.findViewById(R.id.full_layout)
        pp = mView.findViewById(R.id.profile_image)
        Glide.with(context!!).load(link)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(pp!!)
    }

    interface ClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    fun setOnClickListener(clickListener: ClickListener?) {
        mClickListener = clickListener
    }

    companion object {
        private var mClickListener: ClickListener? = null
    }

}