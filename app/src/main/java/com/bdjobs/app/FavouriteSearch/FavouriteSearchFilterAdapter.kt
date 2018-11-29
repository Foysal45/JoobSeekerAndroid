package com.bdjobs.app.FavouriteSearch

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.Databases.Internal.FavouriteSearch
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.toSimpleDateString
import com.bdjobs.app.Utilities.toSimpleTimeString

class FavouriteSearchFilterAdapter(private val context: Context, private val items: List<FavouriteSearch>) : RecyclerView.Adapter<ViewHolder>() {

    val activity = context as Activity
    val dataStorage = DataStorage(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.fav_list_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.favTitle1TV.text = items[position].filtername
        holder.dateTV.text = items[position].createdon?.toSimpleDateString()
        holder.timeTV.text = items[position].createdon?.toSimpleTimeString()

    }

    override fun getItemCount(): Int {
        return items?.size!!
    }


}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val favTitle1TV = view.findViewById(R.id.favTitle1TV) as TextView
    val dateTV = view.findViewById(R.id.createdOnDateTV) as TextView
    val timeTV = view.findViewById(R.id.time1TV) as TextView
    val filter1TV = view.findViewById(R.id.filter1TV) as TextView

}