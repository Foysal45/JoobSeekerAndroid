package com.bdjobs.app.Notification

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.Html
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationManagerCompat
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.Notification
import com.bdjobs.app.Employers.EmployersBaseActivity
import com.bdjobs.app.InterviewInvitation.InterviewInvitationBaseActivity
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.NOTIFICATION_TYPE_CV_VIEWED
import com.bdjobs.app.Utilities.Constants.Companion.NOTIFICATION_TYPE_INTERVIEW_INVITATION
import com.bdjobs.app.Utilities.Constants.Companion.NOTIFICATION_TYPE_MATCHED_JOB
import com.bdjobs.app.Utilities.Constants.Companion.NOTIFICATION_TYPE_PROMOTIONAL_MESSAGE
import com.bdjobs.app.Utilities.Constants.Companion.getDateTimeAsAgo
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.util.*


class NotificationListAdapter(private val context: Context, private val items: MutableList<Notification>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_INTERVIEW_INVITATION = 1
        private const val TYPE_CV_VIEWED = 2
        private const val TYPE_MATCHED_JOB = 4
        private const val TYPE_PROMOTIONAL_MESSAGE = 3

    }

    private val notificationCommunicatior = context as NotificationCommunicatior
    var bdjobsDB: BdjobsDB
    var bdjobsUserSession: BdjobsUserSession
    var notificationHelper: NotificationHelper

    init {
        bdjobsDB = BdjobsDB.getInstance(context)
        bdjobsUserSession = BdjobsUserSession(context)
        notificationHelper = NotificationHelper(context)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        var inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            TYPE_INTERVIEW_INVITATION -> {
                val view = inflater.inflate(R.layout.notification_item_interview, parent, false)
                viewHolder = NotificationViewHolder(view)
            }
            TYPE_CV_VIEWED -> {
                val view = inflater.inflate(R.layout.notification_item_cv_viewed, parent, false)
                viewHolder = CVViewedViewHolder(view)
            }

            TYPE_MATCHED_JOB->{
                val view = inflater.inflate(R.layout.notification_item_matched_job, parent, false)
                viewHolder = MatchedJobViewHolder(view)
            }

            TYPE_PROMOTIONAL_MESSAGE -> {
                val view = inflater.inflate(R.layout.notification_item_msg, parent, false)
                viewHolder = PromotionalMessageViewHolder(view)
            }

        }
        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return items?.size
    }

    override fun getItemViewType(position: Int): Int {

        return when (items[position].type) {
            NOTIFICATION_TYPE_INTERVIEW_INVITATION -> TYPE_INTERVIEW_INVITATION
            NOTIFICATION_TYPE_CV_VIEWED -> TYPE_CV_VIEWED
            NOTIFICATION_TYPE_MATCHED_JOB-> TYPE_MATCHED_JOB
            NOTIFICATION_TYPE_PROMOTIONAL_MESSAGE -> TYPE_PROMOTIONAL_MESSAGE
            else -> TYPE_INTERVIEW_INVITATION
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val time = items[position].arrivalTime

        when (getItemViewType(position)) {

            TYPE_INTERVIEW_INVITATION -> {

                val notificationViewHolder = holder as NotificationViewHolder
                val hashMap = getDateTimeAsAgo(items[position].arrivalTime)

                try {
                    when {
                        hashMap.containsKey("seconds") -> notificationViewHolder.notificationTimeTV.text = "just now"
                        hashMap.containsKey("minutes") -> {
                            if (hashMap["minutes"]!! > 1)
                                notificationViewHolder.notificationTimeTV.text = "${hashMap["minutes"]} minutes ago"
                            else
                                notificationViewHolder.notificationTimeTV.text = "${hashMap["minutes"]} minute ago"
                        }
                        hashMap.containsKey("hours") -> {
                            if (hashMap["hours"]!! > 1)
                                notificationViewHolder.notificationTimeTV.text = "${hashMap["hours"]} hours ago"
                            else
                                notificationViewHolder.notificationTimeTV.text = "${hashMap["hours"]} hour ago"
                        }
                        else -> {
                            if (hashMap["days"]!! > 1)
                                notificationViewHolder.notificationTimeTV.text = "${hashMap["days"]} days ago"
                            else
                                notificationViewHolder.notificationTimeTV.text = "${hashMap["days"]} day ago"
                        }
                    }
                } catch (e: Exception) {
                }

                notificationViewHolder.notificationTitleTV.text = Html.fromHtml(items[position].body)

                try {
                    if (items[position].seen!!) {
                        notificationViewHolder.notificationCL.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    } else
                        notificationViewHolder.notificationCL.setBackgroundColor(Color.parseColor("#FFF2FA"))
                } catch (e: Exception) {
                }


                notificationViewHolder?.notificationCV?.setOnClickListener {
                    NotificationManagerCompat.from(context).cancel(Constants.NOTIFICATION_INTERVIEW_INVITATTION)
                    notificationCommunicatior.positionClicked(position)
                    if (!items[position].seen!!) {
                        notificationViewHolder.notificationCL.setBackgroundColor(Color.parseColor("#FFFFFF"))

                        doAsync {
                            bdjobsDB.notificationDao().updateNotification(Date(), true, items[position].notificationId!!, items[position].type!!)
                            val count = bdjobsDB.notificationDao().getNotificationCount()
                            bdjobsUserSession.updateNotificationCount(count)
                            uiThread {

                            }
                        }
                    }
                    context?.startActivity<InterviewInvitationBaseActivity>(
                            "from" to "notificationList",
                            "jobid" to items[position].serverId,
                            "companyname" to items[position].companyName,
                            "jobtitle" to items[position].jobTitle,
                            "seen" to items[position].seen,
                            "nid" to items[position].notificationId

                    )
                }
            }

            TYPE_CV_VIEWED -> {

                val cvViewedViewHolder = holder as CVViewedViewHolder

                val str = Html.fromHtml(items[position].body)
                cvViewedViewHolder.notificationTitleTV.text = str
                val hashMap = getDateTimeAsAgo(items[position].arrivalTime)

                try {
                    when {
                        hashMap.containsKey("seconds") -> cvViewedViewHolder.notificationTimeTV.text = "just now"
                        hashMap.containsKey("minutes") -> {
                            if (hashMap["minutes"]!! > 1)
                                cvViewedViewHolder.notificationTimeTV.text = "${hashMap["minutes"]} minutes ago"
                            else
                                cvViewedViewHolder.notificationTimeTV.text = "${hashMap["minutes"]} minute ago"
                        }
                        hashMap.containsKey("hours") -> {
                            if (hashMap["hours"]!! > 1)
                                cvViewedViewHolder.notificationTimeTV.text = "${hashMap["hours"]} hours ago"
                            else
                                cvViewedViewHolder.notificationTimeTV.text = "${hashMap["hours"]} hour ago"
                        }
                        else -> {
                            if (hashMap["days"]!! > 1)
                                cvViewedViewHolder.notificationTimeTV.text = "${hashMap["days"]} days ago"
                            else
                                cvViewedViewHolder.notificationTimeTV.text = "${hashMap["days"]} day ago"
                        }
                    }
                } catch (e: Exception) {
                }

                try {
                    if (items[position].seen!!) {
                        cvViewedViewHolder?.notificationCL?.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    } else
                        cvViewedViewHolder?.notificationCL?.setBackgroundColor(Color.parseColor("#FFF2FA"))
                } catch (e : Exception){

                }


                cvViewedViewHolder?.notificationCV?.setOnClickListener {
                    cvViewedViewHolder.notificationCL.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    val intent = Intent(context.applicationContext, EmployersBaseActivity::class.java)
                    intent.putExtra("from", "notificationList")

                    try {
                        if (!items[position].seen!!) {
                            doAsync {
                                bdjobsDB.notificationDao().updateNotification(Date(), true, items[position].notificationId!!, items[position].type!!)
                                val count = bdjobsDB.notificationDao().getNotificationCount()
                                bdjobsUserSession.updateNotificationCount(count)
                            }
                        }
                    } catch (e : Exception){
                        logException(e)
                    }
                    context?.startActivity(intent)
                    notificationCommunicatior.positionClicked(position)
                }
            }

            TYPE_MATCHED_JOB->{

                val matchedJobViewHolder = holder as MatchedJobViewHolder

                val str = Html.fromHtml(items[position].body)
                matchedJobViewHolder.notificationTitleTV.text = str
                val hashMap = getDateTimeAsAgo(items[position].arrivalTime)

                try {
                    when {
                        hashMap.containsKey("seconds") -> matchedJobViewHolder.notificationTimeTV.text = "just now"
                        hashMap.containsKey("minutes") -> {
                            if (hashMap["minutes"]!! > 1)
                                matchedJobViewHolder.notificationTimeTV.text = "${hashMap["minutes"]} minutes ago"
                            else
                                matchedJobViewHolder.notificationTimeTV.text = "${hashMap["minutes"]} minute ago"
                        }
                        hashMap.containsKey("hours") -> {
                            if (hashMap["hours"]!! > 1)
                                matchedJobViewHolder.notificationTimeTV.text = "${hashMap["hours"]} hours ago"
                            else
                                matchedJobViewHolder.notificationTimeTV.text = "${hashMap["hours"]} hour ago"
                        }
                        else -> {
                            if (hashMap["days"]!! > 1)
                                matchedJobViewHolder.notificationTimeTV.text = "${hashMap["days"]} days ago"
                            else
                                matchedJobViewHolder.notificationTimeTV.text = "${hashMap["days"]} day ago"
                        }
                    }
                } catch (e: Exception) {
                }

                if (items[position].seen!!) {
                    matchedJobViewHolder.notificationCL.setBackgroundColor(Color.parseColor("#FFFFFF"))
                } else
                    matchedJobViewHolder.notificationCL.setBackgroundColor(Color.parseColor("#FFF2FA"))


                matchedJobViewHolder?.notificationCV?.setOnClickListener {

                    matchedJobViewHolder.notificationCL.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    val jobids = ArrayList<String>()
                    val lns = ArrayList<String>()
                    val deadline = ArrayList<String>()
                    deadline.add(items[position].deadline!!)
                    jobids.add(items[position].serverId!!)
                    lns.add(items[position].lanType!!)
                    val intent = Intent(context.applicationContext, JobBaseActivity::class.java)?.apply {
                        putExtra("from", "notificationList")
                        putExtra("jobids", jobids)
                        putExtra("lns", lns)
                        putExtra("position", 0)
                        putExtra("deadline", deadline)
                    }

                    if (!items[position].seen!!) {
                        doAsync {
                            bdjobsDB.notificationDao().updateNotification(Date(), true, items[position].notificationId!!, items[position].type!!)
                            val count = bdjobsDB.notificationDao().getNotificationCount()
                            bdjobsUserSession.updateNotificationCount(count)
                        }
                    }
                    context?.startActivity(intent)
                    notificationCommunicatior.positionClicked(position)
                }
            }

            TYPE_PROMOTIONAL_MESSAGE -> {
                val promotionalMessageViewHolder = holder as PromotionalMessageViewHolder

                val hashMap = getDateTimeAsAgo(items[position].arrivalTime)


                try {
                    when {
                        hashMap.containsKey("seconds") -> promotionalMessageViewHolder.messageTime.text = "just now"
                        hashMap.containsKey("minutes") -> {
                            if (hashMap["minutes"]!! > 1)
                                promotionalMessageViewHolder.messageTime.text = "${hashMap["minutes"]} minutes ago"
                            else
                                promotionalMessageViewHolder.messageTime.text = "${hashMap["minutes"]} minute ago"
                        }
                        hashMap.containsKey("hours") -> {
                            if (hashMap["hours"]!! > 1)
                                promotionalMessageViewHolder.messageTime.text = "${hashMap["hours"]} hours ago"
                            else
                                promotionalMessageViewHolder.messageTime.text = "${hashMap["hours"]} hour ago"
                        }
                        else -> {
                            if (hashMap["days"]!! > 1)
                                promotionalMessageViewHolder.messageTime.text = "${hashMap["days"]} days ago"
                            else
                                promotionalMessageViewHolder.messageTime.text = "${hashMap["days"]} day ago"
                        }
                    }
                } catch (e: Exception) {
                }


                val title = Html.fromHtml(items[position].title)
                if (!items[position].title.isNullOrEmpty()) {
                    promotionalMessageViewHolder.messageTitle?.show()
                    promotionalMessageViewHolder.messageTitle?.text = title
                } else {
                    promotionalMessageViewHolder.messageTitle?.hide()
                }

                val body = Html.fromHtml(items[position].body)
                if (!items[position].body.isNullOrEmpty()) {
                    promotionalMessageViewHolder.messageText?.show()
                    promotionalMessageViewHolder.messageText?.text = body
                } else {
                    promotionalMessageViewHolder.messageText?.hide()
                }

                if (!items[position].link.isNullOrEmpty()) {
                    promotionalMessageViewHolder?.messageButton?.show()
                    promotionalMessageViewHolder?.messageButton?.onClick {
                        try {
                            context?.openUrlInBrowser(items[position].link)
                        } catch (e: Exception) {
                        }
                        notificationCommunicatior.positionClickedMessage(position)
                    }
                } else {
                    promotionalMessageViewHolder?.messageButton?.hide()
                }

                if (!items[position].imageLink.isNullOrEmpty()) {
                    promotionalMessageViewHolder?.card?.show()
                    promotionalMessageViewHolder?.messageImage.show()
                    try {
                        Picasso.get().load(items[position].imageLink).into(promotionalMessageViewHolder?.messageImage)
                    } catch (e: Exception) {
                    }
                } else {
                    promotionalMessageViewHolder?.card?.hide()
                    promotionalMessageViewHolder?.messageImage.hide()
                }
            }
        }

    }

    fun removeItem(position: Int) {
        items?.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items?.size)
    }

    fun restoreItem(position: Int, notification: Notification) {
        items?.add(position, notification)
        notifyItemInserted(position)
    }

    fun addItem(notification: Notification) {
        items?.add(0, notification)
        notifyItemInserted(0)
    }

}

