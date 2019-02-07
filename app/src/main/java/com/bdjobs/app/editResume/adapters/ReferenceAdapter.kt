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
import com.bdjobs.app.editResume.adapters.models.ReferenceDataModel
import com.bdjobs.app.editResume.callbacks.OtherInfo

class ReferenceAdapter(arr: java.util.ArrayList<ReferenceDataModel>, val context: Context) : RecyclerView.Adapter<ReferenceAdapter.ReferenceViewHolder>() {

    private var call: OtherInfo = context as OtherInfo
    private val DURATION = 300
    private var itemList: MutableList<ReferenceDataModel> = arr

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReferenceViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_reference_list, parent, false)
        return ReferenceAdapter.ReferenceViewHolder(itemView)
    }

    override fun getItemCount(): Int {

        return if (itemList == null) 0 else itemList.size
    }

    override fun onBindViewHolder(holder: ReferenceViewHolder, position: Int) {
        holder.ivCollapsedLogo?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_reference_icon))
        val dModel = itemList.get(position)
        holder.tvTitle?.text = dModel.name
        holder.tvOrgName?.text = "${dModel.designation} at ${dModel.organization} "
        holder.tvCompletionYear?.text = dModel.address
        holder.tvTrTopic?.text = dModel.mobile
        holder.tvTrCountry?.text = dModel.email
        holder.tvTrLoc?.text = dModel.relation

        holder.ivEdit?.setOnClickListener {
            call.passReferenceData(dModel)
            d("From list : ${dModel.name}")
            call.goToEditInfo("editReference")
        }

        holder.imageViewExpand!!.setOnClickListener {
            Log.d("click", "clicked success")
            toggleDetails(holder)
        }
        holder.moreActionDetails?.visibility = View.GONE
    }

    private fun toggleDetails(holder: ReferenceAdapter.ReferenceViewHolder) {
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


    class ReferenceViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {


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