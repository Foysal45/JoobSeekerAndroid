package com.bdjobs.app.LoggedInUserLanding

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import android.util.Log
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.debug
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.transitFragment
import com.crashlytics.android.Crashlytics
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import kotlinx.android.synthetic.main.activity_main_landing.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class MainLandingActivity : Activity() {

    private val homeFragment = HomeFragment()
    private val hotJobsFragment = HotJobsFragment()
    private val moreFragment = MoreFragment()
    private val shortListedJobFragment = ShortListedJobFragment()
    private lateinit var session: BdjobsUserSession


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_landing)
        disableShiftMode(bottom_navigation)
        session = BdjobsUserSession(applicationContext)
        Crashlytics.setUserIdentifier(session.userId)

        transitFragment(homeFragment, R.id.landingPageFragmentHolderFL)

        Log.d("hdaahd", "\nisCvPosted = ${session.isCvPosted}\n" +
                "userPicUrl = ${session.userPicUrl}\n" +
                "name = ${session.fullName}\n" +
                "email = ${session.email}\n" +
                "userId = ${session.userId}\n" +
                "decodId = ${session.decodId}\n" +
                "userName = ${session.userName}\n" +
                "AppsDate = ${session.AppsDate}\n" +
                "age = ${session.age}\n" +
                "exp = ${session.exp}\n" +
                "catagoryId = ${session.catagoryId}\n" +
                "gender = ${session.gender}\n" +
                "resumeUpdateON = ${session.resumeUpdateON}\n" +
                "IsResumeUpdate = ${session.IsResumeUpdate}\n" +
                "trainingId = ${session.trainingId}\n")
    }


    @SuppressLint("RestrictedApi")
    fun disableShiftMode(view: BottomNavigationView) {
        val menuView = view.getChildAt(0) as BottomNavigationMenuView
        menuView.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
        menuView.buildMenuView()
        try {
            menuView.javaClass.getDeclaredField("mShiftingMode").also { shiftMode ->
                shiftMode.isAccessible = true
                shiftMode.setBoolean(menuView, false)
                shiftMode.isAccessible = false
            }
            for (i in 0 until menuView.childCount) {
                (menuView.getChildAt(i) as BottomNavigationItemView).also { item ->
                    item.setShifting(false) // shifting animation
                    item.setChecked(item.itemData.isChecked)
                    debug("navigation position is : $i")
                }
            }
        } catch (e: Exception) {
            logException(e)
        }
    }


}
