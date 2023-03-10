package com.bdjobs.app.utilities


import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.DatePickerDialog
import android.app.Fragment
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.SplashActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import org.jetbrains.anko.layoutInflater
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.abs


fun Activity.callHelpLine() {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:16479")
    startActivity(intent)
}

fun String.toBanglaDigit(): String {
    val banglaDigits = charArrayOf('???', '???', '???', '???', '???', '???', '???', '???', '???', '???')

    if (this == null)
        return ""
    val builder = StringBuilder()
    try {
        for (i in 0 until this.length) {
            if (Character.isDigit(this[i])) {
                if (this[i].toInt() - 48 <= 9) {
                    builder.append(banglaDigits[this.get(i).toInt() - 48])
                } else {
                    builder.append(this.get(i))
                }
            } else {
                builder.append(this[i])
            }
        }
    } catch (e: Exception) {
        //logger.debug("getDigitBanglaFromEnglish: ",e);
        return ""
    }

    return builder.toString()
}

fun Context.getDeviceID(): String {
    return try {
        Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}


fun Context.isBlueCollarUser(): Boolean {
    val bdjobsUserSession = BdjobsUserSession(this)
    val catId = bdjobsUserSession.catagoryId
    var isBlueCollar = false
    try {
        if (catId != null) {
            val aList = ArrayList(
                Arrays.asList<String>(
                    *catId.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                )
            )
            for (i in aList.indices) {
                println(" -->" + aList[i])
                //Log.d("ListOutput", "ListOutput " + aList[i])
                if (!TextUtils.isEmpty(aList[i].toString().trim { it <= ' ' })) {
                    val temCat = Integer.parseInt(aList[i].toString().trim { it <= ' ' })
                    //Log.d("isBlueCollar", "isBlueCollar temCat $temCat")
                    if (temCat > 59) {
                        //Log.d("isBlueCollar", "isBlueCollar value $isBlueCollar")
                        isBlueCollar = true
                        //Log.d("isBlueCollar", "isBlueCollar value $isBlueCollar")
                        break
                    }
                }
            }
        }
    } catch (e: NumberFormatException) {
        e.printStackTrace()
    }
    return isBlueCollar
}


fun Context.getBlueCollarUserId(): Int {
    val bdjobsUserSession = BdjobsUserSession(this)
    val catId = bdjobsUserSession.catagoryId
    var blueCollarId = 0
    try {
        if (catId != null) {
            val aList = ArrayList(
                Arrays.asList<String>(
                    *catId.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                )
            )
            for (i in aList.indices) {
                println(" -->" + aList[i])
                //Log.d("ListOutput", "ListOutput " + aList[i])
                if (!TextUtils.isEmpty(aList[i].toString().trim { it <= ' ' })) {

                    val temCat = Integer.parseInt(aList[i].toString().trim { it <= ' ' })
                    //Log.d("isBlueCollar", "isBlueCollar temCat $temCat")
                    if (temCat > 59) {
                        blueCollarId = temCat
                        break
                    }
                }
            }
        }
    } catch (e: NumberFormatException) {
        e.printStackTrace()
    }
    return blueCollarId
}

fun Context.launchUrl(url: String?) {
    try {
        val formattedUrl = if (!url!!.startsWith("http://") && !url.startsWith("https://")) {
            "http://$url"
        } else url
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl))
        this.startActivity(browserIntent)
    } catch (e: Exception) {
    }
}

fun Context.openUrlInBrowser(url: String?) {
    try {
        if (url.isNullOrBlank())
            return

        val intentBuilder = CustomTabsIntent.Builder()
        intentBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
        val customTabsIntent = intentBuilder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url.trim()))
    } catch (e: Exception) {
        logException(e)
    }
}


fun String.equalIgnoreCase(string: String): Boolean {
    return equals(string, true)
}

fun String.removeLastComma(): String {
    if (endsWith(",")) {
        return substring(0, length - 1)
    }
    return this
}

fun String.countCommas(): Int {
    val someStringArr = this.toCharArray()
    var count = 0
    val someChar = ','

    someStringArr.forEach {
        if (it == someChar)
            count++
    }
    //Log.d("CommaCount", "count: $count")
    return count
}

fun Date.toSimpleDateString(): String {
    val format = SimpleDateFormat("d MMM yyyy")
    return format.format(this)
}

