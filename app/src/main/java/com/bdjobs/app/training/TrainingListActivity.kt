package com.bdjobs.app.training

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.transitFragmentX

class TrainingListActivity : AppCompatActivity(), TrainingCommunicator {


    override fun backButtonClicked() {
        onBackPressed()
    }

    private var upcomingTrainingFragment = UpcomingTrainingFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training_list_acitivity)
        transitFragmentX(upcomingTrainingFragment, R.id.fragmentHolder, false)
    }
}
