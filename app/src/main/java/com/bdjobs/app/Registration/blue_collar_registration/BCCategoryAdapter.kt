package com.bdjobs.app.Registration.blue_collar_registration

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.bdjobs.app.R
import com.bdjobs.app.Registration.RegistrationCommunicator

class BCCategoryAdapter(private val context: Context, private val items: ArrayList<String>) : RecyclerView.Adapter<ViewHolder>() {

    val activity = context as Activity

    private var selectedPosition = -1
    private  val registrationCommunicator = activity as RegistrationCommunicator

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.category_list_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.categoryTV.text = items[position]
        Log.d("elkgjtsdlgfghfdh","Item ${items.get(position)}")
        Log.d("elkgjtsdlgfghfdh", "position $position")
        holder.categoryTV.setOnClickListener {
            selectedPosition = position
            registrationCommunicator.bcCategorySelected(items[position], selectedPosition)
            notifyDataSetChanged()
            Log.d("selectedPosition", "selectedPosition $selectedPosition  ${items[position]}")

        }
        if (selectedPosition == position) {
            holder.categoryTV.backgroundTintList = context.resources.getColorStateList(R.color.colorPrimary)
            holder.categoryTV.setTextColor(context.resources.getColor(R.color.colorWhite))
        }else {
            holder.categoryTV.backgroundTintList = context.resources.getColorStateList(R.color.colorWhite)
            holder.categoryTV.setTextColor(context.resources.getColor(R.color.colorPrimary))

        }

    }

    override fun getItemCount(): Int {
        return items.size
    }


    fun SetCategoryPositionSelected(categoryPositionSelected: Int) {
        selectedPosition = categoryPositionSelected
        notifyDataSetChanged()
        Log.d("SetCategory", categoryPositionSelected.toString())
    }
}
class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val categoryTV = view.findViewById(R.id.categoryText) as TextView
}