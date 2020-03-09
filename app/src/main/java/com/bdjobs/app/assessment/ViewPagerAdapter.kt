package com.bdjobs.app.assessment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragment: Fragment, val status: String) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return TAB_TITLES.size
    }

    override fun createFragment(position: Int): Fragment {

        val fragment: Fragment

        return when (position) {
            0 -> {
                fragment = HomeFragment()
                fragment.arguments = Bundle().apply {
                    putString("status", status)
                }
                fragment
            }
            else -> {
                CertificateListFragment()
            }
        }
    }

    fun getPageTitle(position: Int): String {
        return TAB_TITLES[position]
    }

    companion object {
        private val TAB_TITLES = arrayOf(
                "Home",
                "Certificate List"
        )
    }
}