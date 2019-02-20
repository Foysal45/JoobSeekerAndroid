package com.bdjobs.app.editResume.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.ProfessionalDataModel
import com.bdjobs.app.editResume.callbacks.EduInfo

class ProfessionalQFAdapter(arr: java.util.ArrayList<ProfessionalDataModel>, val context: Context) : RecyclerView.Adapter<ProfessionalQFAdapter.ProfessionalViewHolder>() {


    private var call: EduInfo = context as EduInfo
    private val DURATION = 300
    private var itemList: MutableList<ProfessionalDataModel> = arr

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfessionalViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_reference_list, parent, false)
        return ProfessionalQFAdapter.ProfessionalViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if (itemList == null) 0 else itemList.size
    }

    override fun onBindViewHolder(holder: ProfessionalViewHolder, position: Int) {

        holder.ivCollapsedLogo?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_professional_icon))
        val dModel = itemList.get(position)
        holder.tvTitle?.text = dModel.certification
        holder.tvOrgName?.text = dModel.institute
        holder.tvCompletionYear?.text = "${dModel.from} - ${dModel.to}"
        holder.tvTrTopic?.text = dModel.location


        holder.tvTrCountry?.hide()
        holder.tvTrLoc?.hide()
        holder.topicsCoverd!!.text = "Location"
        holder.linearLayout3!!.hide()
        holder.linearLayout4!!.hide()


        if (dModel.location == "") {
            holder.moreActionDetails?.hide()
            holder.imageViewExpand?.hide()
        } else {
            holder.moreActionDetails?.show()
            holder.imageViewExpand?.show()
        }


        holder.ivEdit?.setOnClickListener {
            call.passProfessionalData(dModel)
            d("From list : ${dModel.certification}")
            call.goToEditInfo("editProfessional")
        }

        holder.imageViewExpand!!.setOnClickListener {
            Log.d("click", "clicked success")
            toggleDetails(holder)
        }
        holder.moreActionDetails?.visibility = View.GONE

    }


    private fun toggleDetails(holder: ProfessionalQFAdapter.ProfessionalViewHolder) {
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


    class ProfessionalViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {


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
        var topicsCoverd: TextView? = itemView?.findViewById(R.id.topicsCoverd)
        var linearLayout3: LinearLayout? = itemView?.findViewById(R.id.linearLayout3)
        var linearLayout4: LinearLayout? = itemView?.findViewById(R.id.linearLayout4)


    }

}