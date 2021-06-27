package com.bdjobs.app.liveInterview.ui.interview_details

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.CountDownTimer
import android.provider.CalendarContract
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bdjobs.app.liveInterview.data.models.LiveInterviewDetails
import com.bdjobs.app.liveInterview.data.repository.LiveInterviewRepository
import com.bdjobs.app.videoInterview.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class LiveInterviewDetailsViewModel(
        private val repository: LiveInterviewRepository,
        private val contentResolver: ContentResolver,
        val jobId: String) : ViewModel() {

    val calendarInfos = MutableLiveData<ArrayList<String>>().apply {
        value = arrayListOf()
    }

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _liveInterviewDetailsData = MutableLiveData<MutableList<LiveInterviewDetails.Data?>>()
    val liveInterviewDetailsData: LiveData<MutableList<LiveInterviewDetails.Data?>> = _liveInterviewDetailsData

    private val _commonData = MutableLiveData<LiveInterviewDetails.Common?>()
    val commonData: LiveData<LiveInterviewDetails.Common?> = _commonData

    val showConfirmationSection = MutableLiveData<Boolean>().apply {
        value = false
    }

    val showPreparationSection = MutableLiveData<Boolean>().apply { value = false }
    val showJoinSection = MutableLiveData<Boolean>().apply { value = false }
    val showJoinInterviewSmallBTN = MutableLiveData<Boolean>().apply { value = false }

    private val _jobID = MutableLiveData<String>().apply {
        value = jobId
    }
    val jobID: LiveData<String> = _jobID

    private val _applyID = MutableLiveData<String>().apply {
        value = applyId
    }
    val applyID: LiveData<String> = _applyID

    private val _processID = MutableLiveData<String>()
    val processID:LiveData<String> = _processID

    private val _levelStatus = MutableLiveData<String>()
    val levelStatus:LiveData<String> = _levelStatus

    val examDate = MutableLiveData<String?>()
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

    val addToCalendarClickEvent = MutableLiveData<Event<Boolean>>()
    val onAddedToCalendarEvent = MutableLiveData<Event<Boolean>>()
    val takePreparationClickEvent = MutableLiveData<Event<Boolean>>()
    val joinInterviewClickEvent = MutableLiveData<Event<Boolean>> ()

    lateinit var timer: CountDownTimer

    init {
        getAllCalendars()
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

                //exam date and time
                examDate.value = liveInterviewDetailsData.value?.get(0)?.examDate
                examTime.value = liveInterviewDetailsData.value?.get(0)?.examTime
                interviewDateTime = "${liveInterviewDetailsData.value?.get(0)?.examDate} ${liveInterviewDetailsData.value?.get(0)?.examTime}"
                _processID.value = liveInterviewDetailsData.value?.get(0)?.processId
                _levelStatus.value = liveInterviewDetailsData.value?.get(0)?.levelstatus



                applyId = commonData.value?.applyId.toString()
                _applyID.value = commonData.value?.applyId.toString()
                invitationId = liveInterviewDetailsData.value?.get(0)?.invitationId.toString()

                try {
                    eventAlreadyAdded("Live Interview - ${commonData.value?.jobTitle}")
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val confirmationStatus = liveInterviewDetailsData.value?.get(0)?.confimationStatus
                if (confirmationStatus == "1"){
                    showPreparationSection.value = true
                    setTimer(interviewDateTime)
                }

                if (confirmationStatus == "6" || confirmationStatus == "7"){
                    showJoinSection.value = false
                    showPreparationSection.value = false
                }

                //change here
                showJoinSection.value = true


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getAllCalendars() {

        viewModelScope.launch {
            getAllCalendarInfoFromProvider()
        }
    }

    private fun setTimer(interviewDateTime: String) {
     //   Timber.tag("live").d("came here $interviewDateTime")
        val start_calendar: Calendar = Calendar.getInstance()
        val end_calendar: Calendar = Calendar.getInstance()

        val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH)
        end_calendar.time = simpleDateFormat.parse(interviewDateTime)


        val start_millis: Long = start_calendar.getTimeInMillis() //get the start time in milliseconds

        val end_millis: Long = end_calendar.getTimeInMillis() //get the end time in milliseconds

        val total_millis = end_millis - start_millis //total time in milliseconds

      //  Timber.tag("live").d("came here total ${total_millis}")

        timer = object : CountDownTimer(total_millis, 1000) {


            override fun onTick(millisUntilFinished: Long) {
              //  Timber.tag("live").d("came here tick")

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

            //    Timber.d("Minutes: $minutes :: Hours: $hours")
                if(hours < 1 && minutes < 30){
                    showJoinInterviewSmallBTN.value = true
                }
            }

            override fun onFinish() {
                //tv_countdown.setText("Finish!")
                Timber.tag("live").d("came here finish")
                remainingDays.value = "00"
                remainingHours.value = "00"
                remainingMinutes.value = "00"
                remainingSeconds.value = "00"
                showPreparationSection.value = false
                showJoinSection.value = true
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
                showPreparationSection.value = true
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

    fun onAddToCalendarButtonClick() {
        addToCalendarClickEvent.value = Event(true)

    }

    fun onTakePreparationButtonClick() {
        takePreparationClickEvent.value = Event(true)
    }

    fun onJoinInterviewButtonClick() {
        joinInterviewClickEvent.value = Event(true)
    }

    fun insert() {
        viewModelScope.launch {
            insertCalendarEvent(commonData.value)
        }
    }


    private suspend fun getAllCalendarInfoFromProvider() {
        return withContext(Dispatchers.IO) {
            try {
                val cur = contentResolver.query(CalendarContract.Calendars.CONTENT_URI, EVENT_PROJECTION, null, null, null)

                if (cur != null) {
                    while (cur.moveToNext()) {
                        var calID: Long = 0
                        var displayName = ""
                        var accountName = ""
                        var ownerName = ""
                        // Get the field values
                        calID = cur.getLong(PROJECTION_ID_INDEX)
                        displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX)
                        accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX)
                        ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX)
                        val calendarInfo = String.format("Calendar ID: %s\nDisplay Name: %s\nAccount Name: %s\nOwner Name: %s", calID, displayName, accountName, ownerName)
                        calendarInfos.value?.add(calendarInfo)
                    }
                } else {
                    Timber.d("cursor null")
                    cur?.close()
                    return@withContext
                }
                cur.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun eventAlreadyAdded(invitationId: String?) {
        return withContext(Dispatchers.IO) {
            val INSTANCE_PROJECTION = arrayOf(
                    CalendarContract.Instances.EVENT_ID,  // 0
                    CalendarContract.Instances.BEGIN,  // 1
                    CalendarContract.Instances.TITLE, // 2
                    CalendarContract.Instances.ORIGINAL_ID
            )
            val calendar = Calendar.getInstance()
            val simpleDateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH)
            calendar.time = simpleDateFormat.parse("${examDate.value} ${examTime.value}")

            val calID: Long = 1
            val startMillis: Long = calendar.timeInMillis

            val endMillis: Long = calendar.run {
                calendar.add(Calendar.HOUR, 1)
                timeInMillis
            }

            // The ID of the recurring event whose instances you are searching for in the Instances table
            val selection = CalendarContract.Instances.TITLE + " = ?"
            val selectionArgs = arrayOf(invitationId)

            // Construct the query with the desired date range.
            val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
            ContentUris.appendId(builder, startMillis)
            ContentUris.appendId(builder, endMillis)

            // Submit the query
            val cur: Cursor? = contentResolver.query(builder.build(), INSTANCE_PROJECTION, selection, selectionArgs, null)

            if (cur == null) {
                Timber.d("cursor null")
                onAddedToCalendarEvent.postValue(Event(false))
            } else {
                if (cur.count > 0) {
                    Timber.d("cursor > 0")
                    onAddedToCalendarEvent.postValue(Event(true))
                    cur.close()

                } else {
                    Timber.d("cursor == 0")
                    onAddedToCalendarEvent.postValue(Event(false))
                    cur.close()
                }
            }
        }

    }

    private suspend fun insertCalendarEvent(data: LiveInterviewDetails.Common?) {


        return withContext(Dispatchers.IO) {

            val calendar = Calendar.getInstance()
            val simpleDateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH)
            calendar.time = simpleDateFormat.parse("${examDate.value} ${examTime.value}")

            val calID: Long = 1
            val startMillis: Long = calendar.timeInMillis

            val endMillis: Long = calendar.run {
                calendar.add(Calendar.HOUR, 1)
                timeInMillis
            }

            val values = ContentValues().apply {
                put(CalendarContract.Events.DTSTART, startMillis)
                put(CalendarContract.Events.DTEND, endMillis)
                put(CalendarContract.Events.TITLE, "Live Interview - ${data?.jobTitle}")
                put(CalendarContract.Events.DESCRIPTION, "Live Interview from ${data?.companyName} for the position ${data?.jobTitle}")
                put(CalendarContract.Events.CALENDAR_ID, calID)
                put(CalendarContract.Events.ORIGINAL_ID, applyId)
                put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Dhaka")
            }
            val uri: Uri? = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)

            val eventID = uri?.lastPathSegment?.toLong()

            onAddedToCalendarEvent.postValue(Event(true))
        }
    }

    companion object {

        private const val PROJECTION_ID_INDEX: Int = 0
        private const val PROJECTION_ACCOUNT_NAME_INDEX: Int = 1
        private const val PROJECTION_DISPLAY_NAME_INDEX: Int = 2
        private const val PROJECTION_OWNER_ACCOUNT_INDEX: Int = 3

        private val EVENT_PROJECTION: Array<String> = arrayOf(
                CalendarContract.Calendars._ID,                     // 0
                CalendarContract.Calendars.ACCOUNT_NAME,            // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,   // 2
                CalendarContract.Calendars.OWNER_ACCOUNT            // 3
        )
    }

    override fun onCleared() {
        super.onCleared()
        try {
            timer.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



}