class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val notificationTitleTV = view?.findViewById(R.id.notification_interview_text) as TextView
    val notificationTimeTV = view?.findViewById(R.id.notification_interview_time_text) as TextView
    val notificationIMG = view?.findViewById(R.id.notification_interview_img) as ImageView
    val notificationCL = view?.findViewById(R.id.notification_cl) as ConstraintLayout
    val notificationCV = view?.findViewById(R.id.notification_interview_card_view) as CardView
}

class CVViewedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val notificationTitleTV = view?.findViewById(R.id.notification_cv_viewed_text) as TextView
    val notificationTimeTV = view?.findViewById(R.id.notification_cv_viewed_time_text) as TextView
    val notificationIMG = view?.findViewById(R.id.notification_cv_viewed_img) as ImageView
    val notificationCL = view?.findViewById(R.id.notification_cv_viewed_cl) as ConstraintLayout
    val notificationCV = view?.findViewById(R.id.notification_cv_viewed_card_view) as CardView
}

class MatchedJobViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val notificationTitleTV = view?.findViewById(R.id.notification_matched_job_text) as TextView
    val notificationTimeTV = view?.findViewById(R.id.notification_matched_job_time_text) as TextView
    val notificationIMG = view?.findViewById(R.id.notification_matched_job_img) as ImageView
    val notificationCL = view?.findViewById(R.id.notification_matched_job_cl) as ConstraintLayout
    val notificationCV = view?.findViewById(R.id.notification_matched_job_card_view) as CardView
}

class PromotionalMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val messageTitle = view?.findViewById(R.id.message_title) as TextView
    val messageText = view?.findViewById(R.id.message_text) as TextView
    val messageButton = view?.findViewById(R.id.message_btn) as MaterialButton
    val messageTime = view?.findViewById(R.id.message_time_text) as TextView
    val messageImage = view?.findViewById(R.id.message_image) as ImageView
    val card = view?.findViewById(R.id.card) as CardView

}