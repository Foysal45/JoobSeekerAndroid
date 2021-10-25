package com.bdjobs.app.ajkerDeal.checkout.location_dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.ajkerDeal.api.models.location.ThanaInfoModel

class LocationThanaAdapter(private var dataList: MutableList<ThanaInfoModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClicked: ((position: Int, districtModel: ThanaInfoModel?) -> Unit)? = null

    /**
     * onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.district_select_layout,
                parent,
                false
            )
        )
    }

    /**
     * getItemCount
     */
    override fun getItemCount(): Int {
        return dataList.size
    }

    /**
     * onBindViewHolder
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ViewHolder) {

            val model = dataList[position]

            holder.title.text = model.thanaBng
        }
    }
    /**
     * ViewHolder
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal val title: TextView = view.findViewById(R.id.district_spinner_item_id)

        init {
            view.setOnClickListener {
                val index = adapterPosition
                if (index >= 0 && index < dataList.size) {
                    onItemClicked?.invoke(index, dataList[index])
                }
            }
        }
    }

    fun setDataList(list: List<ThanaInfoModel>) {
        dataList.clear()
        dataList.addAll(list)
        notifyDataSetChanged()
    }
}