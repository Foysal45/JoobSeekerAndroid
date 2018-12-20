package com.bdjobs.app.editResume.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.ExpandAndCollapseViewUtil
import com.bdjobs.app.Utilities.debug
import com.bdjobs.app.editResume.adapters.models.sampledata
import com.bdjobs.app.editResume.callbacks.EmpHisCB

class EmpHistoryAdapter(private val items: ArrayList<sampledata>, val context: Context) : RecyclerView.Adapter<EmpHistoryAdapter.MyViewHolder>() {

    private lateinit var call: EmpHisCB
    private val DURATION = 200

    init {
        call = context as EmpHisCB
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_experiece_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dModel = items[position]
        holder.tvDes?.text = dModel.statuscode
        holder.tvDate?.text = dModel.message
        holder.tvCom?.text = dModel.message1
        holder.tvAddress?.text = dModel.statuscode1
        //holder.moreActionDetails!!
        holder.ivEdit?.setOnClickListener {
            call.editInfo()
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
            //rotate(180.0f, holder)
        } else {
            ExpandAndCollapseViewUtil.collapse(holder.moreActionDetails!!, DURATION)
            debug("elsetoggleDetails: $visibility")
            holder.imageViewExpand!!.setImageResource(R.drawable.ic_arrow_down)
            //rotate(-180.0f, holder)
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

        var moreActionDetails: ViewGroup? = itemView!!.findViewById(R.id.cl_expanded)
        var imageViewExpand: ImageView? = itemView!!.findViewById(R.id.iv_details)
        var ivEdit: ImageView? = itemView!!.findViewById(R.id.iv_edit)
        var tvDes: TextView? = itemView?.findViewById(R.id.tvDes)
        var tvDate: TextView? = itemView?.findViewById(R.id.tvDate)
        var tvCom: TextView? = itemView?.findViewById(R.id.tvCom)
        var tvAddress: TextView? = itemView?.findViewById(R.id.tvAddress)
    }
}