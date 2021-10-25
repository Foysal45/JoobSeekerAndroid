package com.bdjobs.app.ajkerDeal.ui.video_shopping.video_pager

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.ajkerDeal.api.models.live_product.LiveProductData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bdjobs.app.R
import com.bdjobs.app.databinding.ItemViewLiveProductOverviewBinding
import com.bdjobs.app.ajkerDeal.utilities.DigitConverter

class LiveProductOverviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<LiveProductData> = mutableListOf()
    var onItemClick: ((model: LiveProductData, position: Int) -> Unit)? = null

    private val options = RequestOptions().placeholder(R.drawable.logo).error(R.drawable.logo)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewLiveProductOverviewBinding = ItemViewLiveProductOverviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding

            Glide.with(binding.productImage)
                .load(model.coverPhoto)
                .apply(options)
                .into(binding.productImage)

            binding.price.text = "৳ ${DigitConverter.toBanglaDigit(model.productPrice, true)}"
        }
    }

    inner class ViewHolder(val binding: ItemViewLiveProductOverviewBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.productImage.setOnClickListener {
                onItemClick?.invoke(dataList[adapterPosition], adapterPosition)
            }
        }
    }

    fun initList(list: List<LiveProductData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun productList() = dataList


}