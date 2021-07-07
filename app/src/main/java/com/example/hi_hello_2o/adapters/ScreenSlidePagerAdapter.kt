package com.example.hi_hello_2o.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.hi_hello_2o.InboxFragment
import com.example.hi_hello_2o.PeopleFragment

// adapter to slide between two tabs
class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int =2

    override fun createFragment(position: Int): Fragment= when(position) {
        0-> InboxFragment()
        else -> PeopleFragment()

    }

}
