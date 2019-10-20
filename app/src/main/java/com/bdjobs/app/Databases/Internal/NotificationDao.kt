package com.bdjobs.app.Databases.Internal

import androidx.room.*
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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
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

    @Query("UPDATE Notification SET seen = :seen, seen_time = :seenTime WHERE id = :id")
    fun updateNotification(seenTime: Date, seen: Boolean, id: Int)


}
