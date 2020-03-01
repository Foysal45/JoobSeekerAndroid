package com.bdjobs.app.assessment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bdjobs.app.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * A simple [Fragment] subclass.
 */
class ViewPagerFragment : Fragment() {

    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var viewPager: ViewPager2
    lateinit var tabLayout : TabLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_pager, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var a : String? = ""

        try {
            val status = ViewPagerFragmentArgs.fromBundle(arguments!!).status
            a = status
        } catch (e: Exception)
        {
            e.printStackTrace()
        }

        Log.d("rakibe", "$a")

        viewPagerAdapter = ViewPagerAdapter(this,a!!)
        viewPager = view.findViewById(R.id.pager)
        viewPager.adapter = viewPagerAdapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        tabLayout  = view.findViewById(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = ViewPagerAdapter(this,a).getPageTitle(position)
        }.attach()
    }


}
