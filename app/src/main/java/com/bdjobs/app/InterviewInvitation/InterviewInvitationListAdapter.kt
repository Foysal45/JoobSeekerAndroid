package com.bdjobs.app.InterviewInvitation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.Databases.Internal.JobInvitation
import com.bdjobs.app.R


class InterviewInvitationListAdapter(private val context: Context, private val items: MutableList<JobInvitation>) : RecyclerView.Adapter<ViewHolder>() {


    private val interviewInvitationCommunicator = context as InterviewInvitationCommunicator

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_interview_list, parent, false))
    }

    override fun getItemCount(): Int {
        return items?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(items[position].seen=="1"){
            holder?.mainCL.background = context.getDrawable(R.drawable.ic_home_card)
        }else{
            holder?.mainCL.background = context.getDrawable(R.drawable.interview_invitatiion_card_unseen)
        }

        holder?.jobtitleTV?.text = items[position].jobTitle
        holder?.companyNameTV?.text = items[position].companyName
        holder?.appliedDateTV?.text = items[position].inviteDate?.split(" ")?.get(0)

        holder?.itemView?.setOnClickListener {
            /*interviewInvitationCommunicator.goToInvitationDetails(
                    jobID = items[position].jobId!!,
                    jobTitle = items[position].jobTitle!!,
                    companyName = items[position].companyName!!
            )*/
        }
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val jobtitleTV = view.findViewById(R.id.jobtitleTV) as TextView
    val companyNameTV = view.findViewById(R.id.companyNameTV) as TextView
    val appliedDateTV = view.findViewById(R.id.appliedDateTV) as TextView
    val mainCL = view.findViewById(R.id.mainCL) as ConstraintLayout

}