package com.bdjobs.app.Utilities

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.Notification.NotificationBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.editResume.EditResLandingActivity

fun createShortcut(context: Context) {

    if (Build.VERSION.SDK_INT >= 25) {
        val shortcutManager = context.getSystemService(ShortcutManager::class.java)

        val notificationIntent = Intent(context, NotificationBaseActivity::class.java)
        notificationIntent.action = Intent.ACTION_VIEW

        val jobSearchIntent = Intent(context, JobBaseActivity::class.java)
        jobSearchIntent.action = Intent.ACTION_VIEW
        jobSearchIntent.putExtra("from","generalsearch")

        val editResumeIntent = Intent(context, EditResLandingActivity::class.java)
        editResumeIntent.action = Intent.ACTION_VIEW

        val notificationShortcut = ShortcutInfo.Builder(context, "notifications")
                .setShortLabel("Notifications")
                .setLongLabel("Notifications")
                .setIcon(Icon.createWithResource(context, R.drawable.ic_notifications_black_24dp))
                .setIntent(notificationIntent)
                .build()

        val jobSearchShortcut = ShortcutInfo.Builder(context, "jobSearch")
                .setShortLabel("Search Job")
                .setLongLabel("Search Job")
                .setIcon(Icon.createWithResource(context, R.drawable.ic_search_shortcut))
                .setIntent(jobSearchIntent)
                .build()

        val editResumeShortcut = ShortcutInfo.Builder(context, "editResume")
                .setShortLabel("Edit Resume")
                .setLongLabel("Edit Resume")
                .setIcon(Icon.createWithResource(context, R.drawable.ic_person_shortcut))
                .setIntent(editResumeIntent)
                .build()

        shortcutManager!!.dynamicShortcuts = listOf(jobSearchShortcut, editResumeShortcut,notificationShortcut)
    }
}

fun removeShortcut(context: Context) {
    if (Build.VERSION.SDK_INT >= 25) {
        val shortcutManager = context.getSystemService(ShortcutManager::class.java)
        shortcutManager!!.removeAllDynamicShortcuts()
    }
}