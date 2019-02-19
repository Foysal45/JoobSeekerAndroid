package com.bdjobs.app.LoggedInUserLanding

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.MybdjobsData
import com.bdjobs.app.Employers.EmployersBaseActivity
import com.bdjobs.app.R
import org.jetbrains.anko.toast

class MybdjobsAdapter(val context: Context) : RecyclerView.Adapter<MyBdjobsViewHolder>() {

    val activity = context as Activity
    private var mybdjobsItems: ArrayList<MybdjobsData>? = ArrayList()
    private val communicator = activity as HomeCommunicator

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyBdjobsViewHolder {
        return MyBdjobsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_grid, parent, false))


    }

    override fun getItemCount(): Int {
        return return if (mybdjobsItems == null) 0 else mybdjobsItems!!.size

    }

    override fun onBindViewHolder(holder: MyBdjobsViewHolder, position: Int) {

        holder?.itemName?.text = mybdjobsItems!![position].itemName
        holder?.itemValue?.text = mybdjobsItems!![position].itemID
        holder?.backgroundRRL?.setBackgroundResource(mybdjobsItems!![position].backgroundID)
        holder?.item_icon?.setBackgroundResource(mybdjobsItems!![position].resourceID)
        //holder.itemName[position]
            holder?.item_Card.setOnClickListener {
                if(mybdjobsItems!![position].itemID.toInt() > 0) {
                    when (mybdjobsItems!![position].itemName) {
                        "Jobs\nApplied" -> communicator.goToAppliedJobs()
                        "Employers\nFollowed" -> communicator.goToFollowedEmployerList("follow")
                        "Interview\nInvitations" -> communicator.goToInterviewInvitation("mybdjobs")
                        "Employers Viewed\nResume" -> communicator.goToEmployerViewedMyResume("vwdMyResume")
                        else -> { // Note the block
                            print("not found")
                        }

                    }
                }

            }



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

    fun removeAll() {
        mybdjobsItems?.clear()
        notifyDataSetChanged()
    }

}

class MyBdjobsViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

    var itemName: TextView = itemView?.findViewById(R.id.item_name_TV)
    var itemValue: TextView = itemView?.findViewById(R.id.item_value_TV)
    var backgroundRRL: RelativeLayout = itemView?.findViewById(R.id.background_RRL)
    var item_icon: ImageView = itemView?.findViewById(R.id.iv_item_icon)
    var item_Card: CardView = itemView?.findViewById(R.id.mybdjobsStatsCard)


}