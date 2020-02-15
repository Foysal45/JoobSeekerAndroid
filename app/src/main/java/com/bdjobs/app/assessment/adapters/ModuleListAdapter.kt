package com.bdjobs.app.assessment.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.openUrlInBrowser
import com.google.android.material.button.MaterialButton
import com.loopj.android.http.AsyncHttpClient.log


class ModuleListAdapter(private val context: Context, private val items : Array<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val dataStorage = DataStorage(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ModuleListViewHolder(LayoutInflater.from(context).inflate(R.layout.item_modules, parent, false))
    }


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ModuleListViewHolder

        viewHolder?.moduleName?.text = items.get(position)
        val sampleLink = "http://mybdjobs.bdjobs.com/mybdjobs/assessment/samples/" + dataStorage.getSampleLinkbyName(items.get(position))
        viewHolder?.downloadButton?.setOnClickListener{
            log.d("Sample link ",sampleLink )
            context.openUrlInBrowser(sampleLink)

        }

    }
}


class ModuleListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val moduleName = view?.findViewById(R.id.tv_module_name) as TextView
    val downloadButton = view?.findViewById(R.id.btn_download_sample) as MaterialButton

}

