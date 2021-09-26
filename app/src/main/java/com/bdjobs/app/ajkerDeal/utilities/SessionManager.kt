package com.bdjobs.app.ajkerDeal.utilities

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.NonNull
import androidx.core.content.edit

object SessionManager {

    private const val prefName = "com.bdjobs.app.ajkerdeal.session"
    private lateinit var pref: SharedPreferences

    fun init(@NonNull context: Context) {
        pref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    }


    var isLogin: Boolean
        get() {
            return pref.getBoolean("isLogin", false)
        }
        set(value) {
            pref.edit {
                putBoolean("isLogin", value)
            }
        }

    var userId: Int
        get() {
            return pref.getInt("userId", 0)
        }
        set(value) {
            pref.edit {
                putInt("userId", value)
            }
        }

    var dtUserId: Int
        get() {
            return pref.getInt("dtUserId", 0)
        }
        set(value) {
            pref.edit {
                putInt("dtUserId", value)
            }
        }

    var adminType: Int
        get() {
            return pref.getInt("adminType", 0)
        }
        set(value) {
            pref.edit {
                putInt("adminType", value)
            }
        }

    var userName: String
        get() {
            return pref.getString("username", "")!!
        }
        set(value) {
            pref.edit {
                putString("username", value)
            }
        }

    var workFrom: String
        get() {
            return pref.getString("workFrom", "")!!
        }
        set(value) {
            pref.edit {
                putString("workFrom", value)
            }
        }


    var userFullName: String
        get() {
            return pref.getString("fullName", "")!!
        }
        set(value) {
            pref.edit {
                putString("fullName", value)
            }
        }

    var department: String
        get() {
            return pref.getString("department", "")!!
        }
        set(value) {
            pref.edit {
                putString("department", value)
            }
        }

    var mobile: String
        get() {
            return pref.getString("mobile", "")!!
        }
        set(value) {
            pref.edit {
                putString("mobile", value)
            }
        }

    var email: String
        get() {
            return pref.getString("email", "")!!
        }
        set(value) {
            pref.edit {
                putString("email", value)
            }
        }

    var gender: String
        get() {
            return pref.getString("gender", "")!!
        }
        set(value) {
            pref.edit {
                putString("gender", value)
            }
        }

    var address: String
        get() {
            return pref.getString("address", "")!!
        }
        set(value) {
            pref.edit {
                putString("address", value)
            }
        }

    var blood: String
        get() {
            return pref.getString("blood", "")!!
        }
        set(value) {
            pref.edit {
                putString("blood", value)
            }
        }

    fun clearSession() {
        pref.edit {
            clear()
        }
    }

    var profileImage: String
        get() {
            return pref.getString("profileImage", "https://static.ajkerdeal.com/images/admin_users/dt/$dtUserId.jpg")!!
        }
        set(value) {
            pref.edit {
                putString("profileImage", value)
            }
        }

    var firebaseToken: String
        get() {
            return pref.getString("firebaseToken", "")!!
        }
        set(value) {
            pref.edit {
                putString("firebaseToken", value)
            }
        }

    var deviceId: String
        get() {
            return pref.getString("deviceId", "")!!
        }
        set(value) {
            pref.edit {
                putString("deviceId", value)
            }
        }

    var isPresent: Boolean
        get() {
            return pref.getBoolean("isPresent", false)
        }
        set(value) {
            pref.edit {
                putBoolean("isPresent", value)
            }
        }

    var latitude: String
        get() {
            return pref.getString("latitude", "0.0")!!
        }
        set(value) {
            pref.edit {
                putString("latitude", value)
            }
        }

    var longitude: String
        get() {
            return pref.getString("longitude", "0.0")!!
        }
        set(value) {
            pref.edit {
                putString("longitude", value)
            }
        }

    var locationAddress: String
        get() {
            return pref.getString("locationAddress", "")!!
        }
        set(value) {
            pref.edit {
                putString("locationAddress", value)
            }
        }

    var isRemainder: Boolean
        get() {
            return pref.getBoolean("isRemainder", false)
        }
        set(value) {
            pref.edit {
                putBoolean("isRemainder", value)
            }
        }

    var remainderTime: String
        get() {
            return pref.getString("remainderTime", "")!!
        }
        set(value) {
            pref.edit {
                putString("remainderTime", value)
            }
        }

    var sessionId: String
        get() {
            return pref.getString("sessionId", "0")!!
        }
        set(value) {
            pref.edit {
                putString("sessionId", value)
            }
        }

    var isLocationConsentShown: Boolean
        get() {
            return pref.getBoolean("locationConsent", false)
        }
        set(value) {
            pref.edit {
                putBoolean("locationConsent", value)
            }
        }

    var calledMerchantIndex: Int
        get() {
            return pref.getInt("isCalledMerchant", 0)
        }
        set(value) {
            pref.edit {
                putInt("isCalledMerchant", value)
            }
        }

    var selectedVideoQuality: Int
        get() {
            return pref.getInt("selectedVideoQuality", -1)
        }
        set(value) {
            pref.edit {
                putInt("selectedVideoQuality", value)
            }
        }

//    var isLoggedIn: Boolean
//        get() {
//            return pref.getBoolean("IsLoggedIn", false)
//        }
//        set(value) {
//            pref.edit {
//                putBoolean("IsLoggedIn", value)
//            }
//        }

    var customerBlock: Boolean
        get() {
            return pref.getBoolean("IsCustomerBlock", false)
        }
        set(value) {
            pref.edit {
                putBoolean("IsCustomerBlock", value)
            }
        }

    var mobileNumber: String?
        get() {
            return pref.getString("phoneKey", "")
        }
        set(value) {
            pref.edit {
                putString("phoneKey", value)
            }
        }

    var deliveryAddressBook: String?
        get() {
            return pref.getString("addressBook", "[]")
        }
        set(value) {
            pref.edit {
                putString("addressBook", value)
            }
        }
}