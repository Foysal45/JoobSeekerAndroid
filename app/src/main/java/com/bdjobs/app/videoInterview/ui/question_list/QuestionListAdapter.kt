package com.bdjobs.app.videoInterview.ui.question_list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.databinding.ItemVideoInterviewQuestionBinding
import com.bdjobs.app.videoInterview.data.models.VideoInterviewQuestionList

class QuestionListAdapter(val context: Context, val clickListener: ClickListener) :
        ListAdapter<VideoInterviewQuestionList.Data, QuestionListAdapter.QuestionViewHolder>(
                DiffUtilCallback
        ) {


    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): QuestionViewHolder {
        return QuestionViewHolder.from(
                parent
        )
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    companion object DiffUtilCallback : DiffUtil.ItemCallback<VideoInterviewQuestionList.Data>() {
        override fun areItemsTheSame(oldItem: VideoInterviewQuestionList.Data, newItem: VideoInterviewQuestionList.Data): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: VideoInterviewQuestionList.Data, newItem: VideoInterviewQuestionList.Data): Boolean {
            return oldItem.questionId == newItem.questionId
        }
    }

    class QuestionViewHolder(private var binding: ItemVideoInterviewQuestionBinding) :
            RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup): QuestionViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemVideoInterviewQuestionBinding.inflate(layoutInflater, parent, false)
                return QuestionViewHolder(binding)
            }
        }

        fun bind(questionData: VideoInterviewQuestionList.Data, clickListener: ClickListener) {
            binding.question = questionData
            binding.executePendingBindings()
            binding.clickListener = clickListener
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }
}


class ClickListener(val clickListener: (questionData: VideoInterviewQuestionList.Data) -> Unit) {
    fun onClick(questionData: VideoInterviewQuestionList.Data) {
        clickListener(questionData)
    }
}