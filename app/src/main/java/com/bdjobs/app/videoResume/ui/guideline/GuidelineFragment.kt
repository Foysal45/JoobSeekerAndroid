package com.bdjobs.app.videoResume.ui.guideline

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_guideline.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GuidelineFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GuidelineFragment : Fragment() {

    var rotationAngle = 0

    val stepsInEnglish = listOf<String>(
            "Prepare & Setup Recording Place",
            "Prepare Answers",
            "Record & submit answers for video resume",
            "Add to Resume Profile"
    )

    val tipsInEnglish = listOf<String>(
            "Tips 1",
            "Tips 2",
            "Tips 3",
    )

    val beneitsInEnglish = listOf(
            "Benefit 1",
            "Benefit 2",
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_guideline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        img_step_expand?.setOnClickListener {

//            val transition: Transition = Fade()
//            transition.setDuration(100)
//            transition.addTarget(R.id.rv_step)
//
//            TransitionManager.beginDelayedTransition(rv_step, transition)

            if (rotationAngle == 0) {
                rotationAngle = 180
                rv_step?.show()
            } else {
                rotationAngle = 0 //toggle
                rv_step?.hide()
            }
            it.animate().rotation(rotationAngle.toFloat()).setDuration(300).start()
        }


        val adapter = GuidelineAdapter(stepsInEnglish)
        rv_step.adapter = adapter

        val adapterTips = GuidelineAdapter(tipsInEnglish)
        rv_tips?.adapter = adapterTips

    }
}