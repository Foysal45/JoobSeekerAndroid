package com.bdjobs.app.InterviewInvitation

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.InvitationDetailModelsData
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.equalIgnoreCase

class InterviewInvitationDetailsAdapter(private val context: Context, private val items: List<InvitationDetailModelsData>):  RecyclerView.Adapter<InterviewInvitationViewHolder>() {

    private var lat =""
    private var lan =""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterviewInvitationViewHolder {
        return InterviewInvitationViewHolder(LayoutInflater.from(context).inflate(R.layout.job_invitation_details_list, parent, false))
    }

    override fun getItemCount(): Int {
      return items?.size
    }

    override fun onBindViewHolder(holder: InterviewInvitationViewHolder, position: Int) {
        try {

            lat = items[position].direction.lat
            lan = items[position].direction.lan

            if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lan)) {
                holder.directionTV.visibility = View.VISIBLE
            } else {
                holder.directionTV.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
            holder.directionTV.visibility = View.GONE
        }



        //holder.directionTV.setOnClickListener(View.OnClickListener { jobInvitationCommunicator.goToVenueDirection(data.get(position).getInvitationDate(), data.get(position).getExamTime(), data.get(position).getVenue(), lat, lan) })

        holder.examTitleTV.text = items[position].examTitle
        holder.examMsgTV.text = items[position].examMessage
        holder.inviteDateTV.text = items[position].invitationDate
        holder.examDateTV.text = items[position].examDate
        holder.examTimeTV.text = items[position].examTime
        holder.examVenueTV.text = items[position].venue


        if (TextUtils.isEmpty(items.get(position).previousScheduleDate)) {
            holder.prevSchRL.visibility = View.GONE
        } else {
            holder.prevSchRL.visibility = View.VISIBLE
            holder.preSchDateTV.text = items[position].previousScheduleDate
            holder.preSchTimeTV.text = items[position].previousScheduleTime
        }

        holder.notifyDetailsTV.text = items[position].confirmationMessage



        if (TextUtils.isEmpty(items.get(position).confirmationDate)) {
            holder.notifyDateTV.visibility = View.GONE


        } else {

            holder.notifyDateTV.visibility = View.VISIBLE
            holder.notifyDateTV.text = items.get(position).confirmationDate
        }


        if (items.get(position).confimationStatus.equalIgnoreCase("0")) {
            holder.notifyRL.visibility = View.GONE
        }
        if (items.get(position).confimationStatus.equalIgnoreCase("1")) {
            holder.notifyRL.visibility = View.VISIBLE
            holder.notifyMsgTV.text = "Confirmed"
            holder.notifyMsgTV.setTextColor(Color.parseColor("#13A10E"))
            holder.notifyDetailsTV.setTextColor(Color.parseColor("#155724"))
            holder.notifyMsgTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_confirm_ic, 0, 0, 0)
        }
        if (items.get(position).confimationStatus.equalIgnoreCase("1") && !TextUtils.isEmpty(items.get(position).previousScheduleDate)) {
            holder.notifyRL.visibility = View.VISIBLE
            holder.notifyMsgTV.text = "Reschedule Confirmed"
            holder.notifyMsgTV.setTextColor(Color.parseColor("#13A10E"))
            holder.notifyDetailsTV.setTextColor(Color.parseColor("#155724"))
            holder.notifyMsgTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_confirm_ic, 0, 0, 0)
        }
        if (items.get(position).confimationStatus.equalIgnoreCase("2")) {
            holder.notifyRL.visibility = View.VISIBLE
            holder.notifyMsgTV.text = "Not Confirmed"
            holder.notifyMsgTV.setTextColor(Color.parseColor("#B71C1C"))
            holder.notifyDetailsTV.setTextColor(Color.parseColor("#721C24"))
            holder.notifyMsgTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_cancel_ic, 0, 0, 0)
        }
        if (items.get(position).confimationStatus.equalIgnoreCase("2") && !TextUtils.isEmpty(items.get(position).previousScheduleDate)) {
            holder.notifyRL.visibility = View.VISIBLE
            holder.notifyMsgTV.text = "Reschedule Not Confirmed"
            holder.notifyMsgTV.setTextColor(Color.parseColor("#B71C1C"))
            holder.notifyDetailsTV.setTextColor(Color.parseColor("#721C24"))
            holder.notifyMsgTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_cancel_ic, 0, 0, 0)
        }
        if (items.get(position).confimationStatus.equalIgnoreCase("3")) {
            holder.notifyRL.visibility = View.VISIBLE
            holder.notifyMsgTV.text = "Reschedule Request"
            holder.notifyMsgTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_resch_ic, 0, 0, 0)
            holder.notifyMsgTV.setTextColor(Color.parseColor("#FF6F00"))
            holder.notifyDetailsTV.setTextColor(Color.parseColor("#856404"))
        }
        if (items.get(position).confimationStatus.equalIgnoreCase("4")) {
            holder.notifyRL.visibility = View.VISIBLE
            holder.notifyMsgTV.text = "Reschedule Rejected"
            holder.notifyMsgTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_cancel_ic, 0, 0, 0)
            holder.notifyMsgTV.setTextColor(Color.parseColor("#B71C1C"))
            holder.notifyDetailsTV.setTextColor(Color.parseColor("#721C24"))
        }

        if (items.get(position).confimationStatus.equalIgnoreCase("6")) {
            holder.notifyRL.visibility = View.VISIBLE
            holder.notifyMsgTV.text = "Expired"
            holder.notifyMsgTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_inv_expired_ic, 0, 0, 0)
            holder.notifyMsgTV.setTextColor(Color.parseColor("#B71C1C"))
            holder.notifyDetailsTV.setTextColor(Color.parseColor("#721C24"))
        }
        if (items.get(position).confimationStatus.equalIgnoreCase("5")) {
            holder.notifyRL.visibility = View.GONE
        }

        if (items.get(position).activity.equalIgnoreCase("4")) {
            holder.rescheduleStattusTV.visibility = View.VISIBLE
        } else {
            holder.rescheduleStattusTV.visibility = View.GONE
        }
    }
}

class  InterviewInvitationViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val examTitleTV = view.findViewById<View>(R.id.examTitleTV) as TextView
    val rescheduleStattusTV = view.findViewById<View>(R.id.rescheduleStattusTV) as TextView
    val examMsgTV = view.findViewById<View>(R.id.examMsgTV) as TextView
    val inviteDateTV = view.findViewById<View>(R.id.inviteDateTV) as TextView
    val examDateTV = view.findViewById<View>(R.id.examDateTV) as TextView
    val examTimeTV = view.findViewById<View>(R.id.examTimeTV) as TextView
    val notifyMsgTV = view.findViewById<View>(R.id.notifyMsgTV) as TextView
    val notifyDetailsTV = view.findViewById<View>(R.id.notifyDetailsTV) as TextView
    val preSchDateTV = view.findViewById<View>(R.id.preSchDateTV) as TextView
    val preSchTimeTV = view.findViewById<View>(R.id.preSchTimeTV) as TextView
    val examVenueTV = view.findViewById<View>(R.id.examVenueTV) as TextView
    val directionTV = view.findViewById<View>(R.id.directionTV) as TextView
    val notifyDateTV = view.findViewById<View>(R.id.notifyDateTV) as TextView
    val prevSchRL = view.findViewById<View>(R.id.prevSchRL) as RelativeLayout
    val notifyRL = view.findViewById<View>(R.id.notifyRL) as RelativeLayout
}