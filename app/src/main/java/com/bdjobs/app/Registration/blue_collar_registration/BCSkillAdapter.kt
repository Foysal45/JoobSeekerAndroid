package com.bdjobs.app.Registration.blue_collar_registration


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
import com.bdjobs.app.Registration.RegistrationBaseActivity
import com.bdjobs.app.Registration.RegistrationCommunicator
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.google.android.material.chip.Chip
import java.util.*


class BCSkillAdapter(private val context: Activity, private val items: ArrayList<AddExpModel>?) :
        RecyclerView.Adapter<SkillViewHolder>() {


    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {

        holder.experienceValueTV.text = items?.get(position)?.workExp
        holder.experienceInstructionTV.text = "কিভাবে '${items?.get(position)?.workExp}' কাজের দক্ষতাটি শিখেছেন?"
        //Log.d("uhiuhiu", "workExp ${items?.get(position)?.workExp}")
        //Log.d("fdhbjh", "position $position")
        //Log.d("fdhbjh", "expSource ${items?.get(position)?.expSource}")
        //Log.d("uhiuhiu", "NTVQF ${items?.get(position)?.NTVQF}")

        val list = items?.get(position)?.expSource!!
        list.forEachIndexed { index, s ->
            //Log.d("expTest", "exp: $s")
            when (s) {
                "1" -> {
                    holder.filter_chip1.text = "নিজেই"
                    holder.filter_chip1.show()
                }
                "2" -> {
                    holder.filter_chip2.text = "শিক্ষা ক্ষেত্রে"
                    holder.filter_chip2.show()
                }
                "3" -> {
                    holder.filter_chip3.text = "চাকরিতে"
                    holder.filter_chip3.show()
                }
                "4" -> {
                    holder.filter_chip4.text = "ট্রেনিং -এ"
                    holder.filter_chip4.show()
                }
                "5" -> {
                    holder.filter_chip5.text = "NTVQF:${items.get(position).NTVQF}"
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
            items.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeRemoved(position, items.size)

        }

        holder.skillEditIcon.setOnClickListener {

            registrationCommunicator?.showEditDialog(items[position])

            /*  d("fdhbjh Passing Model ${items[position]?.expSource} ")*/

            registrationCommunicator?.setItemClick(position)
            /* notifyItemChanged(position)*/


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        return SkillViewHolder(LayoutInflater.from(context).inflate(R.layout.add_skill_list_item_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return items!!.size
    }


    private var registrationCommunicator: RegistrationCommunicator? = null

    init {

        if ((context) is RegistrationBaseActivity) {
            registrationCommunicator = context
        }


    }


}


class SkillViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val experienceValueTV = view.findViewById(R.id.experienceValueTV) as TextView
    val experienceInstructionTV = view.findViewById(R.id.experienceInstructionTV) as TextView
    val skillDeleteIcon = view.findViewById(R.id.skillDeleteIcon) as ImageView
    val skillEditIcon = view.findViewById(R.id.skillEditIcon) as ImageView


    val filter_chip1 = view.findViewById(R.id.filter_chip1) as Chip
    val filter_chip2 = view.findViewById(R.id.filter_chip2) as Chip
    val filter_chip3 = view.findViewById(R.id.filter_chip3) as Chip
    val filter_chip4 = view.findViewById(R.id.filter_chip4) as Chip
    val filter_chip5 = view.findViewById(R.id.filter_chip5) as Chip

}

