package com.bdjobs.app.InterviewInvitation

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ModelClasses.InvitationDetailModelsData
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.equalIgnoreCase
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton

class InterviewInvitationDetailsAdapter(private val context: Context, private val items: List<InvitationDetailModelsData>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_INTERVIEW = 1
        private const val TYPE_VIDEO_INTERVIEW = 2
    }

    private var lat = ""
    private var lan = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var viewHolder: RecyclerView.ViewHolder? = null
        var inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            TYPE_INTERVIEW -> {
                val view = inflater.inflate(R.layout.job_invitation_details_list, parent, false)
                viewHolder = InterviewInvitationViewHolder(view)
            }
            TYPE_VIDEO_INTERVIEW -> {
                val view = inflater.inflate(R.layout.job_video_invitaion_details_item, parent, false)
                viewHolder = VideoInterviewInvitationViewHolder(view)
            }
        }
        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return items?.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position].activity) {
            "sas" -> TYPE_VIDEO_INTERVIEW
            else -> TYPE_INTERVIEW
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {

            TYPE_INTERVIEW -> {
                val holder = viewHolder as InterviewInvitationViewHolder

                try {

                    lat = items[position].direction?.lat!!
                    lan = items[position].direction?.lan!!

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


                if (items.get(position).confimationStatus?.equalIgnoreCase("0")!!) {
                    holder.notifyRL.visibility = View.GONE
                }
                if (items.get(position).confimationStatus?.equalIgnoreCase("1")!!) {
                    holder.notifyRL.visibility = View.VISIBLE
                    holder.notifyMsgTV.text = "Confirmed"
                    holder.notifyMsgTV.setTextColor(Color.parseColor("#13A10E"))
                    holder.notifyDetailsTV.setTextColor(Color.parseColor("#393939"))
                    holder.notifyMsgTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_confirm_ic, 0, 0, 0)
                }
                if (items.get(position).confimationStatus?.equalIgnoreCase("1")!! && !TextUtils.isEmpty(items.get(position).previousScheduleDate)) {
                    holder.notifyRL.visibility = View.VISIBLE
                    holder.notifyMsgTV.text = "Reschedule Confirmed"
                    holder.notifyMsgTV.setTextColor(Color.parseColor("#13A10E"))
                    holder.notifyDetailsTV.setTextColor(Color.parseColor("#393939"))
                    holder.notifyMsgTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_confirm_ic, 0, 0, 0)
                }
                if (items.get(position).confimationStatus?.equalIgnoreCase("2")!!) {
                    holder.notifyRL.visibility = View.VISIBLE
                    holder.notifyMsgTV.text = "Not Confirmed"
                    holder.notifyMsgTV.setTextColor(Color.parseColor("#FF3144"))
                    holder.notifyDetailsTV.setTextColor(Color.parseColor("#393939"))
                    holder.notifyMsgTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_cancel_ic, 0, 0, 0)
                }
                if (items.get(position).confimationStatus?.equalIgnoreCase("2")!! && !TextUtils.isEmpty(items.get(position).previousScheduleDate)) {
                    holder.notifyRL.visibility = View.VISIBLE
                    holder.notifyMsgTV.text = "Reschedule Not Confirmed"
                    holder.notifyMsgTV.setTextColor(Color.parseColor("#FF3144"))
                    holder.notifyDetailsTV.setTextColor(Color.parseColor("#393939"))
                    holder.notifyMsgTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_cancel_ic, 0, 0, 0)
                }
                if (items.get(position).confimationStatus?.equalIgnoreCase("3")!!) {
                    holder.notifyRL.visibility = View.VISIBLE
                    holder.notifyMsgTV.text = "Reschedule Request"
                    holder.notifyMsgTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_resch_ic, 0, 0, 0)
                    holder.notifyMsgTV.setTextColor(Color.parseColor("#FF6F00"))
                    holder.notifyDetailsTV.setTextColor(Color.parseColor("#393939"))
                }
                if (items.get(position).confimationStatus?.equalIgnoreCase("4")!!) {
                    holder.notifyRL.visibility = View.VISIBLE
                    holder.notifyMsgTV.text = "Reschedule Rejected"
                    holder.notifyMsgTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_cancel_ic, 0, 0, 0)
                    holder.notifyMsgTV.setTextColor(Color.parseColor("#FF3144"))
                    holder.notifyDetailsTV.setTextColor(Color.parseColor("#393939"))
                }

                if (items.get(position).confimationStatus?.equalIgnoreCase("6")!!) {
                    holder.notifyRL.visibility = View.VISIBLE
                    holder.notifyMsgTV.text = "Expired"
                    holder.notifyMsgTV.setCompoundDrawablesWithIntrinsicBounds(R.drawable.job_inv_expired_ic, 0, 0, 0)
                    holder.notifyMsgTV.setTextColor(Color.parseColor("#FF3144"))
                    holder.notifyDetailsTV.setTextColor(Color.parseColor("#393939"))
                }
                if (items.get(position).confimationStatus?.equalIgnoreCase("5")!!) {
                    holder.notifyRL.visibility = View.GONE
                }

                if (items.get(position).activity?.equalIgnoreCase("4")!!) {
                    holder.rescheduleStattusTV.visibility = View.VISIBLE
                } else {
                    holder.rescheduleStattusTV.visibility = View.GONE
                }

                if (items.get(position).alertMessage!!.isNotEmpty()) {
                    holder.msgTV.show()
                    holder.msgTV.text = items.get(position).alertMessage
                } else {
                    holder.msgTV.hide()
                }

                if (items.get(position).contactNo!!.isNotEmpty()) {
                    holder.contactHeadingTV.show()
                    holder.phoneNumberTV.show()
                    holder.infoIMGV.show()
                    holder.phoneNumberTV.text = items.get(position).contactNo
                    holder.infoIMGV.setOnClickListener {
                        context.alert("For further assistance, you can call this number in office hours.") {
                            yesButton { dialog ->
                                dialog.dismiss()
                            }
                        }.show()
                    }
                    holder.phoneNumberTV.setOnClickListener {
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = Uri.parse("tel:${items.get(position).contactNo}")
                        context.startActivity(intent)
                    }

                } else {
                    holder.contactHeadingTV.hide()
                    holder.phoneNumberTV.hide()
                    holder.infoIMGV.hide()
                }
            }

            TYPE_VIDEO_INTERVIEW -> {
                val holder = viewHolder as VideoInterviewInvitationViewHolder
        }
    }
}


}

