package com.bdjobs.app.SuggestiveSearch

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.logException
import java.util.*

class HistoryListAdapter(var itemList: ArrayList<String>, private val context: Context) : BaseAdapter() {
    var communicator: SuggestionCommunicator? = null

    init {
        communicator = context as SuggestionCommunicator
    }
    private val mInflator: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ListRowHolder

        if (convertView == null) {
            view = this.mInflator.inflate(R.layout.item_historylist, parent, false)
            vh = ListRowHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ListRowHolder
        }

        try {
            vh.itemTV?.text = itemList.get(position)
            vh.itemTV?.setOnClickListener {
                communicator?.suggestionSelected(vh.itemTV.text.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }

        return view
    }

    override fun getItem(position: Int): Any {
        return itemList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return itemList.size
    }

    inner class ListRowHolder(row: View) {
        val itemTV = row.findViewById<TextView>(R.id.itemNameTV)
    }
}