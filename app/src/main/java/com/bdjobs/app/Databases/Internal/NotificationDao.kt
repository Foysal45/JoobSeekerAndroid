package com.bdjobs.app.Databases.Internal

import androidx.room.*

@Dao
interface NotificationDao {

    @Query("SELECT * FROM Notification ORDER BY id DESC")
    fun getAllNotificationsAndMessages(): List<Notification>

    @Query("SELECT * FROM Notification WHERE seen = :seen")
    fun getNotificationsBySeenStatus(seen: Boolean): List<Notification>

    @Query("SELECT * FROM Notification WHERE type = :type ORDER BY id DESC")
    fun getNotificationsByType(type: String): List<Notification>

    @Query("SELECT * FROM Notification WHERE type = :type")
    fun getMessages(type: String): List<Notification>

    @Query("SELECT * FROM Notification WHERE type != :type")
    fun getNotifications(type: String): List<Notification>

    @Query("DELETE FROM Notification")
    fun deleteAllNotifications()

    @Query("SELECT * FROM Notification WHERE id = :id")
    fun getSingleNotificaiton(id: Int): Notification

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNotification(notification: Notification)

    @Delete
    fun deleteNotification(notification: Notification)

    @Transaction
    fun getMessage(): List<Notification> {
        return getMessages("m")
    }

    @Transaction
    fun getNotification(): List<Notification> {
        return getNotifications("m")
    }


}
