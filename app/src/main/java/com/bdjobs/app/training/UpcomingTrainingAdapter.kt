package com.bdjobs.app.training

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.TrainingListData
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Web.WebActivity
import org.jetbrains.anko.startActivity

class UpcomingTrainingAdapter(private var context: Context) : RecyclerView.Adapter<TrainingListViewHolder>() {

    private var trainingList: ArrayList<TrainingListData>? = ArrayList()

    override fun onBindViewHolder(holder: TrainingListViewHolder, position: Int) {
        holder.trainingName.text = trainingList?.get(position)?.topic
        holder.trainingVenue.text = trainingList?.get(position)?.venue
        holder.trainingDate.text = trainingList?.get(position)?.date
       // holder.adapterPosition(position)
        holder.itemView.setOnClickListener {
            try {

                val url = "https://bdjobstraining.com/trainingdetails.asp?" + trainingList?.get(position)?.detailurl
                println("my page $url")

                context.startActivity<WebActivity>(
                        "from" to "training",
                        "url" to url

                )
            } catch (e: Exception) {
               logException(e)

            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingListViewHolder {
        return TrainingListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_training_list, parent, false))

    }

    override fun getItemCount(): Int {
        return if (trainingList == null) 0 else trainingList!!.size
    }

    fun add(r: TrainingListData) {
        trainingList?.add(r)
        notifyItemInserted(trainingList!!.size - 1)
    }

    fun addAll(moveResults: List<TrainingListData>) {
        for (result in moveResults) {
            add(result)
        }
    }

    fun removeAll() {
        trainingList?.clear()
        notifyDataSetChanged()
    }
}

class TrainingListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val trainingName = view.findViewById(R.id.jobtitleTV) as TextView
    val trainingVenue = view.findViewById(R.id.companyNameTV) as TextView
    val trainingDate = view.findViewById(R.id.appliedDateTV) as TextView

}