package com.bdjobs.app.ajkerDeal.checkout

import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.ajkerDeal.api.models.order.PaymentMediumModel
import com.bdjobs.app.ajkerDeal.utilities.DigitConverter
import com.bumptech.glide.Glide
import timber.log.Timber

class PaymentMediumAdapter(private var dataList: MutableList<PaymentMediumModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var deliveryType = com.bdjobs.app.ajkerDeal.api.models.checkout.DeliveryType.NORMAL
    var selectedIndex: Int = -1
    var isMoreOpen: Boolean = false
    var onItemSelected: ((position: Int, model: PaymentMediumModel) -> Unit)? = null
    var adCashAmount: Float = 0.0f
    var showSpecialDeliveryCharge: Boolean = false
    //private val options = RequestOptions().

    override fun getItemViewType(position: Int): Int {
        return if (!dataList[position].isMore) {
            1
        } else {
            2
        }
    }

    override fun getItemId(position: Int): Long {
        return dataList[position].id.toLong()
    }

    /**
     * onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1){
            ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_view_payment_medium,
                    parent,
                    false
                )
            )
        } else {
            ViewHolder1(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_view_payment_medium_more,
                    parent,
                    false
                )
            )
        }
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

            if (model.isActive == 1){
                holder.parent.visibility = View.VISIBLE
            } else {
                holder.parent.visibility = View.GONE
                return
            }

            if (model.isMajor == 1) {
                holder.parent.visibility = View.VISIBLE
            } else {
                if (isMoreOpen) {
                    holder.parent.visibility = View.VISIBLE
                } else {
                    holder.parent.visibility = View.GONE
                    return
                }
            }

            Glide.with(holder.logo)
                .load(model.icon)
                .thumbnail(1f)
                .into(holder.logo)

            var mediumName = model.name
            if (model.id == 2) {
                mediumName += "\n৳ ${DigitConverter.toBanglaDigit(adCashAmount.toString())}"
            }
            holder.paymentName.text = mediumName

            var charge = 0
            if (deliveryType == com.bdjobs.app.ajkerDeal.api.models.checkout.DeliveryType.NORMAL) {
                charge = model.deliveryCharges.regularDeliveryCharge
                if (charge > 0) {
                    holder.paymentAmount.setTextColor(ContextCompat.getColor(holder.paymentAmount.context, R.color.color_blue))
                } else { // Free
                    holder.paymentAmount.setTextColor(ContextCompat.getColor(holder.paymentAmount.context, R.color.aDheaderColor))
                }
            } else if (deliveryType == com.bdjobs.app.ajkerDeal.api.models.checkout.DeliveryType.EXPRESS) {
                charge = model.deliveryCharges.expressDeliveryCharge
                if (charge > 0) {
                    holder.paymentAmount.setTextColor(ContextCompat.getColor(holder.paymentAmount.context, R.color.aDheaderColor))
                } else { // Free
                    holder.paymentAmount.setTextColor(ContextCompat.getColor(holder.paymentAmount.context, R.color.aDheaderColor))
                }
            }

            if (showSpecialDeliveryCharge) {
                charge = model.deliveryCharges.specialDeliveryCharge
            }
            val deliveryChargeText = if (charge > 0) {
                holder.paymentAmount.setTypeface(holder.paymentAmount.typeface, Typeface.NORMAL)
                "৳ ${DigitConverter.toBanglaDigit(charge.toString())}"
            } else {
                holder.paymentAmount.setTypeface(holder.paymentAmount.typeface, Typeface.BOLD)
                holder.paymentAmount.setPadding(0, 0, 11, 0)
                holder.paymentAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                "ফ্রি "
            }
            holder.paymentAmount.text = deliveryChargeText

            if (selectedIndex == position) {
                holder.parent.background = ContextCompat.getDrawable(holder.parent.context, R.drawable.bg_checkout_payment_select)
            } else {
                holder.parent.background = null
                holder.parent.setBackgroundColor(ContextCompat.getColor(holder.parent.context, R.color.white))
            }


        } else if (holder is ViewHolder1) {

            if (!isMoreOpen) {
                holder.arrow.rotation = 0.0f
            } else {
                holder.arrow.rotation = 180f
            }
        }
    }

    /**
     * ViewHolder
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        //internal val parentCard: CardView = view.findViewById(R.id.payment_medium_parent_card)
        internal val parent: ConstraintLayout = view.findViewById(R.id.payment_medium_parent)
        internal val logo: ImageView = view.findViewById(R.id.payment_medium_logo)
        internal val paymentName: TextView = view.findViewById(R.id.payment_medium_name)
        internal val paymentAmount: TextView = view.findViewById(R.id.payment_medium_amount)


        init {
            view.setOnClickListener {

                val index = adapterPosition
                if (index >= 0 && index < dataList.size) {
                    selectedIndex = index
                    onItemSelected?.invoke(index, dataList[index])
                    notifyDataSetChanged()

                    Timber.d("dataList_size\n" + dataList.size)

                    var data_Size = dataList.size

                    for (i in dataList) {
                     Timber.d("dataList_size_1\n" + i)
                    }
                }
            }
        }
    }

    inner class ViewHolder1(view: View) : RecyclerView.ViewHolder(view) {

        internal val parentCard: CardView = view.findViewById(R.id.payment_medium_more)
        internal val arrow: ImageView = view.findViewById(R.id.payment_medium_more_arrow)

        init {
            parentCard.setOnClickListener {
                isMoreOpen = !isMoreOpen
                notifyDataSetChanged()
            }
        }
    }

}