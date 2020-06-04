package com.bdjobs.app.videoInterview.ui.guidelines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.videoInterview.data.models.Guideline
import kotlinx.android.synthetic.main.fragment_guidelines_viewpager.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GuidelinesViewpagerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GuidelinesViewpagerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val guidelines = listOf(
            Guideline("Guideline 1"),
            Guideline("Guideline 2"),
            Guideline("Guideline 3"),
            Guideline("Guideline 4"),
            Guideline("Guideline 5")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_guidelines_viewpager, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GuidelinesViewpagerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                GuidelinesViewpagerFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        view_pager_guideline.adapter = GuidelineAdapter(requireContext(), guidelines)
        setupIndicators()
        setCurrentIndicator(0)
        view_pager_guideline?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == guidelines.size-1){
                    btn_next.hide()
                    btn_skip.hide()
                    btn_start.show()
                    ll_indicators.hide()
                } else{
                    btn_start.hide()
                    btn_next.show()
                    btn_skip.show()
                    ll_indicators.show()
                }

                setCurrentIndicator(position)
            }
        })

        btn_next?.setOnClickListener {
            if (view_pager_guideline.currentItem + 1 < guidelines.size){
                view_pager_guideline.currentItem = view_pager_guideline.currentItem + 1
            } else {
                Toast.makeText(context,"finished",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupIndicators() {
        var indicators = mutableListOf<ImageView>()
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in guidelines.indices) {
            indicators.add(
                    ImageView(context).apply {
                        setImageDrawable(ContextCompat.getDrawable(context, R.drawable.indicator_inactive))
                    }
            )
            indicators[i].layoutParams = layoutParams
            ll_indicators.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        for (i in 0 until ll_indicators.childCount) {
            val imageView: ImageView = ll_indicators.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.indicator_active))
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.indicator_inactive))
            }

        }
    }
}