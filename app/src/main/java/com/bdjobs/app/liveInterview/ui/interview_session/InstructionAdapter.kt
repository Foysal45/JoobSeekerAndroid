package com.bdjobs.app.liveInterview.ui.interview_session

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.liveInterview.data.models.Instructions
import kotlinx.android.synthetic.main.item_guideline.view.*

//
// Created by Soumik on 5/3/2021.
// piyal.developer@gmail.com
//

class InstructionAdapter(val context: Context,val instructions:List<Instructions>):RecyclerView.Adapter<InstructionAdapter.Holder>() {

    inner class Holder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val imageView : AppCompatImageView = itemView.img_guideline
        val textView: TextView = itemView.tv_guideline_text

        fun bind(guideline: Instructions) {
            imageView.setImageDrawable(AppCompatResources.getDrawable(context,guideline.image))
            textView.text = guideline.text

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return  Holder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_guideline, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return instructions.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(instructions[position])
    }
}