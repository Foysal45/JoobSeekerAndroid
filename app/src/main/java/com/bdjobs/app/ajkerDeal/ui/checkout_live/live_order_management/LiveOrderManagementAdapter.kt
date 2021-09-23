package com.bdjobs.app.ajkerDeal.ui.checkout_live.live_order_management

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.livevideoshopping.api.models.live_order_management.LiveOrderManagementResponseBody
import com.example.livevideoshopping.databinding.ItemViewLiveScheduleProductListBinding
import com.example.livevideoshopping.utilities.DigitConverter

class LiveOrderManagementAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mContext: Context? = null
    private val dataList: MutableList<LiveOrderManagementResponseBody> = mutableListOf()
    var onItemClicked: ((model: LiveOrderManagementResponseBody) -> Unit)? = null
    private val fragmentTag = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemViewLiveScheduleProductListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewModel(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewModel) {
            val model = dataList[position]
            val binding = holder.binding
            Glide.with(binding.productImage).load(model.imageUrl).into(binding.productImage)

            binding.productTitle.text = "অর্ডার কোড: ${DigitConverter.toBanglaDigit(model.orderId)}"
            binding.productPrice.text = "দাম: ৳ ${DigitConverter.toBanglaDigit(model.productPrice)}"
            binding.productQtn.text = "(x${DigitConverter.toBanglaDigit(model.orderCount)} টি)"
            val parts: List<String> = model.insertedOn?.split("T") ?: listOf("")
            var dateFormat = DigitConverter.toBanglaDate(parts.first(), "yyyy-MM-dd")
            dateFormat = DigitConverter.formatDate(dateFormat, "yyyy-MM-dd", "MM-dd-yyyy")
            binding.orderDate.text = "অর্ডার ডেট: ${DigitConverter.toBanglaDate(dateFormat, "MM-dd-yyyy")}"

        }

    }

    inner class ViewModel(val binding: ItemViewLiveScheduleProductListBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION)
                    onItemClicked?.invoke(dataList[absoluteAdapterPosition])
            }
        }
    }

    fun pagingLoad(list: List<LiveOrderManagementResponseBody>) {
        val currentIndex = dataList.size
        val newDataCount = list.size
        dataList.addAll(list)
        notifyItemRangeInserted(currentIndex, newDataCount)
    }

    fun initLoad(list: List<LiveOrderManagementResponseBody>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

}