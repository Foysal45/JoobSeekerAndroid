package com.bdjobs.app.SuggestiveSearch

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bdjobs.app.R


class HistoryAdapter(val itemList: ArrayList<String>, private val context: Context): RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    var communicator: SuggestionCommunicator? = null

    init {
        communicator = context as SuggestionCommunicator
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemTV.text = itemList!![position]
        holder.itemTV.setOnClickListener {
            communicator?.suggestionSelected(holder.itemTV.text.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_historylist, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return itemList?.size!!
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemTV = itemView.findViewById<TextView>(R.id.itemNameTV)!!
    }

}