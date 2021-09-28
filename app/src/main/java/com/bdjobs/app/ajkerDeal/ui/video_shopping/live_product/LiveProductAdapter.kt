package com.bdjobs.app.ajkerDeal.ui.video_shopping.live_product

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bdjobs.app.R
import com.bdjobs.app.ajkerDeal.api.models.live_product.LiveProductData
import com.bdjobs.app.databinding.ItemViewLiveProductBinding
import com.bdjobs.app.ajkerDeal.utilities.DigitConverter

class LiveProductAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<LiveProductData> = mutableListOf()
    var onItemClick: ((model: LiveProductData, position: Int) -> Unit)? = null
    var onBuyClick: ((model: LiveProductData, position: Int) -> Unit)? = null
    var onCartClick: ((model: LiveProductData, position: Int) -> Unit)? = null
    var onDownloadClick: ((model: LiveProductData, position: Int) -> Unit)? = null

    private val options = RequestOptions().placeholder(R.drawable.logo).error(R.drawable.logo)
    private var cartDataList: MutableList<LiveProductData> = mutableListOf()

    private var redirectFlag: Boolean = false
    private var selectedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewLiveProductBinding = ItemViewLiveProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

            binding.title.text = "প্রোডাক্ট কোড: ${DigitConverter.toBanglaDigit(model.id)}"
            binding.price.text = "৳${DigitConverter.toBanglaDigit(model.productPrice, true)}"

            val color = if (cartDataList.contains(model)) {
                ContextCompat.getColor(binding.cartBtn.context, R.color.color_blue)
            } else {
                ContextCompat.getColor(binding.cartBtn.context, R.color.gray)
            }
            binding.cartBtn.imageTintList = ColorStateList.valueOf(color)

            val selectedColor = if (selectedPosition == position) {
                ContextCompat.getColor(binding.parent.context, R.color.live_product_selected)
            } else {
                ContextCompat.getColor(binding.parent.context, R.color.white)
            }
            binding.parent.setBackgroundColor(selectedColor)
            if (selectedPosition == position) {
                onItemClick?.invoke(dataList[selectedPosition], selectedPosition)
            }
        }
    }

    inner class ViewHolder(val binding: ItemViewLiveProductBinding): RecyclerView.ViewHolder(binding.root) {

        init {

            if (redirectFlag) {
                binding.cartBtn.isVisible = false
                binding.buyNowBtn.isVisible = false
                binding.fbBuyNowBtn.isVisible = true
            }

            binding.buyNowBtn.setOnClickListener {
                onBuyClick?.invoke(dataList[adapterPosition], adapterPosition)
            }
            binding.fbBuyNowBtn.setOnClickListener {
                onBuyClick?.invoke(dataList[adapterPosition], adapterPosition)
            }
            binding.productImage.setOnClickListener {
                onItemClick?.invoke(dataList[adapterPosition], adapterPosition)
            }
            binding.detailsLayout.setOnClickListener {
                onItemClick?.invoke(dataList[adapterPosition], adapterPosition)
            }
            binding.cartBtn.setOnClickListener {
                onCartClick?.invoke(dataList[adapterPosition], adapterPosition)
            }
            binding.downloadBtn.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onDownloadClick?.invoke(dataList[adapterPosition], adapterPosition)
                }
            }
        }
    }

    fun initList(list: List<LiveProductData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun cartDataList(list: MutableList<LiveProductData>) {
        cartDataList = list
    }

    fun setRedirectionFlag(flag: Boolean) {
        redirectFlag = flag
    }

    fun setSelectedPosition(position: Int) {
        selectedPosition = position
    }
}