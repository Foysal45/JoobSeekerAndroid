package com.bdjobs.app.Notification

import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.BroadCastReceivers.BackgroundJobBroadcastReceiver
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.databases.internal.Notification
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_notification_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class NotificationListFragment : Fragment() {
//    override fun onUpdateNotification() {
//        //Log.d("rakib", "came in noti list")
//        doAsync {
//            val noti = bdjobsDB.notificationDao().getSingleNotification()
//            //Log.d("rakib", "${noti.type + noti.id}")
//            uiThread {
//                notificationListAdapter.addItem(noti)
//                notificationListAdapter.notifyDataSetChanged()
//            }
//        }
//
//    }


    private lateinit var notificationCommunicator: NotificationCommunicatior
    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var bdjobsDB: BdjobsDB
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var backgroundJobBroadcastReceiver: BackgroundJobBroadcastReceiver
    private lateinit var notificationListAdapter: NotificationListAdapter
    private lateinit var notificationHelper: NotificationHelper
    //private val intentFilter = IntentFilter(Constants.BROADCAST_DATABASE_UPDATE_JOB)


    var notificationList: List<Notification>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notification_list, container, false)
    }

    override fun onResume() {
        super.onResume()
        notificationCommunicator = activity as NotificationCommunicatior
        bdjobsDB = BdjobsDB.getInstance(activity)
        bdjobsUserSession = BdjobsUserSession(activity)
        linearLayoutManager = LinearLayoutManager(activity)
        notificationHelper = NotificationHelper(activity)
        //backgroundJobBroadcastReceiver = BackgroundJobBroadcastReceiver()
        //activity?.registerReceiver(backgroundJobBroadcastReceiver, intentFilter)
        //BackgroundJobBroadcastReceiver.notificationUpdateListener = this

        showDataFromDB()
        //Log.d("rakib", "${notificationCommunicator.getPositionClicked()}")

    }

    override fun onPause() {
        super.onPause()
        //activity?.unregisterReceiver(backgroundJobBroadcastReceiver)
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
                                                    if (notificationListAdapter?.itemCount == 0) {
                                                        notificationNoDataLL?.show()
                                                    } else {
                                                        notificationNoDataLL?.hide()
                                                    }
                                                }
                                            }

                                        }
                                        snackbar.setActionTextColor(ContextCompat.getColor(activity, R.color.undo))
                                        snackbar.show()

                                        if (notificationListAdapter?.itemCount == 0) {
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
        notificationListAdapter?.addItem(item)
        notificationListAdapter?.notifyDataSetChanged()
    }

}
