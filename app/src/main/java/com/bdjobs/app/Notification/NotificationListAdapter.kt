package com.bdjobs.app.Notification

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.Html
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.databases.internal.Notification
import com.bdjobs.app.Employers.EmployersBaseActivity
import com.bdjobs.app.InterviewInvitation.InterviewInvitationBaseActivity
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.Notification.Models.CommonNotificationModel
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.NOTIFICATION_TYPE_BANNER_PROMOTIONAL_MESSAGE
import com.bdjobs.app.Utilities.Constants.Companion.NOTIFICATION_TYPE_CV_VIEWED
import com.bdjobs.app.Utilities.Constants.Companion.NOTIFICATION_TYPE_INTERVIEW_INVITATION
import com.bdjobs.app.Utilities.Constants.Companion.NOTIFICATION_TYPE_LIVE_INTERVIEW
import com.bdjobs.app.Utilities.Constants.Companion.NOTIFICATION_TYPE_MATCHED_JOB
import com.bdjobs.app.Utilities.Constants.Companion.NOTIFICATION_TYPE_PROMOTIONAL_MESSAGE
import com.bdjobs.app.Utilities.Constants.Companion.NOTIFICATION_TYPE_SMS
import com.bdjobs.app.Utilities.Constants.Companion.NOTIFICATION_TYPE_VIDEO_INTERVIEW
import com.bdjobs.app.Utilities.Constants.Companion.getDateTimeAsAgo
import com.bdjobs.app.liveInterview.LiveInterviewActivity
import com.bdjobs.app.videoInterview.VideoInterviewActivity
import com.bdjobs.app.videoResume.ResumeManagerActivity
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import timber.log.Timber
import java.util.*