fun Date.toSimpleTimeString(): String {
    val format = SimpleDateFormat("h:mm a")
    return format.format(this)
}

fun View.closeKeyboard(activity: Context) {
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm!!.hideSoftInputFromWindow(this.windowToken, 0)
}

fun Activity.getFCMtoken() {

    FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this) { instanceIdResult ->
        val token = instanceIdResult.token

        Timber.d("token: $token")
    }

}

//Dec 20, 2021

//MMM dd, yyyy

fun Activity.subscribeToFCMTopic(topicName: String) {


    FirebaseMessaging.getInstance().subscribeToTopic(topicName)
        .addOnCompleteListener { task ->
            var msg = "Firebase topic subscription on : $topicName is Successful"
            if (!task.isSuccessful) {
                msg = "Firebase topic subscription on : $topicName is NOT Successful"
            }
            wtf(msg)
        }
//    FirebaseMessaging.getInstance().subscribeToTopic(topicName)
//            .addOnCompleteListener(object : OnCompleteListener<Void> {
//                override fun onComplete(@NonNull task: Task<Void>) {
//                    var msg = "Firebase topic subscription on : $topicName is Successful"
//                    if (!task.isSuccessful) {
//                        msg = "Firebase topic subscription on : $topicName is NOT Successful"
//                    }
//                    wtf(msg)
//
//                }
//            })
}

fun Activity.unsubscribeFromFCMTopic(topicName: String) {
    FirebaseMessaging.getInstance()?.unsubscribeFromTopic(topicName)
        .addOnCompleteListener { task ->
            var msg = "Firebase topic unsubscribe on : $topicName is successful"
            if (!task.isSuccessful) {
                var msg = "Firebase topic unsubscribe on : $topicName is not successful"
            }
            wtf(msg)
        }
}

fun pickDate(
    c: Context,
    cal: Calendar,
    listener: DatePickerDialog.OnDateSetListener,
    from: String? = ""
) {

    if (from == "assessment") {

        var now: Long = System.currentTimeMillis() - 1000

        val dpd = DatePickerDialog(
            c,
            listener,
            // set DatePickerDialog to point to today's date when it loads up
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        dpd.datePicker.minDate = now
        //dpd.datePicker.maxDate = now+(1000*60*60*24*7)

        dpd.show()
    } else {
        val dpd = DatePickerDialog(
            c,
            listener,
            // set DatePickerDialog to point to today's date when it loads up
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        dpd.datePicker.maxDate = Date().time
        dpd.show()
    }


}

//fun pickDate(c: Context, cal: Calendar, listener: DatePickerDialog.OnDateSetListener ) {
//    pickDate()
//}


fun NestedScrollView.behaveYourself(fab: FloatingActionButton) {
    this.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
        if (scrollY > oldScrollY) {
            fab.hide()
        } else {
            fab.show()
        }
    })
}

fun RecyclerView.behaveYourself(fab: FloatingActionButton) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy > 0 || dy < 0 && fab.isShown)
                fab.hide()
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                fab.show()
            }
            super.onScrollStateChanged(recyclerView, newState)
        }
    })
}


fun Any.simpleClassName(fragment: Fragment): String {
    return fragment::class.java.simpleName
}

fun TextInputLayout.showError(errorMessage: String?) {
    isErrorEnabled = true
    error = errorMessage
}

fun TextInputLayout.hideError() {
    isErrorEnabled = false
}


fun TextInputLayout.setError() {
    isErrorEnabled = true
    this.error = "This field can not be empty"
}


fun Activity.showProgressBar(progressBar: ProgressBar?) {
    progressBar?.show()
    disableUserInteraction()

}

fun Activity.stopProgressBar(progressBar: ProgressBar?) {
    progressBar?.hide()
    enableUserInteraction()
}


fun Context.isOnline(): Boolean {
    val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = cm.activeNetworkInfo
    if (activeNetwork != null) {
        // connected to the internet
        if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
            // connected to wifi
            return true
        } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
            // connected to mobile data
            return true
        }
    }
    // not connected to the internet
    return false

}

fun Context.setLanguage(localeName: String) {
    val myLocale = Locale(localeName)
    val res = resources
    val dm = res.displayMetrics
    val conf = res.configuration
    conf.locale = myLocale
    res.updateConfiguration(conf, dm)
    val refresh = Intent(this, SplashActivity::class.java)
    startActivity(refresh)
}

