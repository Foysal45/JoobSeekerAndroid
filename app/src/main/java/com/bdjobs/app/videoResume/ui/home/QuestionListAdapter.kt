package com.bdjobs.app.videoResume.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.utilities.hide
import com.bdjobs.app.utilities.show
import com.bdjobs.app.videoResume.data.models.Question
import kotlinx.android.synthetic.main.layout_video_resume_question_item.view.*

class QuestionListAdapter(var question: MutableList<Question>) : RecyclerView.Adapter<QuestionListAdapter.QuestionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder = QuestionViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_video_resume_question_item, parent, false)
    )

    override fun getItemCount(): Int = question.size

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(question[position])
    }

    inner class QuestionViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val serial : TextView = view.tv_serial_no
        val title: TextView = view.tv_title
        val time : TextView = view.tv_time
        val isNew : TextView = view.tv_new

        fun bind(question: Question) {
            serial.text = question.serial
            title.text = question.title
            time.text = question.time
            if (question.isNew){
                isNew.show()
            } else
                isNew.hide()

        }
    }

    fun updateList(newQuestions: MutableList<Question>) {
        question.clear()
        question.addAll(newQuestions)
        notifyDataSetChanged()
    }

}