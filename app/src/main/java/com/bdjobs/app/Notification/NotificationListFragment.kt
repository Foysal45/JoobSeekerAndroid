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
import com.bdjobs.app.utilities.Constants
import com.bdjobs.app.utilities.hide
import com.bdjobs.app.utilities.show
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.databases.internal.Notification
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_notification_list.*
import kotlinx.android.synthetic.main.layout_no_data_found.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import timber.log.Timber


class NotificationListFragment : Fragment() {

    private lateinit var notificationCommunicator: NotificationCommunicatior
    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var bdjobsDB: BdjobsDB
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var notificationListAdapter: NotificationListAdapter
    private lateinit var notificationHelper: NotificationHelper


    var notificationList: List<Notification>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notification_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        notificationCommunicator = activity as NotificationCommunicatior
        bdjobsDB = BdjobsDB.getInstance(activity)
        bdjobsUserSession = BdjobsUserSession(activity)
        linearLayoutManager = LinearLayoutManager(activity)
        notificationHelper = NotificationHelper(activity)

        textView10.text = "You currently have no notifications."

        showDataFromDB()
    }

    private fun showDataFromDB() {
        doAsync {
            notificationList = bdjobsDB.notificationDao().getNotification()
            uiThread {
                try {

                    notificationListAdapter = NotificationListAdapter(activity, notificationList as MutableList<Notification>)
                    notificationsRV?.also {
                        it.setHasFixedSize(true)
                        it.itemAnimator = DefaultItemAnimator()
                        it.adapter = notificationListAdapter
                        it.layoutManager = linearLayoutManager
                        try {
                            it.scrollToPosition(notificationCommunicator.getPositionClicked())
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
//                                notificationListAdapter.removeItem(i)
                                softDeleteNotificationFromDB(notificationList!![i])
                            }
                        } catch (e: Exception) {
                        }
                    }

                } catch (e: Exception) {
                }

                if (notificationList?.size!! > 0) {
                    notificationNoDataLL?.hide()
                    notificationsRV?.show()
                } else {
                    notificationsRV?.hide()
                    notificationNoDataLL?.show()
                }
                try {
                    val simpleItemTouchCallback =
                            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

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
                                                Constants.NOTIFICATION_TYPE_INTERVIEW_INVITATION -> NotificationManagerCompat.from(activity).cancel(Constants.NOTIFICATION_INTERVIEW_INVITATTION)
                                                Constants.NOTIFICATION_TYPE_CV_VIEWED -> NotificationManagerCompat.from(activity).cancel(Constants.NOTIFICATION_CV_VIEWED)
                                            }
                                        } catch (e: Exception) {
                                        }
                                        notificationListAdapter.removeItem(position)
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
                                                        notificationListAdapter.restoreItem(position, notification)
                                                    } catch (e: Exception) {
                                                    }
                                                    try {
                                                        notificationsRV!!.scrollToPosition(position)
                                                    } catch (e: Exception) {
                                                    }
                                                    if (!notification.seen!!) {
                                                        bdjobsUserSession = BdjobsUserSession(activity)
                                                        bdjobsUserSession.updateNotificationCount(bdjobsUserSession.notificationCount!! + 1)
                                                    }
                                                    if (notificationListAdapter.itemCount == 0) {
                                                        notificationNoDataLL?.show()
                                                    } else {
                                                        notificationNoDataLL?.hide()
                                                    }
                                                }
                                            }

                                        }
                                        snackbar.setActionTextColor(ContextCompat.getColor(activity, R.color.undo))
                                        snackbar.show()

                                        if (notificationListAdapter.itemCount == 0) {
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
        }
    }


    private fun softDeleteNotificationFromDB(notification: Notification) {
        doAsync {
            bdjobsDB.notificationDao().deleteNotification(notification)
//            bdjobsDB.notificationDao().softDeleteNotification(notification.id!!)
            if (!notification.seen!!) {
                bdjobsUserSession = BdjobsUserSession(activity)
                bdjobsUserSession.updateNotificationCount(bdjobsUserSession.notificationCount!! - 1)
            }

            uiThread {

            }
        }
    }

    fun updateView(item: Notification) {
        notificationListAdapter.addItem(item)
        notificationListAdapter.notifyDataSetChanged()
    }

}
