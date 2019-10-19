package com.bdjobs.app.Notification

import android.os.Bundle
import android.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.Notification
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_joblist_layout.backIV
import kotlinx.android.synthetic.main.fragment_notification_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread


class NotificationListFragment : Fragment() {

    private lateinit var notificationCommunicator: NotificationCommunicatior
    lateinit var bdjobsUserSession: BdjobsUserSession
    lateinit var bdjobsDB: BdjobsDB
    private lateinit var linearLayoutManager: LinearLayoutManager

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

        showDataFromDB()
    }

    private fun showDataFromDB() {
        doAsync {
            notificationList = bdjobsDB.notificationDao().getNotification()
            uiThread {
                val notificationListAdapter = NotificationListAdapter(activity, notificationList as MutableList<Notification>)
                notificationsRV?.also {
                    it.setHasFixedSize(true)
                    it.itemAnimator = DefaultItemAnimator()
                    it.adapter = notificationListAdapter
                    it.layoutManager = linearLayoutManager
                }

                if (notificationList?.size!! > 0) {
                    notificationNoDataLL?.hide()
                    notificationsRV?.show()
                } else {
                    notificationsRV?.hide()
                    notificationNoDataLL?.show()
                }
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
                                    notificationListAdapter!!.removeItem(position)
                                    deleteNotificationFromDB(notification)
                                }
                            }
                        }
                val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
                itemTouchHelper.attachToRecyclerView(notificationsRV)
            }
        }
    }


    private fun deleteNotificationFromDB(notification: Notification) {
        doAsync {
            bdjobsUserSession.updateNotificationCount(bdjobsUserSession.notificationCount!! - 1)
            bdjobsDB.notificationDao().deleteNotification(notification)
            uiThread {
                toast("Notification deleted successfully")

            }
        }

    }


}
