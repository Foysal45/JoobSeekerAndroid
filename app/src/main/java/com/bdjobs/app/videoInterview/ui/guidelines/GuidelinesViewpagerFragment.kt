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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.viewpager2.widget.ViewPager2
import com.bdjobs.app.R
import com.bdjobs.app.utilities.hide
import com.bdjobs.app.utilities.show
import com.bdjobs.app.videoInterview.data.models.Guideline
import kotlinx.android.synthetic.main.fragment_guidelines_viewpager.*

class GuidelinesViewpagerFragment : Fragment() {

    val args : GuidelinesViewpagerFragmentArgs by navArgs()
    private val guidelineVIewModel: GuidelineVIewModel by navGraphViewModels(R.id.video_interview_nav_graph)


    val images = listOf<Int>(
            R.drawable.ic_video_guideline1,
            R.drawable.ic_video_guideline2,
            R.drawable.ic_video_guideline3,
            R.drawable.ic_video_guideline4
    )

    val instructionsInBangla = listOf<String>(
            "ভিডিও রেকর্ডিং এর শুরুতে ডিভাইস এর মাইক্রোফোন এবং ক্যামেরা পারমিশন দিতে হবে।",
            "উত্তর রেকর্ড এর সময় মাউথ পিস / হেডফোন ব্যবহার করা অপরিহার্য, যেন নিয়োগকর্তা আপনার কথা সহজেই বুঝতে পারে",
            "রেকর্ড করা উত্তরগুলো ১ ঘন্টার মধ্যে জমা দিন।",
            "উত্তর রেকর্ডটি আগেই শেষ হয়ে গেলে “Submit(all Videos)” বাটনে ক্লিক করুন।"
    )

    val instructionsInEnglish = listOf<String>(
            "Enable microphone and camera permissions before start recording.",
            "While recording answers, using headphones with the attached microphone is recommended so that the employer can clearly understand you.",
            "Submit all recorded answers within 1hour.",
            "If answer recording is complete, click on “Submit(all Videos)” button"
    )

    private val guidelines = mutableListOf<Guideline>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_guidelines_viewpager, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.language?.let {
            when(it){
                "bangla"->{
                    for (i in 0 until 4) {
                        guidelines.add(Guideline(images[i], instructionsInBangla[i]))
                    }
                    btn_next.text = "পরবর্তী নির্দেশনা"
                }
                else ->{
                    for (i in 0 until 4) {
                        guidelines.add(Guideline(images[i], instructionsInEnglish[i]))
                    }
                    btn_next.text = "Next Guideline"
                }
            }
        }

        view_pager_guideline.adapter = GuidelineAdapter(requireContext(), guidelines)
        setupIndicators()
        setCurrentIndicator(0)
        view_pager_guideline?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == guidelines.size - 1) {
                    btn_next.hide()
                    btn_skip.hide()
                    btn_start.show()
                    ll_indicators.visibility = View.INVISIBLE
                    tv_click_instruction?.show()
                    tv_click_instruction.text = getText(R.string.by_clicking_lets_start_now)
                } else {
                    btn_start.hide()
                    btn_next.show()
                    btn_skip.show()
                    ll_indicators.show()
                    tv_click_instruction?.hide()
                }

                setCurrentIndicator(position)
            }
        })

        img_close?.setOnClickListener {
            findNavController().popBackStack()
        }

        btn_next?.setOnClickListener {
            if (view_pager_guideline.currentItem + 1 < guidelines.size) {
                view_pager_guideline.currentItem = view_pager_guideline.currentItem + 1
            } else {
                Toast.makeText(context, "finished", Toast.LENGTH_SHORT).show()
            }
        }
        btn_start?.setOnClickListener {
            findNavController().navigate(R.id.action_guidelinesViewpagerFragment_to_questionListFragment)
        }
        btn_skip?.setOnClickListener {
            findNavController().navigate(R.id.action_guidelinesViewpagerFragment_to_questionListFragment)
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