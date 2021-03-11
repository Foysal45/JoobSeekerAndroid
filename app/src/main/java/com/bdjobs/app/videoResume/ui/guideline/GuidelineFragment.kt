package com.bdjobs.app.videoResume.ui.guideline

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_guideline.*
import kotlinx.android.synthetic.main.fragment_guideline.tool_bar
import kotlinx.android.synthetic.main.fragment_video_resume_landing.*

class GuidelineFragment : Fragment() {

    enum class Language {
        BN, EN
    }

    var defaultLanguage: Language = Language.EN

    var stepsRotationAngle = 180
    var tipsRotationAngle = 180
    var benefitsRotationAngle = 180

    private val stepsInEnglish = listOf<String>(
            "Prepare & Setup Recording environment",
            "View questions & Prepare answers",
            "Record & submit answers",
            "Add Video Resume to Profile"
    )

    private val stepsInBangla = listOf<String>(
            "রেকর্ডিং এর পরিবেশ ঠিক করে প্রস্তুতি নিন",
            "প্রশ্ন দেখুন এবং উত্তর প্রস্তুত করুন",
            "রেকর্ড করুন এবং উত্তর জমা দিন",
            "প্র্রোফাইলে ভিডিও রিজিউমি যুক্ত করুন"
    )


    private val tipsInEnglish = listOf<String>(
            "Prepare your device (charge , audio/ video)",
            "Find a good background",
            "Check you Internet Connection",
            "Make sure audio should clear, advised to use headphone",
            "Know about recording software",
            "Dressed up formally (comfortable clothing)",
            "Try to maintain eye contact with camera",
            "Prepare a script",
            "Know your audience",
    )

    private val tipsInBangla = listOf(
            "ডিভাইস প্রস্তুতি (চার্জ, অডিও/ ভিডিও)",
            "একটি ভাল পরিবেশ/ ব্যাকগ্রাউন্ড বেছে নিন",
            "ইন্টারনেট কানেকশন চেক করুন",
            "অডিও স্পষ্ট কিনা তা নিশ্চিত করুন, প্রয়োজনে হেডফোন ব্যবহার করুন",
            "যে সফটওয়্যার দিয়ে ভিডিও রেজ্যুমি রেকর্ড করবেন সেই সফটওয়্যার সম্পর্কে জানুন",
            "ফরমাল এবং মানানসই পোশাক অত্যন্ত গুরুত্বপূর্ণ",
            "ক্যামেরার দিকে চোখ রেখে কথা বলুন",
            "রেকর্ড করার আগে সম্ভাব্য প্রশ্নগুলোর ব্যাপারে প্রস্তুতি নিয়ে রাখুন",
            "কোন ধরণের নিয়োগকর্তা আপনার ভিডিও রেজ্যুমিটি ভিউ করতে পারে সেই সম্পর্কে জানুন"
    )

    private val benefitsInEnglish = listOf(
            "Showcases your personality",
            "Expose your skills in a limited time with less effort",
            "Show your technology based skills",
            "Make a eye catching content using technology",
            "Be presentable",
            "Mark yourself unique to the recruiters ",
    )

    private val benefitsInBangla = listOf(
            "ইন্টারভিউ তে দক্ষতার নিজের সাথে পার্সোনালিটি তুলে ধরুন",
            "স্বল্প সময়ে উত্তর প্রাসঙ্গিক রেখে আপনার স্কিল গুলো তুলে ধরুন",
            "আপনার টেকনোলজি বেসড স্কিলগুলো তুলে ধরুন",
            "টেকনোলজি ব্যবহার করে আই ক্যাচিং কনটেন্ট তৈরী করুন",
            "যে সফটওয়্যার দিয়ে ভিডিও রেজ্যুমি রেকর্ড করবেন সেই সফটওয়্যার সম্পর্কে জানুন",
            "নিজেকে উপস্থাপনমূলক করে তুলুন",
            "আপনি কেন ডিফারেন্ট বা কিভাবে একটা কোম্পানির জন্য ভ্যালুএবল এসেট হতে পারেন তা সাবলীল ভাষায় উপস্থাপন করুন"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_guideline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        tool_bar?.setupWithNavController(navController, appBarConfiguration)
        rv_step?.show()
        rv_tips?.show()
        rv_benefits?.show()

        img_step_expand?.setOnClickListener {

            if (stepsRotationAngle == 0) {
                stepsRotationAngle = 180
                rv_step?.show()
            } else {
                stepsRotationAngle = 0 //toggle
                rv_step?.hide()
            }
            it.animate().rotation(stepsRotationAngle.toFloat()).setDuration(200).start()
        }

        img_tips_expand?.setOnClickListener {
            if (tipsRotationAngle == 0) {
                tipsRotationAngle = 180
                rv_tips?.show()
            } else {
                tipsRotationAngle = 0 //toggle
                rv_tips?.hide()
            }
            it.animate().rotation(tipsRotationAngle.toFloat()).setDuration(200).start()
        }

        img_benefits_expand?.setOnClickListener {
            if (benefitsRotationAngle == 0) {
                benefitsRotationAngle = 180
                rv_benefits?.show()
            } else {
                benefitsRotationAngle = 0 //toggle
                rv_benefits?.hide()
            }
            it.animate().rotation(benefitsRotationAngle.toFloat()).setDuration(200).start()
        }

        btn_change_language?.setOnClickListener {
            defaultLanguage = if (defaultLanguage == Language.EN) {
                Language.BN
            } else {
                Language.EN
            }
            changeButtonIconAndText()
            changeLanguage()
        }


        val adapter = GuidelineAdapter(stepsInEnglish)
        rv_step?.adapter = adapter

        val adapterTips = GuidelineAdapter(tipsInEnglish)
        rv_tips?.adapter = adapterTips

        val benefitsAdapter = GuidelineAdapter(benefitsInEnglish)
        rv_benefits?.adapter = benefitsAdapter

    }

    private fun changeButtonIconAndText() {
        when (defaultLanguage) {
            Language.BN -> {
                btn_change_language.apply {
                    text = "View in English"
                    setIconResource(R.drawable.ic_en)
                }
            }
            else -> {
                btn_change_language.apply {
                    text = "বাংলায় দেখুন"
                    setIconResource(R.drawable.ic_bn)
                }
            }
        }
    }

    private fun changeLanguage() {
        when (defaultLanguage) {
            Language.BN -> {
                tv_step?.text = "ভিডিও রিজিউমি রেকর্ড করার ধাপসমূহ"
                rv_step?.adapter = GuidelineAdapter(stepsInBangla)

                tv_tips.text = "টিপস"
                rv_tips.adapter = GuidelineAdapter(tipsInBangla)

                tv_benefits.text = "উপকারিতা"
                rv_benefits.adapter = GuidelineAdapter(benefitsInBangla)
            }
            else -> {
                tv_step?.text = "Steps of creating Video Resume"
                rv_step?.adapter = GuidelineAdapter(stepsInEnglish)

                tv_tips.text = "Tips"
                rv_tips.adapter = GuidelineAdapter(tipsInEnglish)


                tv_benefits.text = "Benefits"
                rv_benefits.adapter = GuidelineAdapter(benefitsInEnglish)
            }
        }
    }
}