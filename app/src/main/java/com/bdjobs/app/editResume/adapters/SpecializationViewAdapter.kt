package com.bdjobs.app.editResume.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.utilities.show
import com.bdjobs.app.editResume.adapters.models.Skill
import com.bdjobs.app.editResume.callbacks.OtherInfo
import com.bdjobs.app.editResume.otherInfo.OtherInfoBaseActivity
import com.google.android.material.chip.Chip
import java.util.*
import java.util.Arrays.asList


class SpecializationViewAdapter(private val context: Activity, private val items: ArrayList<Skill?>?):RecyclerView.Adapter<SpecializationViewSkillViewHolder>() {


    private var otherInfo: OtherInfo? = null

    init {

        if ((context) is OtherInfoBaseActivity) {
            otherInfo = context
        }


    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecializationViewSkillViewHolder {
        return SpecializationViewSkillViewHolder(LayoutInflater.from(context).inflate(R.layout.specialization_view_list_item_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    override fun onBindViewHolder(holder: SpecializationViewSkillViewHolder, position: Int) {
        holder.experienceValueTV.text = items?.get(position)?.skillName
        holder.workExperienceTV.text = "skill - ${position+1}"
        /* holder.experienceInstructionTV.text = "কিভাবে '${items?.get(position)?.workExp}' কাজের দক্ষতাটি শিখেছেন?"*/
     /*   //Log.d("uhiuhiu", "workExp ${items?.get(position)?.workExp}")
        //Log.d("fdhbjh", "position $position")*/
       /* //Log.d("uhiuhiu", "skillBy ${items?.get(position)?.skillBy}")
        //Log.d("uhiuhiu", "NTVQF ${items?.get(position)?.ntvqfLevel}")*/


        val animalList = asList(items?.get(position)?.skillBy!!.replace(", ", ",").split(","))

        animalList.forEachIndexed { index, t ->

            //Log.d("uhiuhiu", "index $index t $t")
            for (s in t) {

                //Log.d("uhiuhiu", "s $s")

                when (s) {
                    "1" -> {
                        //Log.d("uhiuhiu", " in 1")
                        holder.filter_chip1.text = "Self"
                        holder.filter_chip1.show()
                    }
                    "2" -> {
                        //Log.d("uhiuhiu", " in 2")
                        holder.filter_chip3.text = "job"
                        holder.filter_chip3.show()

                    }
                    "3" -> {
                        //Log.d("uhiuhiu", " in 3")
                        holder.filter_chip2.text = "Educational"
                        holder.filter_chip2.show()

                    }
                    "4" -> {
                        //Log.d("uhiuhiu", " in 4")
                        holder.filter_chip4.text = "Professional Training"
                        holder.filter_chip4.show()
                    }
                    "5" -> {
                        //Log.d("uhiuhiu", " in 5")

                        holder.filter_chip5.show()
                        when (items.get(position)?.ntvqfLevel) {
                            "1" -> {
                                //Log.d("uhiuhiu", " in 1")
                                holder.filter_chip5.text = "NTVQF:Pre-Voc Level 1"
                            }
                            "2" -> {
                                //Log.d("uhiuhiu", " in 2")
                                holder.filter_chip5.text = "NTVQF:Pre-Voc Level 2"
                            }
                            "3" -> {
                                //Log.d("uhiuhiu", " in 3")
                                holder.filter_chip5.text = "NTVQF:NTVQF Level 1"
                            }
                            "4" -> {
                                //Log.d("uhiuhiu", " in 4")
                                holder.filter_chip5.text = "NTVQF:NTVQF Level 2"
                            }
                            "5" -> {
                                //Log.d("uhiuhiu", " in 5")


                                holder.filter_chip5.text = "NTVQF:NTVQF Level 3"

                            }
                            "6" -> {
                                //Log.d("uhiuhiu", " in 4")
                                holder.filter_chip5.text = "NTVQF:NTVQF Level 4"
                            }
                            "7" -> {
                                //Log.d("uhiuhiu", " in 5")
                                holder.filter_chip5.text = "NTVQF:NTVQF Level 5"
                            }
                            "8" -> {
                                //Log.d("uhiuhiu", " in 5")
                                holder.filter_chip5.text = "NTVQF:NTVQF Level 6"
                            }

                        }
                    }

                }

            }

        }






    }
}

class SpecializationViewSkillViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val experienceValueTV = view.findViewById(R.id.experienceValueTV) as TextView
    val experienceInstructionTV = view.findViewById(R.id.experienceInstructionTV) as TextView

    val workExperienceTV = view.findViewById(R.id.workExperienceTV) as TextView


    val filter_chip1 = view.findViewById(R.id.filter_chip1) as Chip
    val filter_chip2 = view.findViewById(R.id.filter_chip2) as Chip
    val filter_chip3 = view.findViewById(R.id.filter_chip3) as Chip
    val filter_chip4 = view.findViewById(R.id.filter_chip4) as Chip
    val filter_chip5 = view.findViewById(R.id.filter_chip5) as Chip

}