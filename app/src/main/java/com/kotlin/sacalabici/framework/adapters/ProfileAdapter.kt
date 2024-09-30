package com.kotlin.sacalabici.framework.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kotlin.sacalabici.framework.adapters.views.fragments.EventFragment
import com.kotlin.sacalabici.framework.adapters.views.fragments.GlobalFragment
import com.kotlin.sacalabici.framework.adapters.views.fragments.MedalsFragment

class ProfileAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> EventFragment()
            1 -> MedalsFragment()
            2 -> GlobalFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}