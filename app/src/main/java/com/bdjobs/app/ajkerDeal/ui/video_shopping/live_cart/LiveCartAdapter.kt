package com.bdjobs.app.ajkerDeal.ui.video_shopping.live_cart

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.ajkerDeal.api.models.live_product.LiveProductData
import com.bdjobs.app.ajkerDeal.utilities.DigitConverter
import com.bdjobs.app.databinding.ItemViewLiveCartBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import timber.log.Timber

class LiveCartAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<LiveProductData> = mutableListOf()
    var onItemClick: ((model: LiveProductData, position: Int) -> Unit)? = null
    var onDeleteClick: ((model: LiveProductData, position: Int) -> Unit)? = null
    var onActionClicked: ((action: Int) -> Unit)? = null
    private val minProductCount = 1

    private val options = RequestOptions().placeholder(R.drawable.logo).error(R.drawable.logo)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewLiveCartBinding = ItemViewLiveCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
            val quantity = model.cartDealQuantity
            holder.binding.itemViewShopCartQuantityCount.text = DigitConverter.toBanglaDigit(quantity, true)

            holder.binding.itemViewShopCartQuantityIncrease.setOnClickListener(View.OnClickListener { v ->
                //Toast.makeText(mContext,"quantityIncreaseTV",Toast.LENGTH_SHORT).show();
                Timber.d("quantityIncreaseTV called")
                val i = holder.adapterPosition
                if (i < 0) {
                    return@OnClickListener
                }
                holder.binding.itemViewShopCartQuantityIncrease.isEnabled = false

                if (dataList[i].cartDealQuantity != dataList[i].qtnLimitPerUser) {
                    ++dataList[i].cartDealQuantity
                    holder.binding.itemViewShopCartQuantityCount.text = DigitConverter.toBanglaDigit(dataList[i].cartDealQuantity, true)

                    onActionClicked?.invoke(1)

                }

                holder.binding.itemViewShopCartQuantityIncrease.isEnabled = true
            })

            holder.binding.itemViewShopCartQuantityDecrease.setOnClickListener(View.OnClickListener { v ->
                //Toast.makeText(mContext,"quantityDecreaseTV",Toast.LENGTH_SHORT).show();
                Timber.d("quantityDecreaseTV called")
                val i = holder.adapterPosition
                if (i < 0) {
                    return@OnClickListener
                }
                holder.binding.itemViewShopCartQuantityDecrease.isEnabled = false
                if (dataList[i].cartDealQuantity > minProductCount) {
                    --dataList[i].cartDealQuantity
                    holder.binding.itemViewShopCartQuantityCount.text = DigitConverter.toBanglaDigit(dataList[i].cartDealQuantity, true)

                    onActionClicked?.invoke(0)

                }

                holder.binding.itemViewShopCartQuantityDecrease.isEnabled = true
            })
        }
    }

    inner class ViewHolder(val binding: ItemViewLiveCartBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.productImage.setOnClickListener {
                onItemClick?.invoke(dataList[adapterPosition], adapterPosition)
            }
            binding.removeBtn.setOnClickListener {
                onDeleteClick?.invoke(dataList[adapterPosition], adapterPosition)
            }
        }
    }

    fun initList(list: List<LiveProductData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        if (position in 0..dataList.lastIndex) {
            dataList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}