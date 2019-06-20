package com.bdjobs.app.editResume.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.d
import com.bdjobs.app.editResume.adapters.models.Skill
import com.bdjobs.app.editResume.callbacks.OtherInfo
import com.bdjobs.app.editResume.otherInfo.OtherInfoBaseActivity

class SpecializationSkillAdapter(private val context: Activity, private val items: ArrayList<Skill?>): RecyclerView.Adapter<SpecializationSkillViewHolder>() {
    private var otherInfo: OtherInfo? = null

    init {

        if ((context) is OtherInfoBaseActivity) {
            otherInfo = context
        }


    }

    override fun onBindViewHolder(holder: SpecializationSkillViewHolder, position: Int) {
        holder.experienceValueTV.text = items?.get(position)?.skillName
        holder.workExperienceTV.text = "skill - ${position+1}"
       /* holder.experienceInstructionTV.text = "কিভাবে '${items?.get(position)?.workExp}' কাজের দক্ষতাটি শিখেছেন?"*/
        Log.d("fdhbjh", "workExp ${items?.get(position)?.skillName}")
        Log.d("fdhbjh", "position $position")
        Log.d("fdhbjh", "expSource ${items?.get(position)?.skillBy}")
        Log.d("fdhbjh", "NTVQF ${items?.get(position)?.ntvqfLevel}")

        var firstText =""
        var secondText =""
        var thirdText =""
        var fourthText =""
        var fifthText =""

        val list = items?.get(position)?.skillBy!!
        list.forEachIndexed { index, s ->
            Log.d("expTest", "exp: $s")
            when (s.toString()) {
                "1" -> {
                    firstText = "Self,"

                }
                "2" -> {
                    secondText = "Educational,"

                }
                "3" -> {
                    thirdText = "Job,"

                }
                "4" -> {
                    fourthText = "Professional Training,"

                }
                "5" -> {

                   /* fifthText = "NTVQF:${items?.get(position)?.ntvqfLevel}"*/

                   fifthText = getNtvqf(items[position]?.ntvqfLevel.toString())

                    d("fifthText :  $fifthText")

                }

            }
        }

        var finalText = ""

        if (firstText.isNotEmpty()){

            finalText += firstText
        }


        if (secondText.isNotEmpty()){
            finalText += secondText

        }

        if (thirdText.isNotEmpty()){
            finalText += thirdText

        }

        if (fourthText.isNotEmpty()){
            finalText += fourthText

        }

        if (fifthText.isNotEmpty()){
            finalText += fifthText

        }

        val showingText = finalText.removeSuffix(",")

        holder.skillByTV.text = showingText
        holder.skillDeleteIcon.setOnClickListener {
          /*  items?.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeRemoved(position, items?.size!!)*/
            d("deleteItemId: in adapter position   $position")
            d("deleteItemId: in adapter   ${items[position]!!.sId.toString()}")
            otherInfo?.deleteSpecialization(items[position]!!.sId.toString())


        }

        holder.skillEditIcon.setOnClickListener {

          otherInfo?.showEditDialog(items[position])
          d("fdhbjh Passing Model ${items[position]?.skillName} ")

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



    private fun getNtvqf(item: String): String {

        var ntvqflevel = ""

        when (item) {
            "1" -> {


                ntvqflevel = "Pre-Voc Level 1"
            }
            "2" -> {

                ntvqflevel = "Pre-Voc Level 2"

            }
            "3" -> {

                ntvqflevel = "NTVQF Level 1"

            }
            "4" -> {

                ntvqflevel = "NTVQF Level 2"
            }
            "5" -> {
                ntvqflevel = "NTVQF Level 3"

            }
            "6" -> {

                ntvqflevel = "NTVQF Level 4"
            }
            "7" -> {
                ntvqflevel = "NTVQF Level 5"

            }
            "8" -> {
                ntvqflevel = "NTVQF Level 6"

            }

            /*val dataItem = AddExpModel(item.skillName,skillArrayList,)*/
        }

        return ntvqflevel
    }


}

class SpecializationSkillViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val experienceValueTV = view.findViewById(R.id.experienceValueTV) as TextView
    val experienceInstructionTV = view.findViewById(R.id.experienceInstructionTV) as TextView
    val skillDeleteIcon = view.findViewById(R.id.skillDeleteIcon) as ImageView
    val skillEditIcon = view.findViewById(R.id.skillEditIcon) as ImageView
    val workExperienceTV = view.findViewById(R.id.workExperienceTV) as TextView


    val skillByTV = view.findViewById(R.id.skillByTV) as TextView


}