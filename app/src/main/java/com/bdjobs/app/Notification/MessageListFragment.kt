package com.bdjobs.app.Notification


import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.databases.internal.BdjobsDB
import com.bdjobs.app.databases.internal.Notification

import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_notification_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

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

    override fun onResume() {
        super.onResume()
        notificationCommunicator = activity as NotificationCommunicatior
        bdjobsDB = BdjobsDB.getInstance(activity)
        linearLayoutManager = LinearLayoutManager(activity)

        showDataFromDB()
    }

    private fun showDataFromDB() {
        doAsync {
            notificationList = bdjobsDB.notificationDao().getMessage() as? MutableList
//            notificationList?.add(0, Notification(
//                    title = "Video Resume",
//                    body = "",
//                    type = "bpm",
//                    imageLink = "https://images.app.goo.gl/Ebvz1hPuphafQrZr6",
//                    link = "www.google.com"))
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

                if (notificationList?.size!! > 0) {
                    notificationNoDataLL?.hide()
                    notificationsRV?.show()
                } else {
                    notificationsRV?.hide()
                    notificationNoDataLL?.show()
                }
            }
        }
    }

    fun updateView(item: Notification) {
        adapter?.addItem(item)
        adapter?.notifyDataSetChanged()
    }

}
