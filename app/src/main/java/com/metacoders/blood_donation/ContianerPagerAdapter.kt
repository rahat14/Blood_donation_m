package com.metacoders.blood_donation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.metacoders.blood_donation.fragment.MsgFragment
import com.metacoders.blood_donation.fragment.ReqListFragment


class ContianerPagerAdapter(fragmentActivity: Fragment) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ReqListFragment()
            2 -> MsgFragment()


            else -> { // Note the block
                ReqListFragment()
            }
        }

    }
}