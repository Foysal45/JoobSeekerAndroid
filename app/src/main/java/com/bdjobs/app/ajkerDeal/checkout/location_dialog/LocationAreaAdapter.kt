package com.bdjobs.app.ajkerDeal.checkout.location_dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.ajkerDeal.api.models.location.ThanaInfoModel
import com.bdjobs.app.ajkerDeal.utilities.DigitConverter
import timber.log.Timber

class LocationAreaAdapter(private var dataList: MutableList<ThanaInfoModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //private var thanaList: MutableList<ThanaInfoModel> = mutableListOf()

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
            val msg = "${model.thanaBng}" + if (!model.postalCode.isNullOrEmpty()) " - ${DigitConverter.toBanglaDigit(model.postalCode)}" else ""
            holder.title.text = msg
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
                    Timber.d("Area Position :- " + adapterPosition + " " + dataList[index]+ " " +  dataList)

                    if (dataList[index].isBlock == 1){
                   //   Timber.d("isBlockedArea 1 " + dataList[index].isBlocked)  /*উপজেলা/থানা লেভেল*/
                    Toast.makeText(view.context, "এই এরিয়াতে সাময়িকভাবে ডেলিভারি দেয়া বন্ধ আছে। আপনি চাইলে " + dataList[index].parentName + " থেকে প্রোডাক্ট কালেক্ট করতে পারেন", Toast.LENGTH_LONG).show()

                    } else{
                     //   Timber.d("isBlockedArea 2 " + dataList[index].isBlocked)
                        onItemClicked?.invoke(index, dataList[index])
                    }
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