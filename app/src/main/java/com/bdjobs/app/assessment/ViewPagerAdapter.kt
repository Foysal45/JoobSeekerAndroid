package com.bdjobs.app.assessment

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return TAB_TITLES.size
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> HomeFragment()
            1-> CertificateListFragment()
            else -> HomeFragment()
        }
    }

    fun getPageTitle(position: Int) : String
    {
        return TAB_TITLES[position]
    }

    companion object {
        private val TAB_TITLES = arrayOf(
                "Home",
                "Certificate List"
        )
    }
}