fun Activity.transitFragment(fragment: Fragment, holderID: Int, addToBackStack: Boolean) {
    try {
        fragmentManager?.executePendingTransactions()
        if (!fragment.isAdded) {
            val transaction = fragmentManager.beginTransaction()

            if (addToBackStack) {
                transaction.replace(holderID, fragment, simpleClassName(fragment))
                    .addToBackStack(simpleClassName(fragment))
            } else {
                transaction.replace(holderID, fragment, simpleClassName(fragment))
            }
            transaction.commit()
        } else {
        }
    } catch (e: Exception) {
        logException(e)
    }
}

fun FragmentActivity.transitFragment(
    fragment: androidx.fragment.app.Fragment,
    holderID: Int,
    addToBackStack: Boolean
) {
    try {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        if (addToBackStack) {
            fragmentTransaction.apply {
                replace(holderID, fragment)
                addToBackStack(fragment::class.java.name)
            }
        } else {
            fragmentTransaction.apply {
                replace(holderID, fragment)
            }
        }
        fragmentTransaction.commit()
    } catch (e: Exception) {
        logException(e)
    }
}


fun Activity.transitFragment(fragment: Fragment, holderID: Int) {
    try {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(holderID, fragment, simpleClassName(fragment))
        transaction.commit()
    } catch (e: Exception) {
        logException(e)
    }
}

fun AppCompatActivity.transitFragmentX(
    fragment: androidx.fragment.app.Fragment,
    holderID: Int,
    addToBackStack: Boolean,
    bundle:Bundle?=null
) {
    try {
        if (bundle!=null) {
            fragment.arguments = bundle
        }
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        if (addToBackStack) {
            fragmentTransaction.apply {
                replace(holderID, fragment)
                addToBackStack(fragment::class.java.name)
            }
        } else {
            fragmentTransaction.apply {
                replace(holderID, fragment)
            }
        }
        fragmentTransaction.commit()
    } catch (e: Exception) {
        logException(e)
    }
}


fun Activity.disableUserInteraction() {
    window.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    )
}

fun Activity.enableUserInteraction() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

fun EditText?.getString(): String {
    return this!!.text.trim().toString()
}

fun Activity.ACTVValidation(char: String, et: AutoCompleteTextView, til: TextInputLayout): Boolean {
    when {
        TextUtils.isEmpty(char) -> {
            til.showError(getString(R.string.field_empty_error_message_common))
            requestFocus(et)
            return false
        }
        char.length < 2 -> {
            til.showError("it is too short")
            requestFocus(et)
            return false
        }
        else -> til.hideError()
    }
    return true
}

fun isValidate(
    etCurrent: TextInputEditText?, tilCurrent: TextInputLayout?,
    etNext: TextInputEditText?, last: Boolean, validation: Int
): Int {
    var valid: Int = validation
    if (last) {
        if (TextUtils.isEmpty(etCurrent?.getString())) {
            tilCurrent?.showError("This field can not be empty")
        } else {
            valid++
            tilCurrent?.isErrorEnabled = false
            etNext?.requestFocus()
        }
    } else {
        if (TextUtils.isEmpty(etCurrent?.getString())) {
            tilCurrent?.showError("This field can not be empty")
            etCurrent?.requestFocus()
        }
        etNext?.requestFocus()
        tilCurrent?.hideError()
    }
    return valid
}

fun marksValidation(char: String, et: TextInputEditText, til: TextInputLayout): Boolean {
    when {
        TextUtils.isEmpty(char) -> {
            til.showError("This Field can not be empty")
            return false
        }
        char.length < 1 -> {
            et.requestFocus()
            return false
        }
        else -> til.hideError()
    }
    return true
}

fun mobileValidation(char: String, et: TextInputEditText, til: TextInputLayout): Boolean {
    when {
        TextUtils.isEmpty(char) -> {
            til.showError("This Field can not be empty")
            return false
        }
        char.length < 6 -> {
            et.requestFocus()
            return false
        }
        else -> til.hideError()
    }
    return true
}

