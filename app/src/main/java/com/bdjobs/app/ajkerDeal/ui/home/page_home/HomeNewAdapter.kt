package com.bdjobs.app.ajkerDeal.ui.home.page_home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.ajkerDeal.api.models.live_list.LiveListData
import com.bdjobs.app.common.CommonAdapter
import com.bdjobs.app.databinding.ItemViewHomeLiveShoppingItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.util.*

class HomeNewAdapter: CommonAdapter<LiveListData, ItemViewHomeLiveShoppingItemBinding>(
    diffCallback = object : DiffUtil.ItemCallback<LiveListData> () {
        override fun areItemsTheSame(oldItem: LiveListData, newItem: LiveListData): Boolean {
            return  oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LiveListData, newItem: LiveListData): Boolean {
            return oldItem == newItem
        }

    }
) {

    private val dataList: MutableList<LiveListData> = mutableListOf()
    var onItemClick: ((model: LiveListData, position: Int) -> Unit)? = null

    private val options = RequestOptions().placeholder(R.drawable.logo).error(R.drawable.logo)

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        val binding: ItemViewHomeLiveShoppingItemBinding = ItemViewHomeLiveShoppingItemBinding.inflate(
//            LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding)
//    }

//    override fun getItemCount(): Int = dataList.size
//
//    @ExperimentalStdlibApi
//    @SuppressLint("SetTextI18n")
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        if (holder is ViewHolder) {
//            val model = dataList[position]
//            val binding = holder.binding
//
//            Glide.with(binding.videoCover)
//                .load(model.coverPhoto)
//                .apply(options)
//                .into(binding.videoCover)
//
//            binding.titleTV.text = model.videoTitle
//            binding.liveStatus.text = model.statusName?.uppercase(Locale.US)
//        }
//    }

    inner class ViewHolder(val binding: ItemViewHomeLiveShoppingItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(dataList[adapterPosition], adapterPosition)
            }
        }
    }

    fun initList(list: List<LiveListData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun productList() = dataList

    override fun createBinding(parent: ViewGroup): ItemViewHomeLiveShoppingItemBinding {
        return ItemViewHomeLiveShoppingItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
    }

    override fun bind(binding: ItemViewHomeLiveShoppingItemBinding, item: LiveListData,position: Int) {
            Glide.with(binding.videoCover)
                .load(item.coverPhoto)
                .apply(options)
                .into(binding.videoCover)

            binding.titleTV.text = item.videoTitle
            binding.liveStatus.text = item.statusName?.toUpperCase(Locale.US)

        binding.root.setOnClickListener {
            onItemClick?.invoke(item, position)
        }
    }

}
