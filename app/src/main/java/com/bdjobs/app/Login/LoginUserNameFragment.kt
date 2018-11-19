package com.bdjobs.app.Login

import android.app.Fragment
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.LoginSessionModel
import com.bdjobs.app.API.ModelClasses.LoginUserModel
import com.bdjobs.app.API.ModelClasses.SocialLoginAccountListModel
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.FACEBOOK_GRAPH_REQUEST_PERMISSION_KEY
import com.bdjobs.app.Utilities.Constants.Companion.FACEBOOK_GRAPH_REQUEST_PERMISSION_STRING
import com.bdjobs.app.Utilities.Constants.Companion.FB_KEY_EMAIL
import com.bdjobs.app.Utilities.Constants.Companion.FB_KEY_ID
import com.bdjobs.app.Utilities.Constants.Companion.LINKEDIN_REQUEST_URL
import com.bdjobs.app.Utilities.Constants.Companion.RC_SIGN_IN
import com.bdjobs.app.Utilities.Constants.Companion.SOCIAL_MEDIA_FACEBOOK
import com.bdjobs.app.Utilities.Constants.Companion.SOCIAL_MEDIA_GOOGLE
import com.bdjobs.app.Utilities.Constants.Companion.SOCIAL_MEDIA_LINKEDIN
import com.bdjobs.app.Utilities.Constants.Companion.api_request_result_code_ok
import com.bdjobs.app.Utilities.Constants.Companion.key_false
import com.bdjobs.app.Utilities.Constants.Companion.key_true
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.linkedin.platform.APIHelper
import com.linkedin.platform.LISessionManager
import com.linkedin.platform.errors.LIApiError
import com.linkedin.platform.errors.LIAuthError
import com.linkedin.platform.listeners.ApiListener
import com.linkedin.platform.listeners.ApiResponse
import com.linkedin.platform.listeners.AuthListener
import com.linkedin.platform.utils.Scope
import kotlinx.android.synthetic.main.fragment_login_username.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.regex.Pattern


class LoginUserNameFragment : Fragment() {

    private lateinit var loginCommunicator: LoginCommunicator
    private lateinit var symbol: String
    private lateinit var rootView: View

    //for google sign in
    private var mGoogleSignInClient: GoogleApiClient? = null

