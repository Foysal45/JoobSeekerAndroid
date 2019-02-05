package com.bdjobs.app.InviteCode.InviteCodeOwner

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bdjobs.app.API.ModelClasses.OwnerInviteListModelData
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.loadCircularImageFromUrl
import com.bdjobs.app.Utilities.toBanglaDigit
import java.util.ArrayList

class InvitecodeInviteListAdapter(ctx: Context, private val ownerInviteListModelData: ArrayList<OwnerInviteListModelData>, val state: Int) : BaseAdapter() {

    private val mInflator: LayoutInflater = LayoutInflater.from(ctx)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ListRowHolder

        if (convertView == null) {
            view = this.mInflator.inflate(R.layout.account_success_list_layout, parent, false)
            vh = ListRowHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ListRowHolder
        }
        vh.nameTV.text = ownerInviteListModelData[position].name
        vh.categoryTv.text = ownerInviteListModelData[position].category
        vh.moneyTV.text = ownerInviteListModelData[position].ownerAmount.toBanglaDigit() + "/-"
        vh.dateTv.text = ownerInviteListModelData[position].createdDate.toBanglaDigit()

        vh.profileIMGV.loadCircularImageFromUrl(ownerInviteListModelData[position].photoUrl)


        if (state == 1) {
            vh.moneyTV.setTextColor(Color.parseColor("#F57F17"))
            vh.moneyIconIMGV.setBackgroundResource(R.drawable.account_list_money_icon_active)
            if (ownerInviteListModelData[position].verifyStatus.equalIgnoreCase("2")) {
                vh.moneyTypeTV.visibility = View.VISIBLE
                vh.moneyTypeTV.text = "বাতিল হয়েছে "
                vh.moneyTypeTV.setTextColor(Color.parseColor("#F44336"))
            }
        }

        if (state == 0) {

            vh.moneyTypeTV.visibility = View.VISIBLE

            if (ownerInviteListModelData[position].paidStatus.equalIgnoreCase("True")) {
                vh.moneyTV.setTextColor(Color.parseColor("#AC016D"))
                vh.moneyTypeTV.setTextColor(Color.parseColor("#AC016D"))
                vh.moneyTypeTV.text = "ট্রান্সফার হয়েছে "
                vh.moneyIconIMGV.setBackgroundResource(R.drawable.account_list_money_icon)

            } else if (ownerInviteListModelData[position].paidStatus.equalIgnoreCase("False")) {
                vh.moneyTypeTV.setTextColor(Color.parseColor("#43A047"))
                vh.moneyTV.setTextColor(Color.parseColor("#43A047"))
                vh.moneyIconIMGV.setBackgroundResource(R.drawable.account_list_submit_money_icon)
            }
        }


        return view
    }

    override fun getItem(position: Int): Any {
        return ownerInviteListModelData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return ownerInviteListModelData.size
    }


    inner class ListRowHolder(row: View) {
        val nameTV = row.findViewById(R.id.nameTV) as TextView
        val heading1TV = row.findViewById(R.id.heading1TV) as TextView
        val categoryTv = row.findViewById(R.id.categoryTv) as TextView
        val heading2TV = row.findViewById(R.id.heading2TV) as TextView
        val moneyTV = row.findViewById(R.id.moneyTV) as TextView
        val dateTv = row.findViewById(R.id.dateTv) as TextView
        val profileIMGV = row.findViewById(R.id.profileIMGV) as ImageView
        val moneyIconIMGV = row.findViewById(R.id.moneyIconIMGV) as ImageView
        val moneyTypeTV = row.findViewById(R.id.moneyTypeTV) as TextView
    }
}