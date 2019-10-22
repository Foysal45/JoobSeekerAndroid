package com.bdjobs.app.Databases.Internal

import android.app.Activity
import android.content.Intent
import androidx.room.*
import com.bdjobs.app.Utilities.Constants
import java.util.*

@Dao
interface NotificationDao {

    @Query("SELECT * FROM Notification ORDER BY id DESC")
    fun getAllNotificationsAndMessages(): List<Notification>

    @Query("SELECT * FROM Notification WHERE seen = :seen")
    fun getNotificationsBySeenStatus(seen: Boolean): List<Notification>

    @Query("SELECT * FROM Notification WHERE type = :type ORDER BY id DESC")
    fun getNotificationsByType(type: String): List<Notification>

    @Query("SELECT * FROM Notification WHERE type = :type ORDER BY id DESC")
    fun getMessages(type: String): List<Notification>

    @Query("SELECT * FROM Notification WHERE type != :type ORDER BY id DESC")
    fun getNotifications(type: String): List<Notification>

    @Query("DELETE FROM Notification")
    fun deleteAllNotifications()

    @Query("SELECT * FROM Notification WHERE id = :id")
    fun getSingleNotification(id: Int): Notification

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotification(notification: Notification)

    @Delete
    fun deleteNotification(notification: Notification)

    @Query("SELECT COUNT(id) FROM Notification WHERE seen = 0 AND type != :type")
    fun getNotificationsCount(type: String): Int

    @Transaction
    fun getMessage(): List<Notification> {
        return getMessages("m")
    }

    @Transaction
    fun getNotification(): List<Notification> {
        return getNotifications("m")
    }

    @Transaction
    fun getNotificationCount(): Int {
        return getNotificationsCount("m")
    }

    @Transaction
    fun notificationDelete(notification: Notification, activity: Activity){
        deleteNotification(notification)
        val intent = Intent(Constants.BROADCAST_DATABASE_UPDATE_JOB)
        intent.putExtra("job", "insertNotifications")
        activity.sendBroadcast(intent)
    }

    @Query("UPDATE Notification SET seen = :seen, seen_time = :seenTime WHERE id = :id")
    fun updateNotification(seenTime: Date, seen: Boolean, id: Int)

    @Query("UPDATE Notification SET seen = :seen, seen_time = :seenTime WHERE server_id = :id AND type = :type")
    fun updateNotificationTableByClickingNotification(seenTime: Date, seen: Boolean, id: String,type: String)


}
