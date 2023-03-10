package com.bdjobs.app.editResume.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.editResume.adapters.models.DataItem
import com.bdjobs.app.editResume.callbacks.EmpHisCB
import com.google.gson.Gson
import timber.log.Timber

class EmpHistoryAdapter(arr: java.util.ArrayList<DataItem>, val context: Context) : RecyclerView.Adapter<EmpHistoryAdapter.MyViewHolder>() {

    private var call: EmpHisCB = context as EmpHisCB
    private val DURATION = 300
    private var itemList: MutableList<DataItem>? = arr
    private lateinit var dataStorage: DataStorage

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_experiece_list, parent, false)
        dataStorage = DataStorage(context)
        return MyViewHolder(itemView)

    }

    override fun getItemCount(): Int {
        return if (itemList == null) 0 else itemList!!.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dModel = itemList?.get(position)!!
        //call.saveExpsArray(dModel.areaofExperience as ArrayList<AreaofExperienceItem>)

        Timber.d("Data: ${Gson().toJson(dModel)}")

        if (position == itemList?.size!! - 1) {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = 200
            holder.itemView.layoutParams = params
        } else {
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = 0
            holder.itemView.layoutParams = params
        }
        holder.tvDes?.text = dModel.positionHeld
        holder.tvDate?.text = "From ${dModel.from} to ${dModel.to}"

        Timber.d("Com: ${dModel.companyName} :: Add: ${dModel.companyLocation}")

        holder.tvCom?.text = dModel.companyName
        holder.tvAddress?.text = dModel.companyLocation

        holder.tvDept?.text = dModel.departmant

        //Log.d("dsgjdhsg", "in adpater companyBusiness ${dataStorage.getOrgNameByID(dModel.companyBusiness!!)}")
        //Log.d("dsgjdhsg", "in adpater companyBusiness ID ${dModel.companyBusiness} ")

        holder.tvComBus?.text = dModel.companyBusiness
        val areaOfExp = dModel.areaofExperience
        val idArr = ArrayList<String>()
        var exps = ""
        if (areaOfExp?.size != 0) {
            for ((i, value) in areaOfExp?.withIndex()!!) {

                Log.i("gotValue", "val: ${value?.id} and ${value?.expsName} and $position")
                //value?.let { call.passAreaOfExpsData(it) }
                idArr.add(value?.id.toString())
                Log.i("IdArrayView", "$idArr")
                exps += if (i == areaOfExp.size - 1) {
                    "${value?.expsName}"
                } else {
                    "${value?.expsName},"
                }
            }
        }
        call.setExpIDs(idArr)

        holder.tvAreaOfExp?.text = exps

        holder.tvRespos?.text = dModel.responsibility
        holder.ivEdit?.setOnClickListener {
            call.passData(dModel)
            call.goToEditInfo("edit")
            call.setIsFirst(true)
        }

        holder.imageViewExpand!!.setOnClickListener {
            //Log.d("click", "clicked success")
            toggleDetails(holder)
        }
        holder.moreActionDetails?.visibility = View.GONE
    }
    private fun toggleDetails(holder: MyViewHolder) {
        var visibility: Int = holder.moreActionDetails!!.visibility
        if (visibility == View.GONE) {
            visibility = View.VISIBLE
            Timber.d("iftoggleDetails: $visibility")
//            ExpandAndCollapseViewUtil.expand(holder.moreActionDetails!!, DURATION)
            holder.moreActionDetails?.visibility = View.VISIBLE
            holder.imageViewExpand!!.setImageResource(R.drawable.ic_arrow_up)
        } else {
//            ExpandAndCollapseViewUtil.collapse(holder.moreActionDetails!!, DURATION)

            holder.moreActionDetails?.visibility = View.GONE
            Timber.d("elsetoggleDetails: $visibility")
            holder.imageViewExpand!!.setImageResource(R.drawable.ic_arrow_down)
        }
    }

/*    private fun add(item: DataItem) {
        itemList?.add(item)
        notifyItemInserted(itemList?.size?.minus(1)!!)
    }

    fun addAll(items: ArrayList<DataItem>) {
        for (result in items) {
            add(result)
        }
        notifyDataSetChanged()
    }*/

    class MyViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var moreActionDetails: RelativeLayout? = itemView!!.findViewById(R.id.cl_expanded)
        var imageViewExpand: ImageView? = itemView!!.findViewById(R.id.iv_details)
        var ivEdit: ImageView? = itemView!!.findViewById(R.id.iv_edit)
        var tvDes: TextView? = itemView?.findViewById(R.id.tvDes)
        var tvDate: TextView? = itemView?.findViewById(R.id.tvDate)
        var tvCom: TextView? = itemView?.findViewById(R.id.tvCom)
        var tvAddress: TextView? = itemView?.findViewById(R.id.tvAddress)
        var tvComBus: TextView? = itemView?.findViewById(R.id.tv_comBus)
        var tvDept: TextView? = itemView?.findViewById(R.id.tv_cDept)
        var tvAreaOfExp: TextView? = itemView?.findViewById(R.id.tv_area_exps)
        var tvRespos: TextView? = itemView?.findViewById(R.id.tv_respons)
    }
}