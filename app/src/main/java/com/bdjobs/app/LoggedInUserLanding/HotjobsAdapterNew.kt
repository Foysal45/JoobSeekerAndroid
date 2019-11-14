package com.bdjobs.app.LoggedInUserLanding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.HotJobsData
import com.bdjobs.app.API.ModelClasses.HotJobsJobTitle
import com.bdjobs.app.Ads.Ads
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.*
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.material.button.MaterialButton


class HotjobsAdapterNew(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var hotjoblists = ArrayList<HotJobsData>()
    private var hotjobsTitlesAdapter: HotjobsTitlesAdapter? = null
    private var expandedPosition = -1
    private var hotjoblst: List<HotJobsJobTitle>? = null

    private val showAD = true
    private val ITEM = 0
    private val ITEMWITHAD = 1



    override fun getItemViewType(position: Int): Int {
        return if (showAD && (position % 5 == 0) && position != 0 && position < 80) ITEMWITHAD
        else ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {

        var viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)


        when (viewType) {
            ITEM -> {
                val viewItem = inflater.inflate(R.layout.hotjobs_item, parent, false)
                viewHolder = HotJobsViewHolder(viewItem)
            }
            ITEMWITHAD -> {
                val viewItem = inflater.inflate(R.layout.hotjobs_item_ad, parent, false)
                viewHolder = HotJobsAdViewHolder(viewItem)
            }
        }

        return viewHolder!!

    }


    override fun onBindViewHolder(viewholder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM -> {
                val holder = viewholder as HotJobsViewHolder

                holder.companyTV.text = hotjoblists[position].companyName
                try {
                    holder.companyLogo_IV.loadImageFromUrl(hotjoblists.get(position).logoSource?.trim())
                } catch (e: Exception) {
                    logException(e)
                }


                holder.expandBtn.setOnClickListener {

                    if (position != expandedPosition) {
                        expandedPosition = position
                        notifyDataSetChanged()
                    } else {
                        expandedPosition = -1
                        notifyDataSetChanged()
                    }
                }

                hotjoblst = hotjoblists.get(position).jobTitles as List<HotJobsJobTitle>?

                if (hotjoblst.isNullOrEmpty()) {
                    holder.expandBtn.hide()
                } else {
                    if (hotjoblst!!.size > 2) {
                        holder.expandBtn.show()
                    } else {
                        holder.expandBtn.hide()
                    }
                }


                if (position == expandedPosition) {
                    holder.expandBtn.icon = context.getDrawable(R.drawable.ic_baselineup_24px)
                    hotjobsTitlesAdapter = HotjobsTitlesAdapter(context, hotjoblists.get(position).jobTitles as List<HotJobsJobTitle>)
                    holder.hotjobtitles_RV.adapter = hotjobsTitlesAdapter
                    holder.hotjobtitles_RV.setHasFixedSize(true)
                    holder.hotjobtitles_RV.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
                } else {
                    holder.expandBtn.icon = context.getDrawable(R.drawable.ic_bas18dppx)
                    try {
                        hotjoblst = hotjoblists.get(position).jobTitles as List<HotJobsJobTitle>
                    } catch (e: Exception) {
                        logException(e)
                    }

                    hotjoblst?.let { lt ->
                        hotjobsTitlesAdapter = if (lt.size > 1) {
                            HotjobsTitlesAdapter(context, lt.take(2))
                        } else {
                            HotjobsTitlesAdapter(context, lt)
                        }
                    }

                    holder.hotjobtitles_RV.adapter = hotjobsTitlesAdapter
                    holder.hotjobtitles_RV.setHasFixedSize(true)
                    holder.hotjobtitles_RV.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
                }
            }
            ITEMWITHAD -> {
                val holder = viewholder as HotJobsAdViewHolder

                Ads.showNativeAd(holder.ad_small_template,context)

                holder.companyTV.text = hotjoblists[position].companyName
                try {
                    holder.companyLogo_IV.loadImageFromUrl(hotjoblists.get(position).logoSource?.trim())
                } catch (e: Exception) {
                    logException(e)
                }


                holder.expandBtn.setOnClickListener {

                    if (position != expandedPosition) {
                        expandedPosition = position
                        notifyDataSetChanged()
                    } else {
                        expandedPosition = -1
                        notifyDataSetChanged()
                    }
                }

                hotjoblst = hotjoblists.get(position).jobTitles as List<HotJobsJobTitle>?

                if (hotjoblst.isNullOrEmpty()) {
                    holder.expandBtn.hide()
                } else {
                    if (hotjoblst!!.size > 2) {
                        holder.expandBtn.show()
                    } else {
                        holder.expandBtn.hide()
                    }
                }


                if (position == expandedPosition) {
                    holder.expandBtn.icon = context.getDrawable(R.drawable.ic_baselineup_24px)
                    hotjobsTitlesAdapter = HotjobsTitlesAdapter(context, hotjoblists.get(position).jobTitles as List<HotJobsJobTitle>)
                    holder.hotjobtitles_RV.adapter = hotjobsTitlesAdapter
                    holder.hotjobtitles_RV.setHasFixedSize(true)
                    holder.hotjobtitles_RV.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
                } else {
                    holder.expandBtn.icon = context.getDrawable(R.drawable.ic_bas18dppx)
                    try {
                        hotjoblst = hotjoblists.get(position).jobTitles as List<HotJobsJobTitle>
                    } catch (e: Exception) {
                        logException(e)
                    }

                    hotjoblst?.let { lt ->
                        hotjobsTitlesAdapter = if (lt.size > 1) {
                            HotjobsTitlesAdapter(context, lt.take(2))
                        } else {
                            HotjobsTitlesAdapter(context, lt)
                        }
                    }

                    holder.hotjobtitles_RV.adapter = hotjobsTitlesAdapter
                    holder.hotjobtitles_RV.setHasFixedSize(true)
                    holder.hotjobtitles_RV.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (hotjoblists == null) 0 else hotjoblists.size
    }


    fun add(r: HotJobsData) {
        hotjoblists.add(r)
        notifyItemInserted(hotjoblists.size - 1)
    }

    fun addAll(moveResults: List<HotJobsData>) {
        for (result in moveResults) {
            add(result)
        }
    }

    fun removeAll() {
        hotjoblists.clear()
        notifyDataSetChanged()
    }
}

class HotJobsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val companyTV = view.findViewById(R.id.companyTV) as TextView
    val hotjobtitles_RV = view.findViewById(R.id.hotjobtitles_RV) as RecyclerView
    val companyLogo_IV = view.findViewById(R.id.companyLogo_IV) as ImageView
    val expandBtn = view.findViewById(R.id.button2) as MaterialButton
}

class HotJobsAdViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val companyTV = view.findViewById(R.id.companyTV) as TextView
    val hotjobtitles_RV = view.findViewById(R.id.hotjobtitles_RV) as RecyclerView
    val companyLogo_IV = view.findViewById(R.id.companyLogo_IV) as ImageView
    val expandBtn = view.findViewById(R.id.button2) as MaterialButton
    val ad_small_template: TemplateView = view?.findViewById(R.id.ad_small_template) as TemplateView
}