package com.bdjobs.app.LoggedInUserLanding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.MybdjobsData
import com.bdjobs.app.R

class MybdjobsAdapter(val context: Context) : RecyclerView.Adapter<MyBdjobsViewHolder>() {

    private var mybdjobsItems: ArrayList<MybdjobsData>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBdjobsViewHolder {
        return MyBdjobsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_grid, parent, false))


    }

    override fun getItemCount(): Int {
        return return if (mybdjobsItems == null) 0 else mybdjobsItems!!.size

    }

    override fun onBindViewHolder(holder: MyBdjobsViewHolder, position: Int) {
        /* holder.resourceID_Value.background = context.getDrawable(moreItems!![position].resourceID)
         holder.resourceName_Value.text = moreItems!![position].resourceName*/

    }

    fun add(r: MybdjobsData) {
        mybdjobsItems?.add(r)
        notifyItemInserted(mybdjobsItems!!.size - 1)
    }

    fun addAll(moveResults: List<MybdjobsData>) {
        for (result in moveResults) {
            add(result)
        }
    }

}

class MyBdjobsViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    var itemName: TextView = itemView.findViewById(R.id.item_name_TV)
    var itemValue: TextView = itemView.findViewById(R.id.item_value_TV)


}