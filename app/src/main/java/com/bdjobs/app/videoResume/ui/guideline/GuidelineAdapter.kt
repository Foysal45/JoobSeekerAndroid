package com.bdjobs.app.videoResume.ui.guideline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import kotlinx.android.synthetic.main.layout_item_video_resume_guideline.view.*


class GuidelineAdapter(var guidelines: List<String>) : RecyclerView.Adapter<GuidelineAdapter.GuidelineViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuidelineViewHolder = GuidelineViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.layout_item_video_resume_guideline, parent, false)
    )

    override fun getItemCount(): Int = guidelines.size

    override fun onBindViewHolder(holder: GuidelineViewHolder, position: Int) {
        holder.bind(guidelines[position])
    }

    inner class GuidelineViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val textView: TextView = view.tv

        fun bind(guideline: String) {
            textView.text = guideline
        }
    }

}