package com.bdjobs.app.ajkerDeal.utilities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageInfo
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewpager2.widget.ViewPager2
import com.bdjobs.app.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.io.File
import kotlin.random.Random

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Context?.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.toast(msg: String?, time: Int = Toast.LENGTH_SHORT) {
    if (!msg.isNullOrEmpty()) {
        val toast = Toast.makeText(this, msg, time)
        val view = toast.view
        view?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black_100))
        val textView = view?.findViewById<TextView>(android.R.id.message)
        textView?.setTextColor(ContextCompat.getColor(this, R.color.white))
        toast.show()
    }
}

fun RecyclerView.disableItemAnimator() {
    (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
}

fun Activity.alert(
    title: CharSequence? = null,
    message: CharSequence? = null,
    showCancel: Boolean = false,
    positiveButtonText: String = "ঠিক আছে",
    negativeButtonText: String = "ক্যানসেল",
    listener: ((type: Int) -> Unit)? = null
): AlertDialog {

    val builder = MaterialAlertDialogBuilder(this)
    builder.setTitle(title)
    // Display a message on alert dialog
    builder.setMessage(message)
    // Set a positive button and its click listener on alert dialog
    builder.setPositiveButton(positiveButtonText) { dialog, which ->
        dialog.dismiss()
        listener?.invoke(AlertDialog.BUTTON_POSITIVE)

    }
    // Display a negative button on alert dialog
    if (showCancel) {
        builder.setNegativeButton(negativeButtonText) { dialog, which ->
            dialog.dismiss()
            listener?.invoke(AlertDialog.BUTTON_NEGATIVE)

        }
    }


    val dialog = builder.create()
    val typeface = ResourcesCompat.getFont(this, R.font.roboto)
    val textView = dialog.findViewById<TextView>(android.R.id.message)
    textView?.typeface = typeface
    return dialog
}

fun Fragment.alert(
    title: CharSequence? = null,
    message: CharSequence? = null,
    showCancel: Boolean = false,
    positiveButtonText: String = "ঠিক আছে",
    negativeButtonText: String = "ক্যানসেল",
    listener: ((type: Int) -> Unit)? = null
): AlertDialog {

    val builder = MaterialAlertDialogBuilder(requireContext())
    builder.setTitle(title)
    // Display a message on alert dialog
    builder.setMessage(message)
    // Set a positive button and its click listener on alert dialog
    builder.setPositiveButton(positiveButtonText) { dialog, which ->
        dialog.dismiss()
        listener?.invoke(AlertDialog.BUTTON_POSITIVE)
    }
    // Display a negative button on alert dialog
    if (showCancel) {
        builder.setNegativeButton(negativeButtonText) { dialog, which ->
            dialog.dismiss()
            listener?.invoke(AlertDialog.BUTTON_NEGATIVE)
        }
    }

    val dialog = builder.create()
    val typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto)
    val textView = dialog.findViewById<TextView>(android.R.id.message)
    textView?.typeface = typeface
    return dialog
}

fun Context.alertAd(
    title: CharSequence? = null,
    message: CharSequence? = null,
    showCancel: Boolean = false,
    positiveButtonText: String = "ঠিক আছে",
    negativeButtonText: String = "ক্যানসেল",
    listener: ((type: Int) -> Unit)? = null
): AlertDialog {

    val builder = MaterialAlertDialogBuilder(this)
    builder.setTitle(title)
    // Display a message on alert dialog
    builder.setMessage(message)
    // Set a positive button and its click listener on alert dialog
    builder.setPositiveButton(positiveButtonText) { dialog, which ->
        dialog.dismiss()
        listener?.invoke(AlertDialog.BUTTON_POSITIVE)
    }
    // Display a negative button on alert dialog
    if (showCancel) {
        builder.setNegativeButton(negativeButtonText) { dialog, which ->
            dialog.dismiss()
            listener?.invoke(AlertDialog.BUTTON_NEGATIVE)
        }
    }

    val dialog = builder.create()
    val typeface = ResourcesCompat.getFont(this, R.font.roboto)
    val textView = dialog.findViewById<TextView>(android.R.id.message)
    textView?.typeface = typeface
    return dialog
}


