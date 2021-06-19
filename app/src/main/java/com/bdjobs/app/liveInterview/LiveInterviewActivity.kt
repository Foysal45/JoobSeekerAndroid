package com.bdjobs.app.liveInterview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bdjobs.app.R
import com.bdjobs.app.liveInterview.data.socketClient.SignalingServer
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_live_interview.*
import timber.log.Timber

class LiveInterviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_interview)

        val activityDate = if (intent.getStringExtra("from") == "homePage") "0" else intent.getStringExtra("time")
        val from = intent.getStringExtra("from")
        //Log.d("rakib", "activity date $activityDate")
        Timber.d("activity $activityDate from $from")

        val jobId = intent.getStringExtra("jobId")
        val jobTitle = intent.getStringExtra("jobTitle")

        val navHostFragment = liveInterviewNavHostFragment as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.live_interview_nav_graph)

        //val navController = findNavController(R.id.liveInterviewNavHostFragment)
        val bundle = Bundle().also {
            it.putString("activityDate",activityDate)
            it.putString("jobId",jobId)
            it.putString("jobTitle",jobTitle)
        }


        when (from) {
            "homePage","mybdjobs","popup" -> {
                graph.startDestination = R.id.liveInterviewListFragment
            }
            else -> {
                graph.startDestination = R.id.liveInterviewDetailsFragment
            }
        }

        navHostFragment.navController.setGraph(graph,bundle)

//
//        SignalingServer.get()?.init(this, "6623250")

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}