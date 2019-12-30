package com.bdjobs.app.editResume.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.d
import com.bdjobs.app.editResume.adapters.models.LanguageDataModel
import com.bdjobs.app.editResume.callbacks.OtherInfo

class LanguageAdapter(arr: java.util.ArrayList<LanguageDataModel>, val context: Context) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    private var call: OtherInfo = context as OtherInfo
    private val DURATION = 300
    private var itemList: MutableList<LanguageDataModel> = arr


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_language_list, parent, false)
        return LanguageAdapter.LanguageViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if (itemList == null) 0 else itemList.size
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val dModel = itemList[position]
        holder.langNameTV?.text = dModel.language
        holder.readingLevelTV?.text = dModel.reading
        holder.writingLevelTV?.text = dModel.writing
        holder.speakingLevelTV?.text = dModel.speaking


        holder.langEditIcon?.setOnClickListener {
            call.passLanguageData(dModel)
            d("From list : ${dModel.language}")
            call.goToEditInfo("editLanguage")
        }

        /* holder.imageViewExpand!!.setOnClickListener {
             //Log.d("click", "clicked success")
             toggleDetails(holder)
         }
         holder.moreActionDetails?.visibility = View.GONE*/
    }


    class LanguageViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {


        var langEditIcon: ImageView? = itemView!!.findViewById(R.id.languageEditIV)

        var langNameTV: TextView? = itemView?.findViewById(R.id.langNameTV)
        var readingLevelTV: TextView? = itemView?.findViewById(R.id.readingLevelTV)
        var writingLevelTV: TextView? = itemView?.findViewById(R.id.writingLevelTV)
        var speakingLevelTV: TextView? = itemView?.findViewById(R.id.speakingLevelTV)

    }


}


