package com.bdjobs.app.Login

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.SocialLoginAccountListData
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.loadCircularImageFromUrl

class SocialRVadapter(private val items: List<SocialLoginAccountListData?>?, val context: Context) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.social_account_list_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameTV.text = items?.get(position)?.fullName
        holder.emailTV.text = items?.get(position)?.email

        Log.d("CheckPhotoUrl","Url: ${items?.get(position)?.photo} ")

        val photoUrl = "https://my.bdjobs.com/photos"+items?.get(position)?.photo

        holder.profilePicIMGV.loadCircularImageFromUrl(photoUrl)

    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items?.size!!
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val nameTV = view.findViewById(R.id.nameTV) as TextView
    val emailTV = view.findViewById(R.id.emailTV) as TextView
    val profilePicIMGV = view.findViewById(R.id.profilePicIMGV) as ImageView
}