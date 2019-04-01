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
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.AcaDataItem
import com.bdjobs.app.editResume.callbacks.EduInfo

class AcademicInfoAdapter(arr: java.util.ArrayList<AcaDataItem>, val context: Context) : RecyclerView.Adapter<AcademicInfoAdapter.MyViewHolder>() {

    private var call: EduInfo = context as EduInfo
    private val DURATION = 300
    private var itemList: MutableList<AcaDataItem>? = arr

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_acainfo_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if (itemList == null) 0 else itemList!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Log.d("aca", "calling")
        holder.ivCollapsedLogo?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_education_icon))

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
        val resultId = dModel.resultId!!

        if (resultId.equalIgnoreCase("0")) {
            holder.tvAcaResult!!.hide()
        }



        if (resultId.equalIgnoreCase("8") ||
                resultId.equalIgnoreCase("9") ||
                resultId.equalIgnoreCase("10") ||
                resultId.equalIgnoreCase("12")) {
            holder.tvAcaResult!!.show()
            holder.tvAcaResult?.text = dModel.result
        } else {
            when {
                dModel.marks.equals("0") && dModel.scale.equals("0") -> holder.tvAcaResult?.hide()
                !dModel.marks.equals("0") && !dModel.scale.equals("0") -> {
                    holder.tvAcaResult?.show()
                    holder.tvAcaResult?.text = "CGPA ${dModel.marks} Out of ${dModel.scale}"

                    if (dModel.result.isNullOrEmpty() && resultId.isEmpty() && dModel.marks.isNullOrEmpty() && dModel.scale.isNullOrEmpty()) {

                        d("djkgndj in null condiiton")
                        holder.tvAcaResult!!.hide()
                    }

                }
                !dModel.marks.equals("0") && dModel.scale.equals("0") -> {
                    holder.tvAcaResult?.show()
                    holder.tvAcaResult?.text = "Marks ${dModel.marks}%"

                }


            }

        }




        holder.tvDegree?.text = dModel.examDegreeTitle

        holder.tvUniName?.text = dModel.instituteName
        if (dModel.acievement == "") {
            holder.moreActionDetails?.hide()
            holder.imageViewExpand?.hide()
        } else {
            holder.moreActionDetails?.show()
            holder.imageViewExpand?.show()
        }
        holder.tvAchievement?.text = dModel.acievement
        Log.d("aca", "${dModel.examDegreeTitle}")

        holder.ivEdit?.setOnClickListener {
            call.passData(dModel)
            call.goToEditInfo("edit")
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
        var tvDegree: TextView? = itemView?.findViewById(R.id.tvDes)
        var tvAcaResult: TextView? = itemView?.findViewById(R.id.tvDate)
        var tvUniName: TextView? = itemView?.findViewById(R.id.tvCom)
        var tvAchievement: TextView? = itemView?.findViewById(R.id.tvAchievement)
        var ivCollapsedLogo: ImageView? = itemView?.findViewById(R.id.iv_c_logo)
    }
}