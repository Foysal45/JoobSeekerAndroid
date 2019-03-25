package com.bdjobs.app.InviteCode.InviteCodeOwner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.bdjobs.app.API.ModelClasses.InviteCodeCategoryAmountModelData
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.toBanglaDigit
import java.util.*

class InvitecodeCategoryListAdapter(ctx: Context?, private val categoryAmountList: ArrayList<InviteCodeCategoryAmountModelData>) : BaseAdapter() {

    private val mInflator: LayoutInflater = LayoutInflater.from(ctx)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ListRowHolder

        if (convertView == null) {
            view = this.mInflator.inflate(R.layout.invitecode_category_list_layout, parent, false)
            vh = ListRowHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ListRowHolder
        }

        vh.categoryNameTV?.text = categoryAmountList.get(position).categoryName
        vh.categoryMoneyTV?.text = categoryAmountList.get(position).ownerAmount.toBanglaDigit() + "/-"

        return view
    }

    override fun getItem(position: Int): Any {
        return categoryAmountList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return categoryAmountList.size
    }


    inner class ListRowHolder(row: View) {
        var categoryNameTV: TextView = row.findViewById(R.id.categoryNameTV) as TextView
        var categoryMoneyTV: TextView = row.findViewById(R.id.categoryMoneyTV) as TextView
    }
}