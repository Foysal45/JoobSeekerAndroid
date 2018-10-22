package com.bdjobs.app.Utilities


import android.app.Activity
import android.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.crashlytics.android.Crashlytics
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso

fun Any.simpleClassName(fragment: Fragment): String {
    return fragment::class.java.simpleName
}

fun Activity.transitFragment(fragment: Fragment,holderID:Int, addToBackStack: Boolean) {
    val transaction = fragmentManager.beginTransaction()

    if (addToBackStack) {
        transaction.replace(holderID, fragment, simpleClassName(fragment)).addToBackStack(simpleClassName(fragment))
    } else {
        transaction.replace(holderID, fragment, simpleClassName(fragment))
    }
    transaction.commit()
}

fun Activity.transitFragment(fragment: Fragment,holderID:Int) {

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
    return text.toString()
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

fun View.makeClickable() {
    isClickable = true
}

fun View.makeUnClickable() {
    isClickable = false
}

fun Any.debug(message: String) {
    Log.d(this::class.java.simpleName, message)
    Crashlytics.log(Log.DEBUG, this::class.java.simpleName, message);
}

fun Any.debug(message: String, tr: Throwable) {
    Log.d(this::class.java.simpleName, message, tr)
}


fun Any.error(message: String) {
    Log.e(this::class.java.simpleName, message)
    Crashlytics.log(Log.ERROR, this::class.java.simpleName, message);
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
        } else if (dy < 0 && !fab.isShown) {
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

