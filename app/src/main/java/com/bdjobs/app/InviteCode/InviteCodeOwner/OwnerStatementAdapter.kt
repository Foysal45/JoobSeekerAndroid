package com.bdjobs.app.InviteCode.InviteCodeOwner

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import com.bdjobs.app.API.ModelClasses.InviteCodeOwnerStatementModelData
import com.bdjobs.app.R
import com.bdjobs.app.utilities.equalIgnoreCase
import com.bdjobs.app.utilities.toBanglaDigit
import java.util.ArrayList

class OwnerStatementAdapter(ctx: Context, private val inviteCodeOwnerStatementModelData: ArrayList<InviteCodeOwnerStatementModelData>) : BaseAdapter() {

    private val mInflator: LayoutInflater = LayoutInflater.from(ctx)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ListRowHolder

        if (convertView == null) {
            view = this.mInflator.inflate(R.layout.statement_list, parent, false)
            vh = ListRowHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ListRowHolder
        }

        if (position % 2 == 0) {
            vh.mainRL.setBackgroundColor(Color.parseColor("#FFFFFF"))
        } else {
            vh.mainRL.setBackgroundColor(Color.parseColor("#E4EEF2"))
        }


        var date: String? = null
        var money: String? = null
        var balance: String? = null
        try {
            date = inviteCodeOwnerStatementModelData[position].date.toBanglaDigit()
            money = "৳ " + inviteCodeOwnerStatementModelData[position].amount.toBanglaDigit()
            balance = "৳ " + inviteCodeOwnerStatementModelData[position].balance.toBanglaDigit()
        } catch (e: Exception) {
            e.printStackTrace()
        }


        vh.dateTV.text = date
        vh.moneyTV.text = money
        vh.balanceTV.text = balance

        if (inviteCodeOwnerStatementModelData.get(position).type.equalIgnoreCase("In")) {
            vh.moneyTV.setTextColor(Color.parseColor("#43A047"))
        } else if (inviteCodeOwnerStatementModelData.get(position).type.equalIgnoreCase("Out")) {
            vh.moneyTV.setTextColor(Color.parseColor("#B92D00"))
        }


        return view
    }

    override fun getItem(position: Int): Any {
        return inviteCodeOwnerStatementModelData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return inviteCodeOwnerStatementModelData.size
    }


    inner class ListRowHolder(row: View) {
        val moneyTV = row.findViewById(R.id.moneyTV) as TextView
        val dateTV = row.findViewById(R.id.dateTV) as TextView
        val balanceTV = row.findViewById(R.id.balanceTV) as TextView
        val mainRL = row.findViewById(R.id.mainRL) as RelativeLayout
    }
}