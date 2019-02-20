package com.bdjobs.app.editResume.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.ExpandAndCollapseViewUtil
import com.bdjobs.app.Utilities.d
import com.bdjobs.app.Utilities.debug
import com.bdjobs.app.editResume.adapters.models.Tr_DataItem
import com.bdjobs.app.editResume.callbacks.EduInfo

class TrainingInfoAdapter(arr: java.util.ArrayList<Tr_DataItem>, val context: Context) : RecyclerView.Adapter<TrainingInfoAdapter.MyViewHolder>() {

    private var call: EduInfo = context as EduInfo
    private val DURATION = 300
    private var itemList: MutableList<Tr_DataItem>? = arr

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_traininginfo_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if (itemList == null) 0 else itemList!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.d("aca", "calling")
        holder.ivCollapsedLogo?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_training_sum))
        val dModel = itemList?.get(position)!!
        if (position == itemList?.size!! - 1) {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = 200
            holder.itemView.layoutParams = params
        } else {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = 0
            holder.itemView.layoutParams = params
        }
        holder.tvTitle?.text = dModel.title
        holder.tvOrgName?.text = dModel.institute
        holder.tvCompletionYear?.text = dModel.year
        holder.tvTrTopic?.text = dModel.topic
        holder.tvTrCountry?.text = dModel.country
        holder.tvTrLoc?.text = dModel.location
        holder.tvTrDuration?.text = dModel.duration

        holder.ivEdit?.setOnClickListener {
            call.passTrainingData(dModel)
            d("From list : ${dModel.country}")
            call.goToEditInfo("editTr")
        }

        holder.imageViewExpand!!.setOnClickListener {
            Log.d("click", "clicked success")
            toggleDetails(holder)
        }
        holder.moreActionDetails?.visibility = View.GONE
    }

    private fun toggleDetails(holder: MyViewHolder) {
        var visibility: Int = holder.moreActionDetails!!.visibility
        if (visibility == View.GONE) {
            visibility = View.VISIBLE
            debug("iftoggleDetails: $visibility")
            ExpandAndCollapseViewUtil.expand(holder.moreActionDetails!!, DURATION)
            holder.imageViewExpand!!.setImageResource(R.drawable.ic_arrow_up)
        } else {
            ExpandAndCollapseViewUtil.collapse(holder.moreActionDetails!!, DURATION)
            debug("elsetoggleDetails: $visibility")
            holder.imageViewExpand!!.setImageResource(R.drawable.ic_arrow_down)
        }
    }

    class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var moreActionDetails: ViewGroup? = itemView!!.findViewById(R.id.cl_expanded)
        var imageViewExpand: ImageView? = itemView!!.findViewById(R.id.iv_details)
        var ivEdit: ImageView? = itemView!!.findViewById(R.id.iv_edit)
        var tvTitle: TextView? = itemView?.findViewById(R.id.tvDes)
        var tvCompletionYear: TextView? = itemView?.findViewById(R.id.tvDate)
        var tvOrgName: TextView? = itemView?.findViewById(R.id.tvCom)
        var tvTrTopic: TextView? = itemView?.findViewById(R.id.tvTrTopic)
        var tvTrLoc: TextView? = itemView?.findViewById(R.id.tvTrLoc)
        var tvTrDuration: TextView? = itemView?.findViewById(R.id.tvTrDuration)
        var tvTrCountry: TextView? = itemView?.findViewById(R.id.tvTrCountry)
        var ivCollapsedLogo: ImageView? = itemView?.findViewById(R.id.iv_c_logo)
    }
}