package com.bdjobs.app.ajkerDeal.ui.video_shopping.video_comments

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.ajkerDeal.api.models.video_comments.load_all_comments.VideoCommentsModel
import com.bdjobs.app.databinding.ItemViewVideoShoppingCommentBinding
import com.bdjobs.app.ajkerDeal.utilities.DigitConverter

class VideoCommentsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<VideoCommentsModel> = mutableListOf()
    var onReplyClicked: ((model: VideoCommentsModel, position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewVideoShoppingCommentBinding = ItemViewVideoShoppingCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding

            var deliveryPost: String? = model.insertedOn
            if (deliveryPost != null && !TextUtils.isEmpty(deliveryPost)) {
                val deliveryDate = deliveryPost.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                deliveryPost = DigitConverter.toBanglaDate(deliveryDate[0])
            }

            binding.commentsTime.text = deliveryPost
            binding.sellerName.text = model.customerName
            binding.customerComments.text = model.comment
            if (model.replyComments.isNotEmpty()) {
                binding.replyCount.visibility = View.VISIBLE
                binding.replyCount.text = "রিপ্লাই (${DigitConverter.toBanglaDigit(model.replyComments.size)}টি)"
            } else {
                binding.replyCount.visibility = View.VISIBLE
                binding.replyCount.text = "রিপ্লাই"
            }

        }
    }

    override fun getItemCount(): Int = dataList.size

    inner class ViewHolder(val binding: ItemViewVideoShoppingCommentBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.replyCount.setOnClickListener {
                onReplyClicked?.invoke(dataList[adapterPosition], adapterPosition)
            }
        }
    }

    fun initLoad(list: List<VideoCommentsModel>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }


}