class NotificationListAdapter(private val context: Context, private val items: MutableList<Notification>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_INTERVIEW_INVITATION = 1
        private const val TYPE_CV_VIEWED = 2
        private const val TYPE_MATCHED_JOB = 4
        private const val TYPE_PROMOTIONAL_MESSAGE = 3
        private const val TYPE_VIDEO_INTERVIEW = 5
        private const val TYPE_SMS = 6
        private const val TYPE_BANNER_PROMOTIONAL_MESSAGE = 7
        private const val TYPE_LIVE_INTERVIEW = 8
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

            TYPE_MATCHED_JOB -> {
                val view = inflater.inflate(R.layout.notification_item_matched_job, parent, false)
                viewHolder = MatchedJobViewHolder(view)
            }

            TYPE_PROMOTIONAL_MESSAGE -> {
                val view = inflater.inflate(R.layout.notification_item_msg, parent, false)
                viewHolder = PromotionalMessageViewHolder(view)
            }

            TYPE_BANNER_PROMOTIONAL_MESSAGE -> {
                val view = inflater.inflate(R.layout.notification_item_msg, parent, false)
                viewHolder = BannerPromotionalViewHolder(view)
            }

            TYPE_VIDEO_INTERVIEW -> {
                val view = inflater.inflate(R.layout.notification_item_video_interview, parent, false)
                viewHolder = VideoInterviewViewHolder(view)
            }

            TYPE_SMS -> {
                val view = inflater.inflate(R.layout.notification_item_sms, parent, false)
                viewHolder = VideoInterviewViewHolder(view)
            }

            TYPE_LIVE_INTERVIEW ->{
                val view = inflater.inflate(R.layout.notification_item_live_interview, parent, false)
                viewHolder = LiveInterviewViewHolder(view)
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
            NOTIFICATION_TYPE_MATCHED_JOB -> TYPE_MATCHED_JOB
            NOTIFICATION_TYPE_PROMOTIONAL_MESSAGE -> TYPE_PROMOTIONAL_MESSAGE
            NOTIFICATION_TYPE_VIDEO_INTERVIEW -> TYPE_VIDEO_INTERVIEW
            NOTIFICATION_TYPE_SMS -> TYPE_SMS
            NOTIFICATION_TYPE_BANNER_PROMOTIONAL_MESSAGE -> TYPE_BANNER_PROMOTIONAL_MESSAGE
            NOTIFICATION_TYPE_LIVE_INTERVIEW -> TYPE_LIVE_INTERVIEW
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

            TYPE_VIDEO_INTERVIEW -> {

                val videoInterviewViewHolder = holder as VideoInterviewViewHolder
                val hashMap = getDateTimeAsAgo(items[position].arrivalTime)

                try {
                    when {
                        hashMap.containsKey("seconds") -> videoInterviewViewHolder.notificationTimeTV.text = "just now"
                        hashMap.containsKey("minutes") -> {
                            if (hashMap["minutes"]!! > 1)
                                videoInterviewViewHolder.notificationTimeTV.text = "${hashMap["minutes"]} minutes ago"
                            else
                                videoInterviewViewHolder.notificationTimeTV.text = "${hashMap["minutes"]} minute ago"
                        }
                        hashMap.containsKey("hours") -> {
                            if (hashMap["hours"]!! > 1)
                                videoInterviewViewHolder.notificationTimeTV.text = "${hashMap["hours"]} hours ago"
                            else
                                videoInterviewViewHolder.notificationTimeTV.text = "${hashMap["hours"]} hour ago"
                        }
                        else -> {
                            if (hashMap["days"]!! > 1)
                                videoInterviewViewHolder.notificationTimeTV.text = "${hashMap["days"]} days ago"
                            else
                                videoInterviewViewHolder.notificationTimeTV.text = "${hashMap["days"]} day ago"
                        }
                    }
                } catch (e: Exception) {
                }

                videoInterviewViewHolder.notificationTitleTV.text = Html.fromHtml(items[position].body)

                try {
                    if (items[position].seen!!) {
                        videoInterviewViewHolder.notificationCL.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    } else
                        videoInterviewViewHolder.notificationCL.setBackgroundColor(Color.parseColor("#FFF2FA"))
                } catch (e: Exception) {
                }


                videoInterviewViewHolder?.notificationCV?.setOnClickListener {
                    NotificationManagerCompat.from(context).cancel(Constants.NOTIFICATION_INTERVIEW_INVITATTION)
                    notificationCommunicatior.positionClicked(position)
                    if (!items[position].seen!!) {
                        videoInterviewViewHolder.notificationCL.setBackgroundColor(Color.parseColor("#FFFFFF"))

                        doAsync {
                            bdjobsDB.notificationDao().updateNotification(Date(), true, items[position].notificationId!!, items[position].type!!)
                            val count = bdjobsDB.notificationDao().getNotificationCount()
                            bdjobsUserSession.updateNotificationCount(count)
                            uiThread {

                            }
                        }
                    }
//                    context?.startActivity<InterviewInvitationBaseActivity>(
//                            "from" to "videoInterviewNotificationList",
//                            "jobid" to items[position].serverId,
//                            "companyname" to items[position].companyName,
//                            "jobtitle" to items[position].jobTitle,
//                            "seen" to items[position].seen,
//                            "nid" to items[position].notificationId,
//                            "videoUrl" to items[position].link
//                    )
                    context.startActivity<VideoInterviewActivity>()
                }
            }

            TYPE_LIVE_INTERVIEW -> {

                val liveInterviewViewHolder = holder as LiveInterviewViewHolder
                val hashMap = getDateTimeAsAgo(items[position].arrivalTime)

                try {
                    when {
                        hashMap.containsKey("seconds") -> liveInterviewViewHolder.notificationTimeTV.text = "just now"
                        hashMap.containsKey("minutes") -> {
                            if (hashMap["minutes"]!! > 1)
                                liveInterviewViewHolder.notificationTimeTV.text = "${hashMap["minutes"]} minutes ago"
                            else
                                liveInterviewViewHolder.notificationTimeTV.text = "${hashMap["minutes"]} minute ago"
                        }
                        hashMap.containsKey("hours") -> {
                            if (hashMap["hours"]!! > 1)
                                liveInterviewViewHolder.notificationTimeTV.text = "${hashMap["hours"]} hours ago"
                            else
                                liveInterviewViewHolder.notificationTimeTV.text = "${hashMap["hours"]} hour ago"
                        }
                        else -> {
                            if (hashMap["days"]!! > 1)
                                liveInterviewViewHolder.notificationTimeTV.text = "${hashMap["days"]} days ago"
                            else
                                liveInterviewViewHolder.notificationTimeTV.text = "${hashMap["days"]} day ago"
                        }
                    }
                } catch (e: Exception) {
                }

                liveInterviewViewHolder.notificationTitleTV.text = Html.fromHtml(items[position].body)

                try {
                    if (items[position].seen!!) {
                        liveInterviewViewHolder.notificationCL.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    } else
                        liveInterviewViewHolder.notificationCL.setBackgroundColor(Color.parseColor("#FFF2FA"))
                } catch (e: Exception) {
                }


                liveInterviewViewHolder?.notificationCV?.setOnClickListener {
                    NotificationManagerCompat.from(context).cancel(Constants.NOTIFICATION_INTERVIEW_INVITATTION)
                    notificationCommunicatior.positionClicked(position)
                    if (!items[position].seen!!) {
                        liveInterviewViewHolder.notificationCL.setBackgroundColor(Color.parseColor("#FFFFFF"))

                        doAsync {
                            bdjobsDB.notificationDao().updateNotification(Date(), true, items[position].notificationId!!, items[position].type!!)
                            val count = bdjobsDB.notificationDao().getNotificationCount()
                            bdjobsUserSession.updateNotificationCount(count)
                            uiThread {

                            }
                        }
                    }
//                    context?.startActivity<InterviewInvitationBaseActivity>(
//                            "from" to "notificationList",
//                            "jobid" to items[position].serverId,
//                            "companyname" to items[position].companyName,
//                            "jobtitle" to items[position].jobTitle,
//                            "seen" to items[position].seen,
//                            "nid" to items[position].notificationId
//                    )

                    context.startActivity<LiveInterviewActivity>(
                            "from" to "notificationList",
                            "jobId" to items[position].serverId,
                            "jobTitle" to items[position].jobTitle
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
                } catch (e: Exception) {

                }


                cvViewedViewHolder?.notificationCV?.setOnClickListener {
                    cvViewedViewHolder.notificationCL.setBackgroundColor(Color.parseColor("#FFFFFF"))


                    try {
                        if (!items[position].seen!!) {
                            doAsync {
                                bdjobsDB.notificationDao().updateNotification(Date(), true, items[position].notificationId!!, items[position].type!!)
                                val count = bdjobsDB.notificationDao().getNotificationCount()
                                bdjobsUserSession.updateNotificationCount(count)
                            }
                        }
                    } catch (e: Exception) {
                        logException(e)
                    }

                    notificationCommunicatior.positionClicked(position)

                    context.startActivity<EmployersBaseActivity>(
                            "seen" to items[position].seen,
                            "from" to "notificationList"
                    )


                }
            }

            TYPE_SMS -> {
                val smsViewedViewHolder = holder as SMSViewHolder

                val str = Html.fromHtml(items[position].body)
                smsViewedViewHolder.notificationTitleTV.text = str
                val hashMap = getDateTimeAsAgo(items[position].arrivalTime)

                try {
                    when {
                        hashMap.containsKey("seconds") -> smsViewedViewHolder.notificationTimeTV.text = "just now"
                        hashMap.containsKey("minutes") -> {
                            if (hashMap["minutes"]!! > 1)
                                smsViewedViewHolder.notificationTimeTV.text = "${hashMap["minutes"]} minutes ago"
                            else
                                smsViewedViewHolder.notificationTimeTV.text = "${hashMap["minutes"]} minute ago"
                        }
                        hashMap.containsKey("hours") -> {
                            if (hashMap["hours"]!! > 1)
                                smsViewedViewHolder.notificationTimeTV.text = "${hashMap["hours"]} hours ago"
                            else
                                smsViewedViewHolder.notificationTimeTV.text = "${hashMap["hours"]} hour ago"
                        }
                        else -> {
                            if (hashMap["days"]!! > 1)
                                smsViewedViewHolder.notificationTimeTV.text = "${hashMap["days"]} days ago"
                            else
                                smsViewedViewHolder.notificationTimeTV.text = "${hashMap["days"]} day ago"
                        }
                    }
                } catch (e: Exception) {
                }

                try {
                    if (items[position].seen!!) {
                        smsViewedViewHolder?.notificationCL?.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    } else
                        smsViewedViewHolder?.notificationCL?.setBackgroundColor(Color.parseColor("#FFF2FA"))
                } catch (e: Exception) {

                }


                smsViewedViewHolder?.notificationButton?.setOnClickListener {
                    smsViewedViewHolder.notificationCL.setBackgroundColor(Color.parseColor("#FFFFFF"))


                    try {
                        if (!items[position].seen!!) {
                            doAsync {
                                bdjobsDB.notificationDao().updateNotification(Date(), true, items[position].notificationId!!, items[position].type!!)
                                val count = bdjobsDB.notificationDao().getNotificationCount()
                                bdjobsUserSession.updateNotificationCount(count)
                            }
                        }
                    } catch (e: Exception) {
                        logException(e)
                    }

                    notificationCommunicatior.positionClicked(position)

                    context.startActivity<EmployersBaseActivity>(
                            "seen" to items[position].seen,
                            "from" to "notificationList"
                    )


                }
            }

            TYPE_MATCHED_JOB -> {

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

                    if (!items[position].seen!!) {

                        doAsync {
                            bdjobsDB.notificationDao().updateNotification(Date(), true, items[position].notificationId!!, items[position].type!!)
                            val count = bdjobsDB.notificationDao().getNotificationCount()
                            bdjobsUserSession.updateNotificationCount(count)
                        }
                    }

                    context?.startActivity<JobBaseActivity>(
                            "from" to "notificationList",
                            "jobids" to jobids,
                            "lns" to lns,
                            "position" to 0,
                            "deadline" to deadline,
                            "seen" to items[position].seen
                    )

                    notificationCommunicatior.positionClicked(position)
                }
            }

            TYPE_PROMOTIONAL_MESSAGE -> {
                val promotionalMessageViewHolder = holder as PromotionalMessageViewHolder

                val hashMap = getDateTimeAsAgo(items[position].arrivalTime)

                var commonNotificationModel : CommonNotificationModel?=null

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

                try {
                    if (items[position].seen!!) {
                        promotionalMessageViewHolder.parentCL.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    } else
                        promotionalMessageViewHolder.parentCL.setBackgroundColor(Color.parseColor("#FFF2FA"))
                } catch (e: Exception) {
                }


                val title = Html.fromHtml(items[position].title)
                if (!items[position].title.isNullOrEmpty()) {
                    promotionalMessageViewHolder.messageTitle.show()
                    promotionalMessageViewHolder.messageTitle.text = title
                } else {
                    promotionalMessageViewHolder.messageTitle.hide()
                }

                val body = Html.fromHtml(items[position].body)
                if (!items[position].body.isNullOrEmpty()) {
                    promotionalMessageViewHolder.messageText.show()
                    promotionalMessageViewHolder.messageText.text = body
                } else {
                    promotionalMessageViewHolder.messageText.hide()
                }

                if (!items[position].payload.isNullOrEmpty()) {

                    Timber.d("Payload not null: ${items[position].payload}")

                    var intent:Intent ? =null
                    try {
                        commonNotificationModel  = Gson().fromJson(items[position].payload,CommonNotificationModel::class.java)
                        val className = Class.forName(commonNotificationModel.activityNode!!)
                        intent = Intent(context,className)

                    }
                    catch (e: Exception) {
                        Timber.d("internt exception")
                        if (!commonNotificationModel?.link.isNullOrEmpty()) {
                            try {
                                val formattedUrl = if (!commonNotificationModel?.link?.startsWith("http://") !!
                                        && !commonNotificationModel.link?.startsWith("https://")!!) {
                                    "http://${commonNotificationModel.link}"
                                } else commonNotificationModel.link
                                intent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl))
                            } catch (e: Exception) {
                            }
                        }
                    }

                    if (!items[position].link.isNullOrEmpty()) {
                        Timber.d("Link not null : ${items[position].link}")
                     //   promotionalMessageViewHolder?.messageButton?.show()
                        promotionalMessageViewHolder.itemView.setOnClickListener {

                            try {
                                if (!items[position].seen!!) {
                                    doAsync {
                                        bdjobsDB.notificationDao().updateNotification(Date(), true, items[position].notificationId!!, items[position].type!!)
                                        val count = bdjobsDB.notificationDao().getMessageCount()
                                        bdjobsUserSession.updateMessageCount(count)
                                    }

                                    promotionalMessageViewHolder.parentCL.setBackgroundColor(Color.parseColor("#FFFFFF"))
                                }
                            } catch (e: Exception) {
                                Timber.e("Catch in seen ${e.localizedMessage}")
                                logException(e)
                            }

                            try {
                                Timber.d("Intent: $intent")
                                if (intent!=null) context.startActivity(intent)
                                else context.launchUrl(items[position].link)
                            } catch (e: Exception) {
                                                            Timber.e("Catch in launch: ${e.localizedMessage}")}
                            notificationCommunicatior.positionClickedMessage(position)
                        }
                    } else {
                        promotionalMessageViewHolder?.messageButton?.hide()
                    }
                }
                else {
                    if (!items[position].link.isNullOrEmpty()) {
                     //   promotionalMessageViewHolder?.messageButton?.show()
                        promotionalMessageViewHolder?.messageImage?.setOnClickListener {

                            try {
                                if (!items[position].seen!!) {
                                    doAsync {
                                        bdjobsDB.notificationDao().updateNotification(Date(), true, items[position].notificationId!!, items[position].type!!)
                                        val count = bdjobsDB.notificationDao().getMessageCount()
                                        bdjobsUserSession.updateMessageCount(count)
                                    }
                                }
                            } catch (e: Exception) {
                                logException(e)
                            }

                            try {
                                context?.launchUrl(items[position].link)
                            } catch (e: Exception) {}
                            notificationCommunicatior.positionClickedMessage(position)
                        }
                    } else {
                        promotionalMessageViewHolder?.messageButton?.hide()
                    }
                }

                if (!commonNotificationModel?.LogoSrc.isNullOrEmpty()){
                    Timber.d("Logo not empty: ${commonNotificationModel?.LogoSrc}")
                    try {
                        Picasso.get().load(commonNotificationModel?.LogoSrc?.trim()).into(promotionalMessageViewHolder?.headerImage)
                    }catch (e: Exception) {
                    }
                }


                if (!items[position].imageLink.isNullOrEmpty()) {
                    Timber.d("imagee not empty: ${commonNotificationModel?.imageLink}")
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

            TYPE_BANNER_PROMOTIONAL_MESSAGE -> {
                val promotionalMessageViewHolder = holder as BannerPromotionalViewHolder

                promotionalMessageViewHolder.headerImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_video_resume_popup))

                promotionalMessageViewHolder.messageTime.hide()
                promotionalMessageViewHolder.timeImage.hide()


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

                promotionalMessageViewHolder.messageButton.setOnClickListener {
                    context.startActivity<ResumeManagerActivity>()
                }


                if (!items[position].imageLink.isNullOrEmpty()) {
                    promotionalMessageViewHolder?.card?.show()
                    promotionalMessageViewHolder?.messageImage.show()
                    try {
                        Picasso.get().load(R.drawable.banner_video_resume).into(promotionalMessageViewHolder?.messageImage)
                    } catch (e: Exception) {
                    }
                } else {
                    promotionalMessageViewHolder?.card?.show()
                    promotionalMessageViewHolder?.messageImage.show()
                    try {
                        Picasso.get().load(R.drawable.banner_video_resume).into(promotionalMessageViewHolder?.messageImage)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    promotionalMessageViewHolder?.card?.hide()
                    promotionalMessageViewHolder?.messageImage.hide()
                }

                promotionalMessageViewHolder.card.setOnClickListener {
                    context.startActivity<ResumeManagerActivity>()
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

class VideoInterviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val notificationTitleTV = view?.findViewById(R.id.notification_video_interview_text) as TextView
    val notificationTimeTV = view?.findViewById(R.id.notification_video_interview_time_text) as TextView

    //val notificationIMG = view?.findViewById(R.id.notification_interview_img) as ImageView
    val notificationCL = view?.findViewById(R.id.notification_video__cl) as ConstraintLayout
    val notificationCV = view?.findViewById(R.id.notification_interview_video_card_view) as CardView
    val notificationRecordButton = view?.findViewById(R.id.record_button) as MaterialButton
}

class LiveInterviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val notificationTitleTV = view?.findViewById(R.id.notification_live_interview_text) as TextView
    val notificationTimeTV = view?.findViewById(R.id.notification_live_interview_time_text) as TextView

    //val notificationIMG = view?.findViewById(R.id.notification_interview_img) as ImageView
    val notificationCL = view?.findViewById(R.id.notification_live_cl) as ConstraintLayout
    val notificationCV = view?.findViewById(R.id.notification_interview_live_card_view) as CardView
}

class CVViewedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val notificationTitleTV = view?.findViewById(R.id.notification_cv_viewed_text) as TextView
    val notificationTimeTV = view?.findViewById(R.id.notification_cv_viewed_time_text) as TextView
    val notificationIMG = view?.findViewById(R.id.notification_cv_viewed_img) as ImageView
    val notificationCL = view?.findViewById(R.id.notification_cv_viewed_cl) as ConstraintLayout
    val notificationCV = view?.findViewById(R.id.notification_cv_viewed_card_view) as CardView
}

class SMSViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val notificationTitleTV = view?.findViewById(R.id.notification_cv_viewed_text) as TextView
    val notificationTimeTV = view?.findViewById(R.id.notification_cv_viewed_time_text) as TextView
    val notificationIMG = view?.findViewById(R.id.notification_cv_viewed_img) as ImageView
    val notificationCL = view?.findViewById(R.id.notification_cv_viewed_cl) as ConstraintLayout
    val notificationCV = view?.findViewById(R.id.notification_cv_viewed_card_view) as CardView
    val notificationButton = view?.findViewById(R.id.btn_purchase_new_plan) as MaterialButton
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
    val headerImage = view?.findViewById(R.id.message_header_img) as ImageView
    val parentCL = view.findViewById<ConstraintLayout>(R.id.notification_cv_viewed_cl)

}

class BannerPromotionalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val messageTitle = view?.findViewById(R.id.message_title) as TextView
    val messageText = view?.findViewById(R.id.message_text) as TextView
    val messageButton = view?.findViewById(R.id.message_btn) as MaterialButton
    val messageTime = view?.findViewById(R.id.message_time_text) as TextView
    val messageImage = view?.findViewById(R.id.message_image) as ImageView
    val card = view?.findViewById(R.id.card) as CardView
    val headerImage = view?.findViewById(R.id.message_header_img) as ImageView
    val timeImage = view?.findViewById(R.id.notification_time_image) as ImageView

}