package com.bdjobs.app.ajkerDeal.checkout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.ajkerDeal.checkout.model.CheckoutUserData
import com.bdjobs.app.ajkerDeal.utilities.DigitConverter

class DeliveryAddressAdapter(private var dataList: List<CheckoutUserData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var selectedIndex: Int = -1
    var onItemSelected: ((position: Int, CheckoutUserData: CheckoutUserData) -> Unit)? = null
    var onItemEditPressed: ((position: Int, CheckoutUserData: CheckoutUserData) -> Unit)? = null

    /**
     * onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_view_delivery_address,
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

            holder.checkBox.isChecked = selectedIndex == position

            val msg =
                "${model.billingAddress},\n${if (model.areaName.isEmpty()) "" else "${model.areaName},\n"}${model.thanaName}, ${model.districtName}${if (model.postCode.isNullOrEmpty()) "" else " - ${DigitConverter.toBanglaDigit(model.postCode)}"}"

            holder.deliveryAddress.text = msg
        }
    }

    /**
     * ViewHolder
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal val parent: ConstraintLayout = view.findViewById(R.id.addressBookParent)
        internal val checkBox: AppCompatCheckBox = view.findViewById(R.id.addressBookCheckBox1)
        internal val deliveryAddress: TextView = view.findViewById(R.id.addressBookAddress1)
        internal val editBtn: LinearLayout = view.findViewById(R.id.addressBookEditLayout)

        init {
            view.setOnClickListener {
                val index = adapterPosition
                if (index >= 0 && index < dataList.size) {
                    selectedIndex = index
                    onItemSelected?.invoke(index, dataList[index])
                    notifyDataSetChanged()
                }
            }
            editBtn.setOnClickListener {
                val index = adapterPosition
                if (index >= 0 && index < dataList.size) {
                    onItemEditPressed?.invoke(index, dataList[index])
                }
            }

        }
    }
}