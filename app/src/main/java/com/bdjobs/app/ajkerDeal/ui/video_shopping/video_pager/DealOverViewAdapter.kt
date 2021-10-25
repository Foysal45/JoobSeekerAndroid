package com.bdjobs.app.ajkerDeal.ui.video_shopping.video_pager

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.ajkerDeal.api.models.catalog_details.CatalogProductData
import com.bumptech.glide.Glide
import com.bdjobs.app.R
import com.bdjobs.app.databinding.ItemViewDealOverViewBinding
import com.bdjobs.app.ajkerDeal.utilities.DigitConverter

class DealOverViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<CatalogProductData> = mutableListOf()
    var onItemClick: ((model: CatalogProductData, position: Int) -> Unit)? = null
    var onBuyClick: ((model: CatalogProductData, position: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewDealOverViewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_view_deal_over_view, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding

            Glide.with(binding.image).load(model.imageLink).into(binding.image)

            binding.title.text = model.dealTitle

            binding.price.text = "৳${DigitConverter.toBanglaDigit(model.dealPrice, true)}"
            if (model.dealPrice < model.regularPrice) {
                binding.regularPrice.text = "৳${DigitConverter.toBanglaDigit(model.regularPrice, true)}"
                binding.regularPrice.paintFlags = binding.regularPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                //binding.regularPrice.visibility = View.VISIBLE
            } else {
                binding.regularPrice.text = ""
                //binding.regularPrice.visibility = View.GONE
            }

            if (model.dealDiscount > 0) {
                val discount = (model.dealDiscount / model.regularPrice.toFloat()) * 100
                binding.discountTag.text = "${DigitConverter.toBanglaDigit(discount.toInt())}% অফ"
                binding.discountTag.visibility = View.VISIBLE
            }

        }
    }

    inner class ViewHolder(val binding: ItemViewDealOverViewBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.card.setOnClickListener {
                onItemClick?.invoke(dataList[adapterPosition], adapterPosition)
            }
            binding.buyNowBtn.setOnClickListener {
                onItemClick?.invoke(dataList[adapterPosition], adapterPosition)
            }
        }
    }

    fun initList(list: List<CatalogProductData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }
}