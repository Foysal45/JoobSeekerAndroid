package com.bdjobs.app.databases.internal

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

    @Query("SELECT * FROM Notification WHERE type = :type ORDER BY arrival_time DESC")
    fun getNotificationsByType(type: String): List<Notification>

    @Query("SELECT DISTINCT * FROM Notification WHERE (type = :type or type = 'ii' or type = 'vi' or type = 'li') ORDER BY arrival_time DESC")
    fun getMessages(type: String): List<Notification>

    @Query("SELECT DISTINCT * FROM Notification WHERE type != :type AND type != 'ii' AND type != 'vi' AND type != 'li' AND is_deleted = 0 GROUP BY server_id ORDER BY id DESC")
    fun getNotifications(type: String): List<Notification>

    @Query("DELETE FROM Notification")
    fun deleteAllNotifications()

    @Query("SELECT * FROM Notification WHERE id = :id")
    fun getSingleNotification(id: Int): Notification

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotification(notification: Notification)

    @Delete
    fun deleteNotification(notification: Notification)

    @Query("SELECT COUNT(id) FROM Notification WHERE seen = 0 AND (type != :type AND type != 'li' AND type != 'ii' AND type != 'vi')")
    fun getNotificationsCount(type: String): Int

    @Query("SELECT COUNT(id) FROM Notification WHERE seen = 0 AND (type = :type or type = 'li' or type = 'ii' or type = 'vi')")
    fun getMessagesCount(type: String):Int

    @Query("SELECT COUNT(arrival_time) FROM Notification where arrival_time = :arrivalTime")
    fun checkSameNotification(arrivalTime:Date?):Int

    @Transaction
    fun getMessage(): List<Notification> {
        return getMessages(Constants.NOTIFICATION_TYPE_PROMOTIONAL_MESSAGE)
    }

    @Transaction
    fun getNotification(): List<Notification> {
        return getNotifications(Constants.NOTIFICATION_TYPE_PROMOTIONAL_MESSAGE)
    }

    @Query("SELECT * FROM Notification  WHERE seen = 0 AND type != :type ORDER BY arrival_time  DESC LIMIT 1")
    fun singleNotification(type: String): Notification

    @Query("SELECT * FROM Notification  WHERE seen = 0  ORDER BY arrival_time  DESC LIMIT 1")
    fun getSingleItem(): Notification

    @Transaction
    fun getSingleNotification(): Notification {
        return singleNotification(Constants.NOTIFICATION_TYPE_PROMOTIONAL_MESSAGE)
    }

    @Transaction
    fun getNotificationCount(): Int {
        return getNotificationsCount(Constants.NOTIFICATION_TYPE_PROMOTIONAL_MESSAGE)
    }

    @Transaction
    fun getMessageCount():Int{
        return getMessagesCount(Constants.NOTIFICATION_TYPE_PROMOTIONAL_MESSAGE)
    }

    @Transaction
    fun notificationDelete(notification: Notification, activity: Activity) {
        deleteNotification(notification)
        val intent = Intent(Constants.BROADCAST_DATABASE_UPDATE_JOB)
        intent.putExtra("job", "insertNotifications")
        activity.sendBroadcast(intent)
    }


    @Query("UPDATE Notification SET seen = :seen, seen_time = :seenTime WHERE notification_id = :id AND type =:type")
    fun updateNotification(seenTime: Date, seen: Boolean, id: String, type: String)

    @Query("UPDATE Notification SET seen = :seen, seen_time = :seenTime  WHERE notification_id = :id AND type = :type")
    fun updateNotificationTableByClickingNotification(seenTime: Date, seen: Boolean, id: String, type: String)

    @Query("DELETE FROM Notification WHERE is_deleted = 1 AND arrival_time <= date('now','-30 day')")
    fun deleteNotificationsFromDatabaseOlderThanLast30Days()

    @Query("UPDATE NOTIFICATION SET is_deleted = 1 WHERE id = :id")
    fun softDeleteNotification(id: Int)

    @Query("Delete FROM Notification WHERE server_id=:id AND type=:type")
    fun deleteNotificationBecauseServerToldMe(id: String, type: String)

    @Query("Select arrival_time FROM NOTIFICATION WHERE type = :type AND notification_id =:id ORDER BY arrival_time DESC")
    fun getNotificationArrivalTime(type: String, id: String) : Date

    @Query("SELECT seen FROM NOTIFICATION WHERE notification_id = :id")
    fun getNotificationSeenStatus(id: String) : Boolean
}