fun isValidateAutoCompleteTV(
    etCurrent: AutoCompleteTextView?, tilCurrent: TextInputLayout?,
    etNext: TextInputEditText?, isEmpty: Boolean, validation: Int
): Int {
    var valid: Int = validation
    if (isEmpty) {
        etCurrent?.requestFocus()
        tilCurrent?.isErrorEnabled = true
        tilCurrent?.showError("This field can not be empty")
    } else {
        valid++
        tilCurrent?.isErrorEnabled = false
        etNext?.requestFocus()
    }
    return valid
}

fun Activity.requestFocus(view: View) {
    if (view.requestFocus()) {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }
}

fun Any.logException(e: java.lang.Exception) {
    val crashlytics = FirebaseCrashlytics.getInstance()
    //crashlytics.logException(e)
    Log.e(this::class.java.simpleName, e.toString())
}

fun ImageView.loadImageFromUrl(url: String?) {
    try {
        Picasso.get()
            .load(url?.trim())
            .into(this)
    } catch (e: Exception) {
        logException(e)
    }
}

fun TextInputEditText.enableOrdisableEdit(b: Boolean) {
    this.isEnabled = b
}

fun ImageView.loadCircularImageFromUrl(url: String?) {
    try {
        Picasso.get()
            .load(url?.trim())
            .transform(CircleTransform())
            .into(this)
    } catch (e: Exception) {
        logException(e)
    }
}

fun ImageView.loadCircularImageFromUrlWithoutCach(url: String?) {
    try {
        Picasso.get()
            .load(url?.trim())
            .transform(CircleTransform())
            .networkPolicy(NetworkPolicy.NO_CACHE)
            .memoryPolicy(MemoryPolicy.NO_CACHE)
            .into(this)
    } catch (e: Exception) {
        logException(e)
    }
}

fun EditText.clearText() {
    text?.clear()
}

fun EditText.showKeyboard(activity: Activity?) {
    this.requestFocus()
    val imm: InputMethodManager? =
        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.showSoftInput(this, InputMethodManager.SHOW_FORCED)
}

fun ChipGroup.radioCheckableChip(chip: Chip) {
    for (item in 0 until this.childCount) {
        this.getChildAt(item).isClickable = true
    }
    chip.isClickable = false
}

fun EditText.clearTextOnDrawableRightClick() {
    setOnTouchListener(OnTouchListener { v, event ->
        val DRAWABLE_LEFT = 0
        val DRAWABLE_TOP = 1
        val DRAWABLE_RIGHT = 2
        val DRAWABLE_BOTTOM = 3
        try {
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= right - compoundDrawables[DRAWABLE_RIGHT]?.bounds?.width()!!) {
                    clearText()
                    return@OnTouchListener false
                }
            }
        } catch (e: Exception) {
            logException(e)
            return@OnTouchListener false
        }
        false
    })
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.makeClickable() {
    isClickable = true
}

fun View.makeUnClickable() {
    isClickable = false
}

fun Any.debug(message: String) {
    //Log.d(this::class.java.simpleName, message)
    val crashlytics = FirebaseCrashlytics.getInstance()
    //crashlytics.log(Log.DEBUG, this::class.java.simpleName, message)
}

fun Any.d(message: String) {
    //Log.d("+++" + this::class.java.simpleName, message)
}

fun Any.debug(message: String, tr: Throwable) {
    //Log.d(this::class.java.simpleName, message, tr)
}


fun Any.error(message: String) {
    Log.e(this::class.java.simpleName, message)
    //Crashlytics.log(Log.ERROR, this::class.java.simpleName, message)
}

fun Any.error(message: String, tr: Throwable) {
    Log.e(this::class.java.simpleName, message, tr)
}

fun Any.info(message: String) {
    Log.i(this::class.java.simpleName, message)
}

fun Any.info(message: String, tr: Throwable) {
    Log.i(this::class.java.simpleName, message, tr)
}

fun Any.verbose(message: String) {
    Log.v(this::class.java.simpleName, message)
}

fun TextInputEditText.clear() {
    this.setText("")
}

fun Any.verbose(message: String, tr: Throwable) {
    Log.v(this::class.java.simpleName, message, tr)
}

fun Any.warn(message: String) {
    Log.w(this::class.java.simpleName, message)
}


fun Any.warn(message: String, tr: Throwable) {
    Log.w(this::class.java.simpleName, message, tr)
}

