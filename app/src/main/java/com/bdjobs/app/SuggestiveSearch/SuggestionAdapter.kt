package com.bdjobs.app.SuggestiveSearch

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.logException

class SuggestionAdapter(var itemList: ArrayList<String>, private val context: Context) : RecyclerView.Adapter<SuggestionAdapter.ViewHolder>(), Filterable {


    var filteredItems: ArrayList<String>? = null
    var communicator: SuggestionCommunicator? = null

    init {
        communicator = context as SuggestionCommunicator
    }

    init {
        filteredItems = itemList
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            holder.itemTV.text = filteredItems!![position]
            holder.itemTV.setOnClickListener {
                communicator?.suggestionSelected(holder.itemTV.text.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_filterlist, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return filteredItems?.size!!
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemTV = itemView.findViewById<TextView>(R.id.itemNameTV)!!
    }


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


}