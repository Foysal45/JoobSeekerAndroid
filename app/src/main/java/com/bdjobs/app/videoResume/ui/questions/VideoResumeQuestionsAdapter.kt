package com.bdjobs.app.videoResume.ui.questions

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.databinding.ItemVideoResumeQuestionBinding
import com.bdjobs.app.videoResume.data.models.VideoResumeQuestionList

class VideoResumeQuestionsAdapter(val context: Context, val clickListener: ClickListener) :
    ListAdapter<VideoResumeQuestionList.Data, VideoResumeQuestionsAdapter.QuestionViewHolder>(
        DiffUtilCallback
    ) {

    private var onTipsClicked: ((VideoResumeQuestionList.Data) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): QuestionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemVideoResumeQuestionBinding.inflate(layoutInflater, parent, false)
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    companion object DiffUtilCallback : DiffUtil.ItemCallback<VideoResumeQuestionList.Data>() {
        override fun areItemsTheSame(
            oldItem: VideoResumeQuestionList.Data,
            newItem: VideoResumeQuestionList.Data
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: VideoResumeQuestionList.Data,
            newItem: VideoResumeQuestionList.Data
        ): Boolean {
            return oldItem.questionId == newItem.questionId
        }
    }

    inner class QuestionViewHolder(private var binding: ItemVideoResumeQuestionBinding) :
        RecyclerView.ViewHolder(binding.root) {

//        companion object {
//            fun from(parent: ViewGroup): QuestionViewHolder {
//                val layoutInflater = LayoutInflater.from(parent.context)
//                val binding = ItemVideoResumeQuestionBinding.inflate(layoutInflater, parent, false)
//                return QuestionViewHolder(binding)
//            }
//        }

        fun bind(questionData: VideoResumeQuestionList.Data, clickListener: ClickListener) {
            binding.question = questionData
            binding.tvAnswerTips.paint.isUnderlineText = true

            binding.tvAnswerTips.setOnClickListener {
                onTipsClicked?.let {
                    it(questionData)
                }
            }

            binding.executePendingBindings()
            binding.clickListener = clickListener
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    fun onTipsClicked(listener: (VideoResumeQuestionList.Data) -> Unit) {
        onTipsClicked = listener
    }
}


class ClickListener(val clickListener: (questionData: VideoResumeQuestionList.Data) -> Unit) {
    fun onClick(questionData: VideoResumeQuestionList.Data) {
        clickListener(questionData)
    }
}