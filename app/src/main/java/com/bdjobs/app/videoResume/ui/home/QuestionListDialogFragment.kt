package com.bdjobs.app.videoResume.ui.home

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bdjobs.app.R
import com.bdjobs.app.videoResume.data.models.Question
import com.bdjobs.app.videoResume.data.repository.VideoResumeRepository
import kotlinx.android.synthetic.main.fragment_question_list.*

class QuestionListDialogFragment : DialogFragment() {


    val args : QuestionListDialogFragmentArgs by navArgs()
    private lateinit var videoResumeRepository: VideoResumeRepository

    private lateinit var questionList : MutableList<Question>
    private lateinit var questionListAdapter: QuestionListAdapter

    private var defaultLanguage = Language.EN

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_question_list, container, false)

        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        }
        return  view
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.9).toInt()
        dialog!!.window!!.setLayout(width, height)
        //THIS WILL MAKE WIDTH 90% OF SCREEN
        //HEIGHT WILL BE WRAP_CONTENT
        //getDialog().getWindow().setLayout(width, height);
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        videoResumeRepository = VideoResumeRepository(requireContext().applicationContext as Application)

        questionListAdapter = QuestionListAdapter(args.questions.toMutableList())

        val adapter = questionListAdapter
        rv_question_list?.adapter = adapter

        btn_yes?.setOnClickListener {
            dialog?.dismiss()
        }

        tv_change_lang.setOnClickListener {
            if (defaultLanguage == Language.EN) {
                defaultLanguage = Language.BN
                tv_heading.text = "প্রশ্ন তালিকা"
                tv_change_lang.text = "View in English"
                tv_change_lang.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_en_drawable,0,0,0)
                btn_yes.text = "ওকে"

                questionList = videoResumeRepository.getAllQuestionsFromDBInBn().toMutableList()
//                questionListAdapter = QuestionListAdapter(questionList.toMutableList())
                questionListAdapter.updateList(questionList)

            } else {
                defaultLanguage = Language.EN
                tv_heading.text = "Question list"
                tv_change_lang.text = "বাংলায় দেখুন"
                tv_change_lang.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bn_drawable,0,0,0)
                btn_yes.text = "OK"

                questionList = videoResumeRepository.getAllQuestionsFromDB().toMutableList()
//                questionListAdapter = QuestionListAdapter(questionList.toMutableList())
                questionListAdapter.updateList(questionList)
            }
        }
    }


    enum class Language {
        BN, EN
    }
}