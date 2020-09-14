package com.bdjobs.app.liveInterview.ui.interview_details

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.liveInterview.data.models.LiveInterviewDetails
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository
import com.bdjobs.app.videoInterview.util.Event
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class LiveInterviewDetailsViewModel(private val repository: LiveInterviewRepository, val jobId: String) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _liveInterviewDetailsData = MutableLiveData<MutableList<LiveInterviewDetails.Data?>>()
    val liveInterviewDetailsData: LiveData<MutableList<LiveInterviewDetails.Data?>> = _liveInterviewDetailsData

    private val _commonData = MutableLiveData<LiveInterviewDetails.Common?>()
    val commonData: LiveData<LiveInterviewDetails.Common?> = _commonData

    val showConfirmationSection = MutableLiveData<Boolean>().apply {
        value = false
    }

    val showBlackInfoSection = MutableLiveData<Boolean>().apply {
        value = false
    }

    val showTooltip = MutableLiveData<Event<Boolean>>().apply {
        value = Event(false)
    }

    val examDate = MutableLiveData<String>()
    val examTime = MutableLiveData<String>()

    val remainingDays = MutableLiveData<String>()
    val remainingHours = MutableLiveData<String>()
    val remainingMinutes = MutableLiveData<String>()
    val remainingSeconds = MutableLiveData<String>()

    var interviewDateTime = ""

    val openNoPopup = MutableLiveData<Event<Boolean>>()
    val openReschedulePopup = MutableLiveData<Event<Boolean>>()

    var applyId = ""
    var invitationId = ""
    var cancelReason = ""
    var otherReason = ""
    var rescheduleComment = ""

    val showToast = MutableLiveData<Event<String>>()
    val showUndoSnackbar = MutableLiveData<Event<Boolean>>()

    init {
        getLiveInterviewDetails()
    }

    private fun getLiveInterviewDetails() {
        _dataLoading.value = true
        viewModelScope.launch {
            try {
                val response = repository.getLiveInterviewDetailsFromRemote(jobId)
                _liveInterviewDetailsData.value = response.data?.toMutableList()
                _commonData.value = response.common
                _dataLoading.value = false

                response.data?.let {
                    _liveInterviewDetailsData.value?.get(0)?.showUndo = _commonData.value?.showUndo
                }

                //for green section
                showConfirmationSection.value = liveInterviewDetailsData.value?.get(0)?.confimationStatus == "0" || liveInterviewDetailsData.value?.get(0)?.confimationStatus == "5"

                //for black section
                showBlackInfoSection.value = liveInterviewDetailsData.value?.get(0)?.activity == "3"

                //exam date and time
                examDate.value = liveInterviewDetailsData.value?.get(0)?.examDate
                examTime.value = liveInterviewDetailsData.value?.get(0)?.examTime
                interviewDateTime = "${liveInterviewDetailsData.value?.get(0)?.examDate} ${liveInterviewDetailsData.value?.get(0)?.examTime}"

                applyId = commonData.value?.applyId.toString()
                invitationId = liveInterviewDetailsData.value?.get(0)?.invitationId.toString()

                if (liveInterviewDetailsData.value?.get(0)?.confimationStatus == "1")
                    setTimer(interviewDateTime)

//                if (commonData.value?.showUndo == "1")
//                    showUndoSnackbar.value = Event(true)
                delay(1000)

                if (liveInterviewDetailsData.value?.get(0)?.activity == "3")
                    showTooltip.value = Event(true)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setTimer(interviewDateTime: String) {
        Timber.tag("live").d("came here $interviewDateTime")
        val start_calendar: Calendar = Calendar.getInstance()
        val end_calendar: Calendar = Calendar.getInstance()

        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH)
        end_calendar.time = simpleDateFormat.parse(interviewDateTime)

        //end_calendar.set(2020, 8, 15) // 10 = November, month start at 0 = January


        val start_millis: Long = start_calendar.getTimeInMillis() //get the start time in milliseconds

        val end_millis: Long = end_calendar.getTimeInMillis() //get the end time in milliseconds

        val total_millis = end_millis - start_millis //total time in milliseconds

        Timber.tag("live").d("came here total ${total_millis}")


        //1000 = 1 second interval

        //1000 = 1 second interval
        val timer = object : CountDownTimer(total_millis, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                Timber.tag("live").d("came here tick")

                var millisUntilFinished = millisUntilFinished
                val days: Long = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
                millisUntilFinished -= TimeUnit.DAYS.toMillis(days)
                val hours: Long = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                millisUntilFinished -= TimeUnit.HOURS.toMillis(hours)
                val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes)
                val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)

                remainingDays.value = DecimalFormat("00").format(days).toString()
                remainingHours.value = DecimalFormat("00").format(hours).toString()
                remainingMinutes.value = DecimalFormat("00").format(minutes).toString()
                remainingSeconds.value = DecimalFormat("00").format(seconds).toString()

                Timber.d("${remainingDays.value} ${remainingHours.value} ${remainingMinutes.value} ${remainingSeconds.value}")

//                tv_countdown.setText("$days:$hours:$minutes:$seconds") //You can compute the millisUntilFinished on hours/minutes/seconds
            }

            override fun onFinish() {
                //tv_countdown.setText("Finish!")
                Timber.tag("live").d("came here finish")
                remainingDays.value = "00"
                remainingHours.value = "00"
                remainingMinutes.value = "00"
                remainingSeconds.value = "00"
            }
        }.start()
    }

    fun onYesButtonClick() {
        //getLiveInterviewDetails()
        Timber.d("applyId $applyId activity 1 cancelReason $cancelReason otherReason $otherReason rescheduleComment $rescheduleComment invitationId $invitationId")

        viewModelScope.launch {
            val response = repository.sendLiveInterviewConfirmation(
                    applyId = applyId,
                    invitationId = invitationId,
                    activity = "3"
            )
            if (response.statuscode == "4") {
                showToast.value = Event(response.message.toString())
                getLiveInterviewDetails()
                showConfirmationSection.value = false
            } else {
                showToast.value = Event(response.message.toString())
            }
        }
    }

    fun onNoButtonClick() {
        openNoPopup.value = Event(true)
    }

    fun onRescheduleButtonClick() {
        openReschedulePopup.value = Event(true)
    }

    fun onCancelSubmitButtonClick() {

        Timber.d("applyId $applyId activity 2 cancelReason $cancelReason otherReason $otherReason rescheduleComment $rescheduleComment invitationId $invitationId")

        viewModelScope.launch {
            val response = repository.sendLiveInterviewConfirmation(
                    applyId = applyId,
                    invitationId = invitationId,
                    activity = "2",
                    cancelReason = cancelReason,
                    otherComment = otherReason
            )
            if (response.statuscode == "4") {
                showToast.value = Event(response.message.toString())
                getLiveInterviewDetails()
            } else {
                showToast.value = Event(response.message.toString())
            }
        }
    }

    fun onChangeButtonClick() {
        Timber.d("applyId $applyId activity 6 cancelReason $cancelReason otherReason $otherReason rescheduleComment $rescheduleComment invitationId $invitationId")
        viewModelScope.launch {
            val response = repository.sendLiveInterviewConfirmation(
                    applyId = applyId,
                    invitationId = invitationId,
                    activity = "6",
                    cancelReason = "",
                    otherComment = ""
            )
            if (response.statuscode == "4") {
                showToast.value = Event(response.message.toString())
                _dataLoading.value = true
                getLiveInterviewDetails()
            } else {
                showToast.value = Event(response.message.toString())
            }
        }
    }

    fun onRescheduleSubmitClick() {
        Timber.d("applyId $applyId activity 4 cancelReason $cancelReason otherReason $otherReason rescheduleComment $rescheduleComment invitationId $invitationId")
        viewModelScope.launch {
            val response = repository.sendLiveInterviewConfirmation(
                    applyId = applyId,
                    invitationId = invitationId,
                    activity = "4",
                    otherComment = "",
                    rescheduleComment = rescheduleComment
            )
            if (response.statuscode == "4") {
                showToast.value = Event(response.message.toString())
                _dataLoading.value = true
                getLiveInterviewDetails()
            } else {
                showToast.value = Event(response.message.toString())
            }
        }
    }

}