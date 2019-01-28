package com.bdjobs.app.Utilities


import android.app.Activity
import android.app.DatePickerDialog
import android.app.Fragment
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.NonNull
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.SplashActivity
import com.crashlytics.android.Crashlytics
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*


fun Activity.callHelpLine() {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:16479")
    startActivity(intent)
}

fun String.toBanglaDigit():String{
    val banglaDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

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

fun Context.getDeviceID():String{
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
            val aList = ArrayList(Arrays.asList<String>(*catId.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()))
            for (i in aList.indices) {
                println(" -->" + aList[i])
                Log.d("ListOutput", "ListOutput " + aList[i])
                if (!TextUtils.isEmpty(aList[i].toString().trim { it <= ' ' })) {
                    val temCat = Integer.parseInt(aList[i].toString().trim { it <= ' ' })
                    Log.d("isBlueCollar", "isBlueCollar temCat $temCat")
                    if (temCat > 59) {
                        Log.d("isBlueCollar", "isBlueCollar value $isBlueCollar")
                        isBlueCollar = true
                        Log.d("isBlueCollar", "isBlueCollar value $isBlueCollar")
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
            val aList = ArrayList(Arrays.asList<String>(*catId.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()))
            for (i in aList.indices) {
                println(" -->" + aList[i])
                Log.d("ListOutput", "ListOutput " + aList[i])
                if (!TextUtils.isEmpty(aList[i].toString().trim { it <= ' ' })) {

                    val temCat = Integer.parseInt(aList[i].toString().trim { it <= ' ' })
                    Log.d("isBlueCollar", "isBlueCollar temCat $temCat")
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


fun Context.openUrlInBrowser(url: String?) {
    if (url.isNullOrBlank())
        return
    val intentBuilder = CustomTabsIntent.Builder()
    intentBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
    intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
    val customTabsIntent = intentBuilder.build()
    customTabsIntent.launchUrl(this, Uri.parse(url))
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

fun Date.toSimpleDateString(): String {
    val format = SimpleDateFormat("dd/MM/yyy")
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
        info("newToken $token")
    }
}

fun Activity.subscribeToFCMTopic(topicName: String) {

    FirebaseMessaging.getInstance().subscribeToTopic(topicName)
            .addOnCompleteListener(object : OnCompleteListener<Void> {
                override fun onComplete(@NonNull task: Task<Void>) {
                    var msg = "Firebase topic subscription on : $topicName is Successful"
                    if (!task.isSuccessful) {
                        msg = "Firebase topic subscription on : $topicName is NOT Successful"
                    }
                    wtf(msg)

                }
            })
}

fun pickDate(c: Context, now: Calendar, listener: DatePickerDialog.OnDateSetListener) {
    val dpd = DatePickerDialog(c,
            listener,
            // set DatePickerDialog to point to today's date when it loads up
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH))
    dpd.show()
}


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


fun Activity.showProgressBar(progressBar: ProgressBar) {
    progressBar.show()
    disableUserInteraction()

}

fun Activity.stopProgressBar(progressBar: ProgressBar) {
    progressBar.hide()
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
    val transaction = fragmentManager.beginTransaction()

    if (addToBackStack) {
        transaction.replace(holderID, fragment, simpleClassName(fragment)).addToBackStack(simpleClassName(fragment))
    } else {
        transaction.replace(holderID, fragment, simpleClassName(fragment))
    }
    transaction.commit()
}

fun Activity.transitFragment(fragment: Fragment, holderID: Int) {

    val transaction = fragmentManager.beginTransaction()
    transaction.replace(holderID, fragment, simpleClassName(fragment))
    transaction.commit()
}


fun Activity.disableUserInteraction() {
    window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

fun Activity.enableUserInteraction() {
    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
}

fun EditText.getString(): String {
    return text.trim().toString()
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

fun isValidate(etCurrent: TextInputEditText?, tilCurrent: TextInputLayout?,
               etNext: TextInputEditText?, last: Boolean, validation: Int): Int {
    var valid: Int = validation
    if (last) {
        if (TextUtils.isEmpty(etCurrent?.getString())) {
            tilCurrent?.showError("This Field can not be empty")
        } else {
            valid++
            tilCurrent?.isErrorEnabled = false
            etNext?.requestFocus()
        }
    } else {
        if (TextUtils.isEmpty(etCurrent?.getString())) {
            tilCurrent?.showError("This Field can not be empty")
            etCurrent?.requestFocus()
        }
        etNext?.requestFocus()
        tilCurrent?.hideError()
    }
    return valid
}

fun Activity.requestFocus(view: View) {
    if (view.requestFocus()) {
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }
}

fun Any.logException(e: java.lang.Exception) {
    Crashlytics.logException(e)
    Log.e(this::class.java.simpleName, e.toString())
}

fun ImageView.loadImageFromUrl(url: String) {
    try {
        Picasso.get().load(url).into(this)
    } catch (e: Exception) {
        logException(e)
    }
}


fun ImageView.loadCircularImageFromUrl(url: String?) {
    try {
        Picasso.get().load(url).transform(CircleTransform()).memoryPolicy(MemoryPolicy.NO_CACHE).into(this)
    } catch (e: Exception) {
        logException(e)
    }
}

fun EditText.clearText() {
    text?.clear()
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
                    return@OnTouchListener true
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
    Log.d(this::class.java.simpleName, message)
    Crashlytics.log(Log.DEBUG, this::class.java.simpleName, message)
}

fun Any.d(message: String) {
    Log.d("+++" + this::class.java.simpleName, message)
}

fun Any.debug(message: String, tr: Throwable) {
    Log.d(this::class.java.simpleName, message, tr)
}


fun Any.error(message: String) {
    Log.e(this::class.java.simpleName, message)
    Crashlytics.log(Log.ERROR, this::class.java.simpleName, message)
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

fun RecyclerView.bindFloatingActionButton(fab: FloatingActionButton) = this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
fun TextView.easyOnTextChangedListener(listener: (e: CharSequence) -> Unit) = this.addTextChangedListener(object : TextWatcher {
    override fun afterTextChanged(p0: Editable) {
    }

    override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
        listener(p0)
    }
})

