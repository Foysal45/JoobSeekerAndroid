package com.bdjobs.app.videoInterview.ui.guidelines

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.videoInterview.data.models.Guideline
import kotlinx.android.synthetic.main.item_guideline.view.*

class GuidelineAdapter(val context: Context, var guidelines: List<Guideline>) : RecyclerView.Adapter<GuidelineAdapter.GuidelineViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuidelineViewHolder = GuidelineViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_guideline, parent, false)
    )

    override fun getItemCount(): Int = guidelines.size

    override fun onBindViewHolder(holder: GuidelineViewHolder, position: Int) {
        holder.bind(guidelines[position])
    }

    inner class GuidelineViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imageView : AppCompatImageView = view.img_guideline
        val textView: TextView = view.tv_guideline_text

        fun bind(guideline: Guideline) {
            imageView.setImageDrawable(AppCompatResources.getDrawable(context,guideline.image))
            textView.text = guideline.text

        }
    }

}