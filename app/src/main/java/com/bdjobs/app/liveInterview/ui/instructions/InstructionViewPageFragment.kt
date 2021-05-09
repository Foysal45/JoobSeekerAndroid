package com.bdjobs.app.liveInterview.ui.instructions

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.liveInterview.data.models.Instructions
import kotlinx.android.synthetic.main.fragment_instruction_view_page.*

class InstructionViewPageFragment : Fragment() {

    private val args: InstructionViewPageFragmentArgs by navArgs()

    private val images = listOf(
            R.drawable.ic_video_guideline1,
            R.drawable.ic_video_guideline2,
            R.drawable.ic_live_interview_3,
    )

    private val instructionsInBengali = listOf<String>(
            "লাইভ ইন্টারভিউ এর শুরুতে ডিভাইস এর মাইক্রোফোন এবং ক্যামেরা পারমিশন দিতে হবে",
            "লাইভ ইন্টারভিউ এর সময় মাউথ পিস / হেডফোন ব্যবহার করা অপরিহার্য, যেন নিয়োগকর্তা আপনার কথা সহজেই বুঝতে পারে",
            "শান্ত এবং নিরিবিলি স্থানে অবস্থান করে লাইভ ইন্টারভিউ তে অংশগ্রহণ করুন যাতে নিয়োগকর্তা এবং আপনার যোগাযোগ এ বিঘ্ন না ঘটে",
    )

    private val instructionsInEnglish = listOf<String>(
            "Allow device's microphone and camera permission to start Live Interview.",
            "Use mouthpieces/ headphones during the Live Interview so that the employer can easily understand you.",
            "Try to attend Live Interview in a quiet place & avoid interruption.",
    )

    private val instructions = mutableListOf<Instructions>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_instruction_view_page, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.language?.let {
            when(it){
                "bangla"->{
                    for (i in 0 until 3) {
                        instructions.add(Instructions(images[i], instructionsInBengali[i]))
                    }
                    btn_next.text = "পরবর্তী গাইড"
                }
                else ->{
                    for (i in 0 until 3) {
                        instructions.add(Instructions(images[i], instructionsInEnglish[i]))
                    }
                    btn_next.text = "Next guide"
                }
            }
        }

        view_pager_guideline.adapter = InstructionAdapter(requireContext(), instructions)
        setupIndicators()
        setCurrentIndicator(0)

        view_pager_guideline?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                if (position == instructions.size - 1) {
                    btn_next.hide()
                    btn_skip.hide()
                    btn_start.show()
                    ll_indicators.visibility = View.INVISIBLE
                } else {
                    btn_start.hide()
                    btn_next.show()
                    btn_skip.show()
                    ll_indicators.show()
                }

                setCurrentIndicator(position)
            }
        })

        img_close?.setOnClickListener {
            findNavController().popBackStack()
        }

        btn_next?.setOnClickListener {
            if (view_pager_guideline.currentItem + 1 < instructions.size) {
                view_pager_guideline.currentItem = view_pager_guideline.currentItem + 1
            } else {
                Toast.makeText(context, "finished", Toast.LENGTH_SHORT).show()
            }
        }
        btn_start?.setOnClickListener {
            findNavController().navigate(InstructionViewPageFragmentDirections.actionInstructionViewPageFragmentToInterviewSessionFragment())
            findNavController().popBackStack()
        }
        btn_skip?.setOnClickListener {
            findNavController().navigate(InstructionViewPageFragmentDirections.actionInstructionViewPageFragmentToInterviewSessionFragment())
            findNavController().popBackStack()
        }
    }


    private fun setupIndicators() {
        val indicators = mutableListOf<ImageView>()
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in instructions.indices) {
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