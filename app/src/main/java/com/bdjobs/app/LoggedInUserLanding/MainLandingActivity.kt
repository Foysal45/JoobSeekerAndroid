package com.bdjobs.app.LoggedInUserLanding

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bdjobs.app.Jobs.JobBaseActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.SuggestiveSearch.SuggestiveSearchActivity
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.BdjobsUserRequestCode
import com.bdjobs.app.Utilities.Constants.Companion.key_from
import com.bdjobs.app.Utilities.Constants.Companion.key_typedData
import com.crashlytics.android.Crashlytics
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import kotlinx.android.synthetic.main.activity_main_landing.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class MainLandingActivity : Activity() ,HomeCommunicator{



    private val homeFragment = HomeFragment()
    private val hotJobsFragment = HotJobsFragment()
    private val moreFragment = MoreFragment()
    private val shortListedJobFragment = ShortListedJobFragment()
    private val mybdjobsFragment = MyBdjobsFragment()
    private lateinit var session: BdjobsUserSession







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_landing)

        disableShiftMode(bottom_navigation)
        session = BdjobsUserSession(applicationContext)
        Crashlytics.setUserIdentifier(session.userId)
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        bottom_navigation.selectedItemId = R.id.navigation_home

        tetsLog()
    }

    override fun goToKeywordSuggestion() {
        val intent = Intent(this@MainLandingActivity, SuggestiveSearchActivity::class.java)
        intent.putExtra(Constants.key_from, Constants.key_jobtitleET)
        intent.putExtra(key_typedData, "")
        window.exitTransition = null
        startActivityForResult(intent, BdjobsUserRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BdjobsUserRequestCode) {
            if (resultCode == Activity.RESULT_OK) {
                val typedData = data?.getStringExtra(key_typedData)
                val from = data?.getStringExtra(key_from)
                startActivity<JobBaseActivity>(
                        Constants.key_jobtitleET to typedData)
            }
        }
    }



    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                transitFragment(homeFragment, R.id.landingPageFragmentHolderFL)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_shortlisted_jobs -> {
                transitFragment(shortListedJobFragment, R.id.landingPageFragmentHolderFL)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_hotjobs -> {
                transitFragment(hotJobsFragment, R.id.landingPageFragmentHolderFL)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_mybdjobs -> {
                transitFragment(mybdjobsFragment, R.id.landingPageFragmentHolderFL)
                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_more -> {
                transitFragment(moreFragment, R.id.landingPageFragmentHolderFL)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }




    fun tetsLog() {

        Log.d("XZXfg", "\nisCvPosted = ${session.isCvPosted}\n" +
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
