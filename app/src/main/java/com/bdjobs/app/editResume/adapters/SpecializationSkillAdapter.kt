package com.bdjobs.app.editResume.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.AddExpModel
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.editResume.callbacks.OtherInfo
import com.bdjobs.app.editResume.otherInfo.OtherInfoBaseActivity
import com.google.android.material.chip.Chip
import java.util.*

class SpecializationSkillAdapter(private val context: Activity, private val items: ArrayList<AddExpModel>?): RecyclerView.Adapter<SpecializationSkillViewHolder>() {
    private var otherInfo: OtherInfo? = null

    init {

        if ((context) is OtherInfoBaseActivity) {
            otherInfo = context
        }


    }

    override fun onBindViewHolder(holder: SpecializationSkillViewHolder, position: Int) {
        holder.experienceValueTV.text = items?.get(position)?.workExp
        holder.workExperienceTV.text = "skill - ${position+1}"
       /* holder.experienceInstructionTV.text = "কিভাবে '${items?.get(position)?.workExp}' কাজের দক্ষতাটি শিখেছেন?"*/
        Log.d("uhiuhiu", "workExp ${items?.get(position)?.workExp}")
        Log.d("fdhbjh", "position $position")
        Log.d("fdhbjh", "expSource ${items?.get(position)?.expSource}")
        Log.d("uhiuhiu", "NTVQF ${items?.get(position)?.NTVQF}")

        val list = items?.get(position)?.expSource!!
        list.forEachIndexed { index, s ->
            Log.d("expTest", "exp: $s")
            when (s) {
                "1" -> {
                    holder.filter_chip1.text = "Self"
                    holder.filter_chip1.show()
                }
                "2" -> {
                    holder.filter_chip2.text = "Educational"
                    holder.filter_chip2.show()
                }
                "3" -> {
                    holder.filter_chip3.text = "job"
                    holder.filter_chip3.show()
                }
                "4" -> {
                    holder.filter_chip4.text = "Professional Training"
                    holder.filter_chip4.show()
                }
                "5" -> {
                    holder.filter_chip5.text = "NTVQF:${items?.get(position)?.NTVQF}"
                    holder.filter_chip5.show()
                }
                "-1" -> {

                    holder.filter_chip1.hide()
                }
                "-2" -> {

                    holder.filter_chip2.hide()
                }
                "-3" -> {

                    holder.filter_chip3.hide()
                }
                "-4" -> {

                    holder.filter_chip4.hide()
                }
                "-5" -> {

                    holder.filter_chip5.hide()
                }
            }
        }



        holder.skillDeleteIcon.setOnClickListener {
            items?.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeRemoved(position, items?.size!!)

        }

        holder.skillEditIcon.setOnClickListener {

            otherInfo?.showEditDialog(items[position])

            /*  d("fdhbjh Passing Model ${items[position]?.expSource} ")*/

            otherInfo?.setItemClick(position)
            /* notifyItemChanged(position)*/


        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecializationSkillViewHolder {
        return SpecializationSkillViewHolder(LayoutInflater.from(context).inflate(R.layout.specialization_add_skill_list_item_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return items!!.size
    }





}

class SpecializationSkillViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val experienceValueTV = view.findViewById(R.id.experienceValueTV) as Chip
    val experienceInstructionTV = view.findViewById(R.id.experienceInstructionTV) as TextView
    val skillDeleteIcon = view.findViewById(R.id.skillDeleteIcon) as ImageView
    val skillEditIcon = view.findViewById(R.id.skillEditIcon) as ImageView
    val workExperienceTV = view.findViewById(R.id.workExperienceTV) as TextView


    val filter_chip1 = view.findViewById(R.id.filter_chip1) as TextView
    val filter_chip2 = view.findViewById(R.id.filter_chip2) as TextView
    val filter_chip3 = view.findViewById(R.id.filter_chip3) as TextView
    val filter_chip4 = view.findViewById(R.id.filter_chip4) as TextView
    val filter_chip5 = view.findViewById(R.id.filter_chip5) as TextView

}