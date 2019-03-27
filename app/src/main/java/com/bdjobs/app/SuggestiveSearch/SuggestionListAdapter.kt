package com.bdjobs.app.SuggestiveSearch

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.logException
import java.util.*

class SuggestionListAdapter(var itemList: ArrayList<String>, private val context: Context) : BaseAdapter(), Filterable {


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {

                Log.d("aaa", "Size:e")
                val charString = charSequence.toString()
                filteredItems = if (charString.isEmpty()) {
                    itemList
                } else {
                    val filteredList = ArrayList<String>()
                    for (row in itemList) {
                        if (row.toLowerCase().contains(charString.toLowerCase()) || row.contains(charSequence)) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults = Filter.FilterResults()
                filterResults.values = filteredItems
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                filteredItems = filterResults.values as ArrayList<String>
                Log.d("aaa", "Size: ${filterResults.count}")
                notifyDataSetChanged()
            }
        }
    }

    var filteredItems: ArrayList<String>? = null
    var communicator: SuggestionCommunicator? = null
    private val mInflator: LayoutInflater = LayoutInflater.from(context)

    init {
        communicator = context as SuggestionCommunicator
    }

    init {
        filteredItems = itemList
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ListRowHolder

        if (convertView == null) {
            view = this.mInflator.inflate(R.layout.item_filterlist, parent, false)
            vh = ListRowHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ListRowHolder
        }

        try {
            vh.itemTV?.text =  filteredItems?.get(position)
            vh.itemTV?.setOnClickListener {
                communicator?.suggestionSelected(vh?.itemTV?.text.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }

        return view
    }

    override fun getItem(position: Int): Any {
        return filteredItems?.get(position)!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return filteredItems?.size!!
    }

    inner class ListRowHolder(row: View) {
        val itemTV = row?.findViewById<TextView>(R.id.itemNameTV)
    }
}