fun Activity.progressDialog(message: String = getString(R.string.loading_checkorder_process)): ProgressDialog {

    val dialog = ProgressDialog(this)
    with(dialog) {
        setMessage(message)
    }
    return dialog
}

val <T> T.exhaustive: T
    get() = this

fun Fragment.progressDialog(message: String = getString(R.string.loading_checkorder_process)): ProgressDialog {

    val dialog = ProgressDialog(requireContext())
    with(dialog) {
        setMessage(message)
    }
    return dialog
}

fun View.snackbar(message: String, length: Int = Snackbar.LENGTH_INDEFINITE){
    Snackbar.make(this, message, length).also { snackbar ->
        snackbar.setAction("ঠিক আছে") {
            snackbar.dismiss()
        }
    }.show()
}

fun View.snackbar(message: String, length: Int = Snackbar.LENGTH_INDEFINITE, actionName: String, onClick: ((view: View) -> Unit)? = null): Snackbar {
    return Snackbar.make(this, message, length).also { snackbar ->
        snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines = 5
        snackbar.setActionTextColor(Color.YELLOW)
        snackbar.setAction(actionName) {
            onClick?.invoke(it)
            snackbar.dismiss()
        }
    }
}

fun isEnglishLetterOnly(text: String): Boolean {

    val match = "[a-zA-Z0-9 ]*".toRegex()
    return text.matches(match)
}

fun Context.dpToPx(value: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, this.resources.displayMetrics).toInt()
}

fun Bundle.bundleToString(): String {
    return this.keySet().joinToString(", ", "{", "}") { key ->
        "$key=${this[key]}"
    }
}

fun generateNameInitial(name: String?): String {
    if (name.isNullOrEmpty()) return ""
    var initial: String = ""
    val array = name.trim().split(" ")
    array.forEach {
        if (it.isNotEmpty()) {
            initial += it[0]
        }
    }
    return initial
}

fun isValidPhone(phone: String?): Boolean {
    if (phone == null) return false

    if (phone.isDigitsOnly() && phone.length == 11 && phone.startsWith("01", true)) {
        return true
    }
    return false
}

fun isValidEmail(email: String): Boolean {
    return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun generateRandomNumber(limit: Int): Int {
    val random = Random(limit)
    return random.nextInt()
}

fun getFileContentType(filePath: String): String? {
    val file = File(filePath)
    val map = MimeTypeMap.getSingleton()
    val ext = MimeTypeMap.getFileExtensionFromUrl(file.name)
    var type = map.getMimeTypeFromExtension(ext)
    if (type == null) type = "*/*"
    return type
}

fun Activity.appVersion(): String {
    return try {
        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        pInfo.versionName
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun Fragment.appVersion(): String {
    return try {
        val pInfo: PackageInfo? = this.context?.packageManager?.getPackageInfo(this.context?.packageName ?: "", 0)
        pInfo?.versionName ?: ""
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun Activity.appVersionCode(): Int {
    return try {
        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        pInfo.versionCode
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }
}

fun Fragment.appVersionCode(): Int {
    return try {
        val pInfo: PackageInfo? = this.context?.packageManager?.getPackageInfo(this.context?.packageName ?: "", 0)
        pInfo?.versionCode ?: 0
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }
}

fun ViewPager2.reduceDragSensitivity() {
    val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
    recyclerViewField.isAccessible = true
    val recyclerView = recyclerViewField.get(this) as RecyclerView

    val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
    touchSlopField.isAccessible = true
    val touchSlop = touchSlopField.get(recyclerView) as Int
    touchSlopField.set(recyclerView, touchSlop * 4)       // sensitivity
}