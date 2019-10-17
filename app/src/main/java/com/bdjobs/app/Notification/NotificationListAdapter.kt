package com.bdjobs.app.Notification

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bdjobs.app.Databases.Internal.Notification
import com.bdjobs.app.R
import org.jetbrains.anko.toast


class NotificationListAdapter(private val context: Context, private val items: MutableList<Notification>) : RecyclerView.Adapter<ViewHolder>() {


    private val notificationCommunicatior = context as NotificationCommunicatior

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items?.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.notificationTV?.text = "${items[position].payload} \n${items[position].seenTime.toString()}\n\n\n"
        holder.notificationTV.setOnClickListener {
            context.toast("Coming soon")
        }
    }

    fun removeItem(position: Int){
        items?.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position,items?.size)
    }

    fun restoreItem(position: Int, notification: Notification){
        items?.add(position,notification)
        notifyItemInserted(position)
    }

}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val notificationTV = view?.findViewById(R.id.notification_tv) as TextView


}