    //for facebook sign in
    private var callbackManager: CallbackManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater?.inflate(R.layout.fragment_login_username, container, false)!!
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        loginCommunicator = activity as LoginCommunicator
        initializeGoogleSignIN()
        initializeFacebookSignIN()
        onClicks()
    }

    private fun initializeFacebookSignIN() {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {

                        val request = GraphRequest.newMeRequest(loginResult.accessToken) { profileData, response ->
                            Log.d("LoginActivity", response.toString())
                            try {
                                var semail: String? = null
                                var sMid: String? = null

                                try {
                                    if (profileData.has(FB_KEY_EMAIL)) {
                                        semail = profileData.getString(FB_KEY_EMAIL)
                                    }
                                    if (profileData.has(FB_KEY_ID)) {
                                        sMid = profileData.getString(FB_KEY_ID)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }



                                signOutFromFacebook()
                                socialMediaMapping(sMid, semail, SOCIAL_MEDIA_FACEBOOK)


                                Log.d("FacebookSignIN", "sid:$sMid \n semial:$semail")

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        val parameters = Bundle()
                        parameters.putString(FACEBOOK_GRAPH_REQUEST_PERMISSION_KEY, FACEBOOK_GRAPH_REQUEST_PERMISSION_STRING)
                        request.parameters = parameters
                        request.executeAsync()
                    }

                    override fun onCancel() {
                        toast("Please sign in to facebook first to complete your sign in by facebook")
                    }

                    override fun onError(exception: FacebookException) {
                        logException(exception)

                        toast(exception.toString())

                    }
                })
    }

    private fun socialMediaMapping(sMid: String?, semail: String?, sType: String?) {

        Log.d("socialMediaMapping", "sMid:$sMid \n semail:$semail  \n sType: $sType")

        activity.showProgressBar(loadingProgressBar)

        ApiServiceMyBdjobs.create().getSocialAccountList(SocialMediaId = sMid, email = semail, MediaName = sType).enqueue(object : Callback<SocialLoginAccountListModel> {
            override fun onFailure(call: Call<SocialLoginAccountListModel>, t: Throwable) {
                activity.stopProgressBar(loadingProgressBar)
                error("onFailure", t)

            }

            override fun onResponse(call: Call<SocialLoginAccountListModel>, response: Response<SocialLoginAccountListModel>) {
                activity.stopProgressBar(loadingProgressBar)
                if (response?.body()?.statuscode!!.equalIgnoreCase(api_request_result_code_ok))
                {

                    var mappedAccountNumber: Int? = null
                    var hasMappedAccount = false

                    response.body()?.data?.let {dataList->
                        for ((index, value) in dataList.withIndex()) {
                            if (value?.isMap?.equalIgnoreCase(key_true)!!) {
                                mappedAccountNumber = index
                                hasMappedAccount = true
                            }
                        }

                    }

                    Log.d("mappedAccountNumber", "mappedAccountNumber: $mappedAccountNumber")

                    if(hasMappedAccount){
                        val mappedAccount = response.body()?.data?.get(mappedAccountNumber!!)
                        ApiServiceMyBdjobs.create().doLogin(socialMediaId = mappedAccount?.socialMediaId,
                                socialMediaName = mappedAccount?.socialMediaName,
                                susername = mappedAccount?.susername,
                                email = mappedAccount?.email,
                                fullName = mappedAccount?.fullName,
                                userId = mappedAccount?.userId,
                                isMap = mappedAccount?.isMap,
                                smId = mappedAccount?.smId
                        ).enqueue(object : Callback<LoginSessionModel> {

                            override fun onFailure(call: Call<LoginSessionModel>, t: Throwable) {
                                error("onFailure", t)
                            }

                            override fun onResponse(call: Call<LoginSessionModel>, response: Response<LoginSessionModel>) {

                                if (response.body()?.statuscode!!.equalIgnoreCase(api_request_result_code_ok)) {

                                    response.body()?.data?.get(0)?.let { sessionData ->
                                        val bdjobsUserSession = BdjobsUserSession(activity)
                                        bdjobsUserSession.createSession(sessionData)
                                    }

                                } else {
                                    toast(response.body()?.message!!)
                                }
                            }
                        })
                    }
                    else if (!hasMappedAccount) {
                        response.body()?.common?.total?.let { total ->
                        Log.d("mappedAccountNumber", "totalNumberofUnmappedAccounts: $total")
                            try {
                                if ( total.toInt() > 0) {
                                    loginCommunicator.goToSocialAccountListFragment(response.body()?.data)
                                }
                            } catch (e: Exception) {
                                logException(e)
                            }
                        }
                    }
                }
                else {
                    toast(response.body()?.message!!)
                }

            }

        })


    }

    private fun initializeGoogleSignIN() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleApiClient.Builder(activity)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }

    private fun onClicks() {
        backBtnIMGV.setOnClickListener {
            loginCommunicator?.backButtonClicked()
        }

        nextButtonFAB.setOnClickListener {
            val userName = usernameTIET.getString()
            if (validateUserName(userName)) {
                checkUserHasAccount(userName)
            }
        }

        usernameTIET.easyOnTextChangedListener { charSequence ->
            validateUserName(charSequence.toString())
        }

        googleSignInIMGV.setOnClickListener {
            signInWithGoogle()
        }

        facebookLoginIMGV.setOnClickListener {
            signInWithFacebook()
        }

        linkedInSignInIMGV.setOnClickListener {
            signInWithLinkedIn()
        }



        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            try {
                val r = Rect()
                rootView.getWindowVisibleDisplayFrame(r)
                val heightDiff = rootView.rootView.height - (r.bottom - r.top)

                if (heightDiff > 200) { // if more than 100 pixels, its probably a keyboard...
                    footerIMGV.hide()
                } else {
                    //ok now we know the keyboard is down...
                    footerIMGV.show()

                }
            } catch (e: Exception) {
                logException(e)
            }
        }
    }

    private fun buildScope(): Scope {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS)
    }

    private fun signInWithLinkedIn() {

        LISessionManager.getInstance(activity.applicationContext).init(activity, buildScope(), object : AuthListener {

            override fun onAuthSuccess() {

                val apiHelper = APIHelper.getInstance(activity)

                apiHelper.getRequest(activity, LINKEDIN_REQUEST_URL, object : ApiListener {

                    override fun onApiSuccess(result: ApiResponse) {
                        val response = result.responseDataAsJson
                        try {
                            var lemail = ""
                            var sMid = ""
                            if (response.has("emailAddress")) {
                                lemail = response.getString("emailAddress")
                            }


                            if (response.has("id")) {
                                sMid = response.getString("id")
                            }

                            socialMediaMapping(sMid, lemail, SOCIAL_MEDIA_LINKEDIN)
                            Log.d("signInWithLinkedIn", "sMid:$sMid \n lemail: $lemail")


                        } catch (e: Exception) {
                            logException(e)

                            e.printStackTrace()
                            toast(e.toString())
                            toast("Some internal errors have been occurred,please try again later!")
                        }


                    }

                    override fun onApiError(error: LIApiError) {
                        logException(error)
                        toast(error.toString())

                    }
                })


            }

            override fun onAuthError(error: LIAuthError) {
                error(error)
                toast(error.toString())

            }
        }, true)


    }

    private fun signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this@LoginUserNameFragment, Arrays.asList("public_profile", "email"))
    }

    private fun signInWithGoogle() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        LISessionManager.getInstance(activity.applicationContext).onActivityResult(activity, requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // Google Sign In was successful, authenticate with Firebase
                val account = result.signInAccount
                val sid = account?.id
                val semial = account?.email
                val name = account?.displayName
                Log.d("GoogleSignIn", "sid:$sid \n semial:$semial  \n sname: $name")
                signOutFromGoogle()
                socialMediaMapping(sid, semial, SOCIAL_MEDIA_GOOGLE)

            } else {
                // Google Sign In failed, update UI appropriately
                toast("Please sign in to google first to complete your sign in by goole")
            }
        }

    }

    override fun onStart() {
        super.onStart()
        mGoogleSignInClient?.connect()
    }

    override fun onStop() {
        if (mGoogleSignInClient?.isConnected!!) {
            mGoogleSignInClient?.disconnect()
        }
        super.onStop()
    }

    private fun signOutFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
        val request = GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, GraphRequest.Callback { LoginManager.getInstance().logOut() }).executeAsync()
    }

    private fun signOutFromGoogle() {
        if (mGoogleSignInClient?.isConnected!!) {
            Auth.GoogleSignInApi.signOut(mGoogleSignInClient)
            mGoogleSignInClient?.disconnect()
            mGoogleSignInClient?.connect()
        }
    }

    private fun checkUserHasAccount(userName: String) {
        activity.showProgressBar(loadingProgressBar)
        ApiServiceMyBdjobs.create().getLoginUserDetails(userName).enqueue(object : Callback<LoginUserModel> {

            override fun onFailure(call: Call<LoginUserModel>?, t: Throwable?) {
                activity.stopProgressBar(loadingProgressBar)
            }

            override fun onResponse(call: Call<LoginUserModel>?, response: Response<LoginUserModel>?) {
                activity.stopProgressBar(loadingProgressBar)
                if (response?.body()?.statuscode == api_request_result_code_ok) {
                    useNameTIL.hideError()
                    if (response?.body()?.data?.get(0)?.isBlueCollar?.equalIgnoreCase(key_true)!!) {
                        loginCommunicator.goToOtpFragment(userName, response?.body()?.data?.get(0)?.userId, response?.body()?.data?.get(0)?.userFullName, response?.body()?.data?.get(0)?.imageurl)
                    } else if (response?.body()?.data?.get(0)?.isBlueCollar?.equalIgnoreCase(key_false)!!) {
                        loginCommunicator.goToPasswordFragment(userName, response?.body()?.data?.get(0)?.userId, response?.body()?.data?.get(0)?.userFullName, response?.body()?.data?.get(0)?.imageurl)
                    }

                } else {
                    useNameTIL.showError(response?.body()?.message)
                    requestFocus(usernameTIET)
                }
            }
        })

    }

    private fun validateUserName(userName: String): Boolean {

        when {
            TextUtils.isEmpty(userName) -> {
                useNameTIL.showError(getString(R.string.field_empty_error_message_common))
                requestFocus(usernameTIET)
                return false
            }
            checkStringHasSymbol(userName) -> {
                useNameTIL.showError("Username can not contain $symbol")
                requestFocus(usernameTIET)
                return false
            }
            userName.trim { it <= ' ' }.length < 5 /*|| userName.trim { it <= ' ' }.length > 15*/ -> {
                useNameTIL.showError("Username is too short!")
                requestFocus(usernameTIET)
                return false
            }
            else -> useNameTIL.hideError()
        }
        return true
    }

    private fun checkStringHasSymbol(s: String): Boolean {
        val p = Pattern.compile("[:`!#$%&*()_ +/=;|'\"<>?{}\\[\\]~-]")
        for (i in 0 until s.length) {
            symbol = s[i].toString()
            val m = p.matcher(symbol)
            if (m.matches()) {
                return true
            }
        }
        return false
    }

    private fun requestFocus(view: View) {
        if (view.requestFocus()) {
            activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
    }

}