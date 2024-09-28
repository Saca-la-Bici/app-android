package com.kotlin.sacalabici.framework.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kotlin.sacalabici.framework.views.fragments.EventosFragment
import com.kotlin.sacalabici.framework.views.fragments.RodadasFragment
import com.kotlin.sacalabici.framework.views.fragments.TalleresFragment

class ActivitiesPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RodadasFragment()
            1 -> EventosFragment()
            2 -> TalleresFragment()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }
}