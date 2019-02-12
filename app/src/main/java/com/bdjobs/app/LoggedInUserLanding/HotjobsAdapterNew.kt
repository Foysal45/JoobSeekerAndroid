package com.bdjobs.app.LoggedInUserLanding

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.HotJobsData
import com.bdjobs.app.API.ModelClasses.HotJobsJobTitle
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.loadImageFromUrl
import com.bdjobs.app.Utilities.show
import com.google.android.material.button.MaterialButton


class HotjobsAdapterNew(private val context: Context) : RecyclerView.Adapter<HotJobsViewHolder>() {

    private var hotjoblists = ArrayList<HotJobsData>()
    private var hotjobsTitlesAdapter: HotjobsTitlesAdapter? = null
    private var expandedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotJobsViewHolder {
        return HotJobsViewHolder(LayoutInflater.from(context).inflate(R.layout.hotjobs_item, parent, false))

    }

    override fun getItemCount(): Int {
        return return if (hotjoblists == null) 0 else hotjoblists!!.size
    }

    override fun onBindViewHolder(holder: HotJobsViewHolder, position: Int) {
        holder?.companyTV.text = hotjoblists!![position].companyName
        holder?.companyLogo_IV.loadImageFromUrl(hotjoblists?.get(position)?.logoSource?.trim()!!)
        Log.d("valuev", "===" + hotjoblists?.get(position)?.jobTitles as List<HotJobsJobTitle>)

        holder?.expandBtn.setOnClickListener {

            if(position!=expandedPosition) {
                expandedPosition = position
                notifyDataSetChanged()
            }else{
                expandedPosition = -1
                notifyDataSetChanged()
            }
        }

        val hotjoblst = hotjoblists?.get(position)?.jobTitles as List<HotJobsJobTitle>

        if (hotjoblst.size > 2) {
            holder?.expandBtn.show()
        } else {
            holder?.expandBtn.hide()
        }


        if (position == expandedPosition) {
            holder?.expandBtn.icon = context.getDrawable(R.drawable.ic_baselineup_24px)
            hotjobsTitlesAdapter = HotjobsTitlesAdapter(context, hotjoblists?.get(position)?.jobTitles as List<HotJobsJobTitle>)
            holder?.hotjobtitles_RV?.adapter = hotjobsTitlesAdapter
            holder?.hotjobtitles_RV?.setHasFixedSize(true)
            holder?.hotjobtitles_RV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        } else {
            holder?.expandBtn.icon = context.getDrawable(R.drawable.ic_bas18dppx)
            val hotjoblst = hotjoblists?.get(position)?.jobTitles as List<HotJobsJobTitle>
            hotjobsTitlesAdapter = if (hotjoblst.size > 1) {
                HotjobsTitlesAdapter(context, hotjoblst?.take(2))
            } else {
                HotjobsTitlesAdapter(context, hotjoblst)
            }
            holder?.hotjobtitles_RV?.adapter = hotjobsTitlesAdapter
            holder?.hotjobtitles_RV?.setHasFixedSize(true)
            holder?.hotjobtitles_RV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        }

    }

    fun add(r: HotJobsData) {
        hotjoblists?.add(r)
        notifyItemInserted(hotjoblists!!.size - 1)
    }

    fun addAll(moveResults: List<HotJobsData>) {
        for (result in moveResults) {
            add(result)
        }
    }

    fun removeAll() {
        hotjoblists?.clear()
        notifyDataSetChanged()
    }
}

class HotJobsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val companyTV = view?.findViewById(R.id.companyTV) as TextView
    val hotjobtitles_RV = view?.findViewById(R.id.hotjobtitles_RV) as RecyclerView
    val companyLogo_IV = view?.findViewById(R.id.companyLogo_IV) as ImageView
    val expandBtn = view?.findViewById(R.id.button2) as MaterialButton
}