fun Any.warn(tr: Throwable) {
    Log.w(this::class.java.simpleName, tr)
}

fun Any.wtf(message: String) {
    Log.wtf(this::class.java.simpleName, message)
}


fun Any.wtf(message: String, tr: Throwable) {
    Log.wtf(this::class.java.simpleName, message, tr)
}

fun Any.wtf(tr: Throwable) {
    Log.wtf(this::class.java.simpleName, tr)
}

fun RecyclerView.bindFloatingActionButton(fab: FloatingActionButton) =
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0 && fab.isShown) {
                fab.hide()
            } else if (dy <= 0 && !fab.isShown) {
                fab.show()
            }
        }
    })

/**
 * Calls the given function on [TextWatcher.afterTextChanged]
 */
fun TextView.easyOnTextChangedListener(listener: (e: CharSequence) -> Unit) =
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable) {
        }

        override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
            listener(p0)
        }
    })


fun AutoCompleteTextView.easyOnTextChangedListener(listener: (e: CharSequence) -> Unit) =
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable) {
        }

        override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
            listener(p0)
        }
    })

fun Activity.getAppVersion(): String {
    val pinfo = packageManager.getPackageInfo(packageName, 0)
    return pinfo.versionName
}

fun Activity.getAppVersionCode(): Int {
    val pinfo = packageManager.getPackageInfo(packageName, 0)
    return pinfo.versionCode
}

fun Context.getDeviceInformation(): HashMap<String, String> {

    var percentAvail: Long = 0
    var totalRAM: Long = 0
    val mi = ActivityManager.MemoryInfo()
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    activityManager.getMemoryInfo(mi)
    val availableMegs = mi.availMem / 1048576L


    val freeBytesInternal = File(filesDir.absoluteFile.toString()).freeSpace
    val totalBytesInternal = File(filesDir.absoluteFile.toString()).totalSpace
    val manager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val carrierName = manager?.networkOperatorName

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        totalRAM = mi.totalMem / 1048576L
        percentAvail = mi.availMem / mi.totalMem
    }

    var ssid: String? = null
    try {
        val connManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (networkInfo != null) {
            if (networkInfo.isConnected) {
                val wifiManager =
                    applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val connectionInfo = wifiManager.connectionInfo
                if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.ssid)) {
                    ssid = connectionInfo.ssid

                }
            }
        }
    } catch (e: Exception) {
    }
    var pInfo: PackageInfo? = null
    var versionCode = "0"
    try {
        pInfo = packageManager.getPackageInfo(packageName, 0)
        versionCode = pInfo!!.versionName + " (" + pInfo.versionCode + ")"
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }

    val c = Calendar.getInstance()
    val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val currentDateAndTime = df.format(c.time)

    var screenInches: Double = 0.0
    try {
        val dm = DisplayMetrics()
        (this as Activity).windowManager.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels
        val height = dm.heightPixels
        val wi = width.toDouble() / dm.xdpi.toDouble()
        val hi = height.toDouble() / dm.ydpi.toDouble()
        val x = Math.pow(wi, 2.0)
        val y = Math.pow(hi, 2.0)
        screenInches = Math.sqrt(x + y)
    } catch (e: Exception) {

    }

    val deviceInfo = HashMap<String, String>()
    deviceInfo[Constants.KEY_MANUFACTURER] = Build.MANUFACTURER.toUpperCase()
    deviceInfo[Constants.KEY_OS_VERSION] =
        System.getProperty("os.version") + "(" + Build.VERSION.INCREMENTAL + ")"
    deviceInfo[Constants.KEY_OS_API_LEVEL] = Build.VERSION.SDK_INT.toString()
    deviceInfo[Constants.KEY_DEVICE] = Build.DEVICE
    deviceInfo[Constants.KEY_MODEL_PRODUCT] = Build.MODEL + " (" + Build.PRODUCT + ")"
    deviceInfo[Constants.KEY_TOTAL_INTERNAL_STORAGE] = (totalBytesInternal / 1e+6).toString()
    deviceInfo[Constants.KEY_FREE_INTERNAL_STORAGE] = (freeBytesInternal / 1e+6).toString()
    deviceInfo[Constants.KEY_MOBILE_NETWORK] = carrierName
    deviceInfo[Constants.KEY_DATE_AND_TIME] = currentDateAndTime
    deviceInfo[Constants.KEY_APP_VERSION] = versionCode
    deviceInfo[Constants.KEY_SCREEN_SIZE] = screenInches.toString()

    if (ssid != null) {
        deviceInfo[Constants.KEY_CONNECTED_WIFI] = ssid
    }
    if (totalRAM != 0L) {
        deviceInfo[Constants.KEY_DEVICE_RAM_SIZE] = totalRAM.toString()
    }

    deviceInfo[Constants.KEY_DEVICE_FREE_RAM_SIZE] = availableMegs.toString()

    return deviceInfo
}

fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}


//fun Context.showJobApplicationGuidelineDialog() {
//
//    val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
//    dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
//    dialog?.setCancelable(true)
//    dialog?.setContentView(R.layout.job_application_guideline)
//    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//    val backImageView = dialog?.findViewById<ImageView>(R.id.job_application_guideline_back_arrow)
//    val dropArrowImageView = dialog?.findViewById<ImageView>(R.id.job_application_guideline_drop_arrow)
//    val scrollView = dialog?.findViewById<ScrollView>(R.id.scroll_view)
//    val titleView = dialog?.findViewById<TextView>(R.id.job_application_guideline_how_it_works_title)
//
//    dropArrowImageView?.setOnClickListener {
//        scrollView?.post { scrollView.smoothScrollTo(0, titleView!!.bottom) }
//    }
//
//    backImageView?.setOnClickListener {
//        dialog?.dismiss()
//    }
//
//    dialog?.show()
//}

fun String.toFormattedSeconds(): String {

    val totalSeconds = this.toInt()
    var formattedTime = ""
    return if (totalSeconds <= 60) {
        formattedTime = String.format("%02d:%02d", 0, totalSeconds)
        formattedTime
    } else {
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        formattedTime = String.format("%02d:%02d", minutes, seconds)
        formattedTime
    }


}

@SuppressLint("RestrictedApi")
fun MaterialButton.changeColor(color: Int) {
    this.supportBackgroundTintList = AppCompatResources.getColorStateList(this.context, color)
}

fun Context.openSettingsDialog() {
    val dialog = MaterialAlertDialogBuilder(this).create()
    val view = this.layoutInflater.inflate(R.layout.dialog_enable_video_permissions, null)
    view?.apply {
        findViewById<Button>(R.id.dialog_btn_cancel).setOnClickListener {
            dialog.dismiss()
        }
        findViewById<Button>(R.id.dialog_btn_go_to_settings).setOnClickListener {
            val intent = createAppSettingsIntent(this@openSettingsDialog)
            startActivity(intent)
            dialog.dismiss()
        }
    }
    dialog.setView(view)
    dialog.show()
}

private fun createAppSettingsIntent(context: Context) = Intent().apply {
    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    data = Uri.fromParts("package", context.packageName, null)
}

fun Context.isFirstInstall(): Boolean {
    return try {
        val firstInstallTime =
            this.packageManager.getPackageInfo(this.packageName, 0).firstInstallTime
        val lastUpdateTime =
            this.packageManager.getPackageInfo(this.packageName, 0).lastUpdateTime
        firstInstallTime == lastUpdateTime
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        true
    }
}


fun Context.isInstallFromUpdate(): Boolean {
    return try {
        val firstInstallTime =
            this.packageManager.getPackageInfo(this.packageName, 0).firstInstallTime
        val lastUpdateTime =
            this.packageManager.getPackageInfo(this.packageName, 0).lastUpdateTime
        firstInstallTime != lastUpdateTime
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        false
    }
}

val currentDate: String
    @SuppressLint("SimpleDateFormat")
    get() = SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().time)

val currentTime: String
    @SuppressLint("SimpleDateFormat")
    get() = SimpleDateFormat("hh:mm:ss a").format(Calendar.getInstance().time)

@SuppressLint("SimpleDateFormat")
fun getDifferenceBetweenDates(currentDate: String, installedAt: String): String {

    val date1: Date
    val date2: Date

    val dates = SimpleDateFormat("MM/dd/yyyy")
    date1 = dates.parse(installedAt)!!
    date2 = dates.parse(currentDate)!!

    val difference: Long = abs(date1.time - date2.time)
    val differenceDates = difference / (24 * 60 * 60 * 1000)

    return differenceDates.toString()

}