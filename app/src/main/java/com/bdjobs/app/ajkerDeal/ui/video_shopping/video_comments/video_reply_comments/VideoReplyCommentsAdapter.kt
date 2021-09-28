package com.bdjobs.app.ajkerDeal.ui.video_shopping.video_comments.video_reply_comments

import android.annotation.SuppressLint
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.ajkerDeal.api.models.video_comments.load_all_comments.VideoReplyCommentsModel
import com.bdjobs.app.databinding.ItemViewVideoShoppingCommentBinding
import com.bdjobs.app.ajkerDeal.utilities.DigitConverter
import timber.log.Timber

class VideoReplyCommentsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<VideoReplyCommentsModel> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewVideoShoppingCommentBinding = ItemViewVideoShoppingCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding

            binding.replyCount.visibility = View.GONE

            var deliveryPost: String? = model.insertedOn
            if (deliveryPost != null && !TextUtils.isEmpty(deliveryPost)) {
                val deliveryDate = deliveryPost.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                deliveryPost = DigitConverter.toBanglaDate(deliveryDate[0])
            }

            binding.commentsTime.text = deliveryPost
            binding.sellerName.text = model.customerName
            binding.customerComments.text = model.replyComment
            Timber.d("ReplyComments: ${model.replyComment}")
//            Log.e("ReplyComments", "${model.replyComment}")
            Timber.d("ReplyDataComments: ${dataList}, ${dataList.size}")
            Log.e("ReplyComments", "${dataList.size}")
            /*if (model.replyComment.isNotEmpty()) {
                binding.replyCount.visibility = View.VISIBLE
                binding.replyCount.text = "রিপ্লাই (${DigitConverter.toBanglaDigit(model.replyComment.size)}টি)"
            } else {
                binding.replyCount.visibility = View.GONE
            }*/

        }
    }

    override fun getItemCount(): Int = dataList.size

    inner class ViewHolder(val binding: ItemViewVideoShoppingCommentBinding): RecyclerView.ViewHolder(binding.root)

    fun initLoad(list: List<VideoReplyCommentsModel>){
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

}
