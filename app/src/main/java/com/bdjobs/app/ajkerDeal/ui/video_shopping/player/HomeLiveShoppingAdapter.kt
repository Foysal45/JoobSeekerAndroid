package com.bdjobs.app.ajkerDeal.ui.video_shopping.player

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.ajkerDeal.api.models.firebase.ViewCount
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bdjobs.app.R
import com.bdjobs.app.ajkerDeal.api.models.live_list.LiveListData
import com.bdjobs.app.databinding.ItemViewHomeLiveShoppingItemBinding
import com.bdjobs.app.ajkerDeal.utilities.DigitConverter
import com.bdjobs.app.ajkerDeal.utilities.dpToPx
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class HomeLiveShoppingAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataList: MutableList<LiveListData> = mutableListOf()
    private val options = RequestOptions().placeholder(R.drawable.ic_live_placeholder_1).error(R.drawable.ic_live_placeholder_1)
    private val options1 = RequestOptions().placeholder(R.drawable.profile_image).error(R.drawable.profile_image)
    var onClick: ((model: LiveListData, position: Int) -> Unit)? = null
    private val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US)

    private val holderList: HashMap<Int, RecyclerView.ViewHolder> = HashMap()

    private val dbFirestore = Firebase.firestore.collection("liveshow").document("views")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: ItemViewHomeLiveShoppingItemBinding = ItemViewHomeLiveShoppingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val model = dataList[position]
            val binding = holder.binding
            if (!holderList.containsKey(position)) { holderList[position] = holder }
            binding.titleTV.text = model.videoTitle
            if (model.statusName == "live") {
                val drawable = ContextCompat.getDrawable(binding.liveStatus.context, R.drawable.bg_live_on)
                binding.liveStatus.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                binding.liveStatus.compoundDrawablePadding = binding.liveStatus.context.dpToPx(6f)

                //binding.viewCount.text = DigitConverter.toBanglaDigit(0)
                //binding.viewCount.visibility = View.VISIBLE
            } else {
                binding.liveStatus.setCompoundDrawables(null, null, null, null)
                binding.viewCount.visibility = View.GONE
            }
            binding.liveStatus.text = model.statusName?.toUpperCase(Locale.US)

            Glide.with(binding.videoCover)
                .load(model.coverPhoto)
                .apply(options)
                .into(binding.videoCover)

            when (model.statusName) {
                "upcoming" -> {
                    binding.viewCount.visibility = View.GONE
                    try {
                        val formatDate = "${model.liveDate} ${model.fromTime}"
                        val date = sdf.parse(formatDate)
                        if (date != null) {
                            binding.liveSchedule.text = DigitConverter.relativeTime(date)
                            binding.liveSchedule.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                "live" -> {
                    initLiveCount(model.id.toString(), holder)
                    binding.liveSchedule.visibility = View.GONE
                }
                "replay" -> {
                    binding.viewCount.visibility = View.GONE
                    binding.liveSchedule.visibility = View.GONE
                }
            }
        }
    }

    inner class ViewHolder(val binding: ItemViewHomeLiveShoppingItemBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onClick?.invoke(dataList[adapterPosition], adapterPosition)
            }
        }
    }

    private fun initLiveCount(liveStreamId: String, holder: ViewHolder) {

        //val liveShowViewRef = dbRef.child(liveStreamId)
        val randomView = Random.nextInt(10, 21)
        val liveShowViewRef = dbFirestore.collection(liveStreamId)
        liveShowViewRef.document("currentView").get().addOnSuccessListener { document ->
            if (!document.exists()) {
                liveShowViewRef.document("currentView").set(ViewCount(randomView))
                liveShowViewRef.document("totalView").set(ViewCount(randomView))
            } else {
                val viewerCount = document.getLong("view") ?: 0L
                val viewerCountStr = if (viewerCount >= 1000) {
                    "${DigitConverter.formatDecimal(viewerCount / 1000f)}k"
                } else {
                    if (viewerCount > 0L) {
                        viewerCount.toString()
                    } else {
                        "1"
                    }
                }
                //binding?.viewCount?.text = "${DigitConverter.toBanglaDigit(viewerCountStr)} জন দেখছেন"
                holder.binding.viewCount.visibility = View.VISIBLE
                holder.binding.viewCount.text = DigitConverter.toBanglaDigit(viewerCountStr)

            }
        }
    }

    fun initLoad(list: List<LiveListData>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }

    fun lazyLoad(list: List<LiveListData>) {
        val start = dataList.size
        dataList.addAll(list)
        notifyItemRangeInserted(start, list.size)
    }

    fun getList(): List<LiveListData> = dataList

    fun getDataByPosition(position: Int): LiveListData? {
        return if (position in 0..dataList.lastIndex) {
            dataList[position]
        } else null
    }

    fun getViewByPosition(position: Int): RecyclerView.ViewHolder? {
        return holderList[position]
    }

}