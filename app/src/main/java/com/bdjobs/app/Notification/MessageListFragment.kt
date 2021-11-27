package com.bdjobs.app.Notification


import android.annotation.SuppressLint
import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.databases.internal.Notification
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_message_list.*
import kotlinx.android.synthetic.main.layout_no_data_found.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import timber.log.Timber


class MessageListFragment : Fragment() {

    private lateinit var notificationCommunicator: NotificationCommunicatior
    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var bdjobsDB: BdjobsDB
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: NotificationListAdapter

    var notificationList: MutableList<Notification>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message_list, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        notificationCommunicator = activity as NotificationCommunicatior
        bdjobsDB = BdjobsDB.getInstance(activity)
        linearLayoutManager = LinearLayoutManager(activity)

        textView10.text = "You currently have no messages."

        showDataFromDB()
    }

    private fun showDataFromDB() {
        doAsync {
            notificationList = bdjobsDB.notificationDao().getMessage() as? MutableList

            uiThread {

                adapter = NotificationListAdapter(activity, notificationList as MutableList<Notification>)
                notificationsRV?.also {
                    it.setHasFixedSize(true)
                    it.itemAnimator = DefaultItemAnimator()
                    it.adapter = adapter
                    it.layoutManager = linearLayoutManager
                    try {
                        it.scrollToPosition(notificationCommunicator.getPositionClickedMessage())
                    } catch (e: Exception) {
                    }
                }

                for (i in notificationList!!.indices) {
                    try {
                        val hashMap = Constants.getDateTimeAsAgo(notificationList!![i].arrivalTime)
                        val days = hashMap["days"]
                        val minutes = hashMap["minutes"]

                        Timber.d("Min: $minutes Days: $days")

                        if (days!=null && days>=7) {
                            Timber.d("Deleting notification")
//                            adapter.removeItem(i)
                            softDeleteNotificationFromDB(notificationList!![i])
                        }
                    } catch (e: Exception) {
                    }
                }

                if (notificationList?.size!! > 0) {
                    notificationNoDataLL?.hide()
                    notificationsRV?.show()
                } else {
                    notificationsRV?.hide()
                    notificationNoDataLL?.show()
                }

                swipeToDelete()
            }
        }
    }

    private fun swipeToDelete() {
        try {
            val simpleItemTouchCallback =
                object : ItemTouchHelper.SimpleCallback(
                    0,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                ) {

                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        return false
                    }

                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val position = viewHolder.adapterPosition


                        if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) {
                            val notification = notificationList!![position]

                            try {
                                when (notificationList!![position].type) {
                                    Constants.NOTIFICATION_TYPE_INTERVIEW_INVITATION -> NotificationManagerCompat.from(
                                        activity
                                    ).cancel(
                                        Constants.NOTIFICATION_INTERVIEW_INVITATTION
                                    )
                                    Constants.NOTIFICATION_TYPE_CV_VIEWED -> NotificationManagerCompat.from(
                                        activity
                                    ).cancel(
                                        Constants.NOTIFICATION_CV_VIEWED
                                    )
                                }
                            } catch (e: Exception) {
                            }
                            adapter.removeItem(position)
                            softDeleteNotificationFromDB(notification)
                            val snackbar = Snackbar.make(
                                parentCL,
                                " Notification removed!",
                                Snackbar.LENGTH_LONG
                            )
                            snackbar.setAction("UNDO") {
                                // undo is selected, restore the deleted item
                                doAsync {
                                    bdjobsDB.notificationDao().insertNotification(notification)
                                    uiThread {
                                        try {
                                            adapter.restoreItem(position, notification)
                                        } catch (e: Exception) {
                                        }
                                        try {
                                            notificationsRV!!.scrollToPosition(position)
                                        } catch (e: Exception) {
                                        }
                                        if (!notification.seen!!) {
                                            bdjobsUserSession = BdjobsUserSession(activity)
                                            bdjobsUserSession.updateMessageCount(
                                                bdjobsUserSession.messageCount!! + 1
                                            )
                                        }
                                        if (adapter.itemCount == 0) {
                                            notificationNoDataLL?.show()
                                        } else {
                                            notificationNoDataLL?.hide()
                                        }
                                    }
                                }

                            }
                            snackbar.setActionTextColor(
                                ContextCompat.getColor(
                                    activity,
                                    R.color.undo
                                )
                            )
                            snackbar.show()

                            if (adapter.itemCount == 0) {
                                notificationNoDataLL?.show()
                            } else {
                                notificationNoDataLL?.hide()
                            }
                        }
                    }
                }
            val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
            itemTouchHelper.attachToRecyclerView(notificationsRV)
        } catch (e: Exception) {
        }
    }


    private fun softDeleteNotificationFromDB(notification: Notification) {
        doAsync {
            bdjobsDB.notificationDao().deleteNotification(notification)
//            bdjobsDB.notificationDao().softDeleteNotification(notification.id!!)
            if (!notification.seen!!) {
                bdjobsUserSession = BdjobsUserSession(activity)
                bdjobsUserSession.updateMessageCount(bdjobsUserSession.messageCount!! - 1)
            }

            uiThread {

            }
        }
    }

    fun updateView(item: Notification) {
        adapter.addItem(item)
        adapter.notifyDataSetChanged()
    }

}


// {msgTitle=ভিডিও রিজিউমি (Video Resume),
// imgSrc=, msg=আপনি তিনটির কম প্রশ্নের উত্তর রেকর্ড করেছেন, নিয়োগকর্তাকে আপনার ভিডিও রিজিউমিটি দেখাতে হলে কমপক্ষে তিনটি প্রশ্নের উত্তর রেকর্ড করতে হবে। এখনই রেকর্ড করে ফেলুন ➜,
// pId=4361771,
// link=https://mybdjobs.bdjobs.com/mybdjobs/videoResume/video_resume_home.asp,
// type=pm,
// notificationId=69,
// activityNode=com.bdjobs.app.videoResume.VideoResumeActivity,
// LogoSrc= https://bdjobs.com/NotificationMessageimages/videoresumeslogo.png}

//{
// jobTitle=dfjk,
// imageLink=,
// companyName=Bdjobs Test Account - Az,
// pId=4361771,
// body=Bdjobs Test Account - Az had sent you a Live Interview schedule. Be sure to take part in the interview.,
// link=https://mybdjobs.bdjobs.com/mybdjobs/invite-interview-detail.asp?nstatus=1&Notification=6773237&id=954216,
// type=li,
// jobId=954216,
// title=Interview Invitation,
// notificationId=6773237,
// deleteType=
// }


//{
// jobTitle=Employer Viewed Resume,
// imageLink=,
// companyName=Bdjobs Test Account - Az1,
// pId=4361771,
// body=An employer has viewed your resume/ CV. View employer name.,
// link=https://mybdjobs.bdjobs.com/mybdjobs/resume_view.asp?Notification=28542990,
// type=cv,
// jobId=35450,
// title=Employer Viewed Resume,
// notificationId=28542990,
// deleteType=
// }