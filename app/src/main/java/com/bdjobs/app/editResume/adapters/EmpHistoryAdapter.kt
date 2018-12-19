package com.bdjobs.app.editResume.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.ExpandAndCollapseViewUtil
import com.bdjobs.app.Utilities.debug
import com.bdjobs.app.editResume.adapters.models.sampledata

class EmpHistoryAdapter(private val items: ArrayList<sampledata>, val context: Context) : RecyclerView.Adapter<EmpHistoryAdapter.MyViewHolder>() {

    private val DURATION = 200
    private var visibility: Int = View.GONE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_experiece_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dModel = items[position]
        holder.jstatus.text = dModel.message
        holder.application.text = dModel.message1
        holder.more.text = dModel.statuscode
        holder.more1.text = dModel.statuscode1
        //holder.moreActionDetails!!
        //ExpandAndCollapseViewUtil.collapse(holder.moreActionDetails!!, 0)
        holder.imageViewExpand!!.setOnClickListener {
            debug("clicked success")
            toggleDetails(holder)
        }
        val visibility = holder.moreActionDetails!!.visibility
        debug("beforetoggleDetails: $visibility")
        holder.moreActionDetails?.visibility = View.GONE
    }

    private fun toggleDetails(holder: MyViewHolder) {
        var visibility = holder.moreActionDetails!!.visibility
        if (visibility == View.GONE) {
            visibility = View.VISIBLE
            debug("iftoggleDetails: $visibility")
            ExpandAndCollapseViewUtil.expand(holder.moreActionDetails!!, DURATION)
            holder.imageViewExpand!!.setImageResource(R.drawable.ic_arrow_down)
            rotate(180.0f, holder)
        } else {
            ExpandAndCollapseViewUtil.collapse(holder.moreActionDetails!!, DURATION)
            debug("elsetoggleDetails: $visibility")
            holder.imageViewExpand!!.setImageResource(R.drawable.ic_arrow_up)
            rotate(-180.0f, holder)
        }
    }

    private fun rotate(angle: Float, holder: MyViewHolder) {
        val animation = RotateAnimation(0.0f, angle, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f)
        animation.fillAfter = true
        animation.duration = DURATION.toLong()
        holder.imageViewExpand!!.startAnimation(animation)
    }

    class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var moreActionDetails: ViewGroup? = itemView!!.findViewById(R.id.rl_expanded)
        var moreAction: RelativeLayout? = itemView!!.findViewById(R.id.rl_collapsed)
        var imageViewExpand: ImageView? = itemView!!.findViewById(R.id.iv_details) as ImageView

        var jstatus: TextView = itemView!!.findViewById(R.id.tv_sam)
        var application: TextView = itemView!!.findViewById(R.id.tv_1)
        var more: TextView = itemView!!.findViewById(R.id.tv_2)
        var more1: TextView = itemView!!.findViewById(R.id.tv_3)
    }
}