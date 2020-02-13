package com.bdjobs.app.assessment.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.bdjobs.app.assessment.models.Booking

class TestLocationViewModel(application: Application) : AndroidViewModel(application) {

    var booking : Booking = Booking()

}