class InterviewInvitationViewHolder(view: View) : RecyclerView.ViewHolder(view) {

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
    val msgTV = view.findViewById<View>(R.id.msgTV) as TextView

    val contactHeadingTV = view.findViewById<View>(R.id.contactHeadingTV) as TextView
    val phoneNumberTV = view.findViewById<View>(R.id.phoneNumberTV) as TextView
    val infoIMGV = view.findViewById<View>(R.id.infoIMGV) as ImageView
}

class VideoInterviewInvitationViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val examTitleTV = view.findViewById<View>(R.id.video_invitation_heading_text) as TextView

    //    val rescheduleStattusTV = view.findViewById<View>(R.id.rescheduleStattusTV) as TextView
    val examMsgTV = view.findViewById<View>(R.id.video_invitation_body_text) as TextView

    val inviteDateTV = view.findViewById<View>(R.id.inviteDateTV) as TextView

    val examDateTV = view.findViewById<View>(R.id.date_text) as TextView
    val examTimeTV = view.findViewById<View>(R.id.total_time_text) as TextView
    val examQuestionsTV = view.findViewById<View>(R.id.questions_text) as TextView
    val examAttemptsTimeTV = view.findViewById<View>(R.id.attempts_text) as TextView

    //    val notifyMsgTV = view.findViewById<View>(R.id.notifyMsgTV) as TextView
//    val notifyDetailsTV = view.findViewById<View>(R.id.notifyDetailsTV) as TextView
//
//    val preSchDateTV = view.findViewById<View>(R.id.preSchDateTV) as TextView
//    val preSchTimeTV = view.findViewById<View>(R.id.preSchTimeTV) as TextView
//    val examVenueTV = view.findViewById<View>(R.id.examVenueTV) as TextView
//    val directionTV = view.findViewById<View>(R.id.directionTV) as TextView
//    val notifyDateTV = view.findViewById<View>(R.id.notifyDateTV) as TextView
//    val prevSchRL = view.findViewById<View>(R.id.prevSchRL) as RelativeLayout
//    val notifyRL = view.findViewById<View>(R.id.notifyRL) as RelativeLayout
//    val msgTV = view.findViewById<View>(R.id.msgTV) as TextView
//
//    val contactHeadingTV = view.findViewById<View>(R.id.contactHeadingTV) as TextView
//    val phoneNumberTV = view.findViewById<View>(R.id.phoneNumberTV) as TextView
    val infoIMGV = view.findViewById<View>(R.id.infoIMGV) as ImageView
}