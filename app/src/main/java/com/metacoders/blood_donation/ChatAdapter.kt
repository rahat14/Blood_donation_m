package com.metacoders.blood_donation

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.metacoders.blood_donation.model.chatMsgModel
import java.util.*

class ChatAdapter(
    private val context: Context,
    private val chatList: MutableList<chatMsgModel>
) : RecyclerView.Adapter<ChatAdapter.myHolder>() {

    private val MSG_TYPE_RIGHT = 1
    private val MSG_TYPE_LEFT = 0
    private val IMAGE_MSG_TYPE_RIGHT = 5
    private val IMAGE_MSG_TYPE_LEFT = 6
    var uid = FirebaseAuth.getInstance().uid.toString()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myHolder {
        var view: View? = null
        when (viewType) {
            MSG_TYPE_LEFT -> {
                view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.chat_item_left, parent, false)
            }
            MSG_TYPE_RIGHT -> {
                view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.chat_item_right, parent, false)
            }
            IMAGE_MSG_TYPE_LEFT -> {
                view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.chat_item_left, parent, false)
            }
            IMAGE_MSG_TYPE_RIGHT -> {
                view = LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.chat_item_right, parent, false)
            }
        }
        return myHolder(view!!)
    }

    fun sendList(): MutableList<chatMsgModel> {
        return chatList
    }


    override fun onBindViewHolder(holder: myHolder, position: Int) {
        val msg: String = chatList[position].msg.toString()
        holder.msgView.text = msg
        //  holder.date.setText(ConvertTime.getTimeAgo(Date(chatList!![position].time?.times(1000) ?: 0)))
        if (!chatList[position].content_type.equals("null") && !chatList[position].content_type.equals("null")
        ) {
            Glide.with(context)
                .load(chatList[position].content_link)
                .into(holder.imageView)
            holder.imageView.visibility = View.VISIBLE
            //holder.imageView.setOnClickListener(v -> Toast.makeText(contextt, chatList.get(position).getContent_link() + "", Toast.LENGTH_LONG).show());
        }else {
            holder.imageView.visibility = View.GONE
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].uid.equals(uid) && chatList[position].content_type.equals("null")
        ) {
            MSG_TYPE_RIGHT
        } else if (chatList[position].uid
                .equals(uid) && !chatList[position].content_type.equals("null")
        ) {
            IMAGE_MSG_TYPE_RIGHT
        } else if (chatList[position].uid
                .equals(uid) && chatList[position].content_type.equals("null")
        ) {
            MSG_TYPE_LEFT
        } else {
            IMAGE_MSG_TYPE_LEFT
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }


    class myHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var msgView: TextView
        var imageView: ImageView
        private fun setImageView(url: String, context: Context) {
            imageView = itemView.findViewById(R.id.image)
            Glide.with(context).load(url).into(imageView)
        }

        init {
            msgView = itemView.findViewById(R.id.message)
            imageView = itemView.findViewById(R.id.image)
        }
    }


}
