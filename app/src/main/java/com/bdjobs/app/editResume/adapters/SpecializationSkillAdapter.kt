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

        var firstText =""
        var secondText =""
        var thirdText =""
        var fourthText =""
        var fifthText =""


        list.forEachIndexed { index, s ->
            Log.d("expTest", "exp: $s")
            when (s) {
                "1" -> {
                   firstText = "Self"

                }
                "2" -> {
                    secondText = "Educational"

                }
                "3" -> {
                   thirdText = "job"

                }
                "4" -> {
                    fourthText = "Professional Training"

                }
                "5" -> {
                    fifthText = "NTVQF:${items?.get(position)?.NTVQF}"

                }

                "-1" -> {
                   firstText = ""

                }
                "-2" -> {
                    secondText = ""

                }
                "-3" -> {
                   thirdText = ""

                }
                "-4" -> {
                    fourthText = ""

                }
                "-5" -> {
                    fifthText = ""

                }

            }
        }

        holder.skillByTV.text = "$firstText , $secondText , $thirdText , $fourthText , $fifthText"

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


    val skillByTV = view.findViewById(R.id.skillByTV) as TextView


}