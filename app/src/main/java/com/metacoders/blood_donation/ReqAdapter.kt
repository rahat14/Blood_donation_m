package com.metacoders.blood_donation

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import com.metacoders.blood_donation.databinding.RowForDonationPostBinding
import com.metacoders.blood_donation.model.BloodReq
import java.util.*

class ReqAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BloodReq>() {

        override fun areItemsTheSame(oldItem: BloodReq, newItem: BloodReq): Boolean {
            return oldItem.reqID == newItem.reqID
        }

        override fun areContentsTheSame(oldItem: BloodReq, newItem: BloodReq): Boolean {
            return oldItem == newItem
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return viewholder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_for_donation_post,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is viewholder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun submitList(list: List<BloodReq>) {
        differ.submitList(list)
    }

    class viewholder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        val binding = RowForDonationPostBinding.bind(itemView)

        fun bind(item: BloodReq) {
            binding.chat.setOnClickListener {
                interaction?.onItemSelected(absoluteAdapterPosition, item)
            }

            binding.ph.text  = item.phn
            binding.name.text = item.userName
            binding.bloodGrp.text= item.reqBlood.toString()
            binding.text3.text = ConvertTime.getTimeAgo(
                Date(
                    item.time * 1000
                )
            )


        }
    }

    interface Interaction {
        fun onItemSelected(position: Int, item: BloodReq)
    }
}
