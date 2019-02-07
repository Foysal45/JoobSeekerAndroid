package com.bdjobs.app.InviteCode.InviteCodeOwner

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.InviteCodeCategoryAmountModel
import com.bdjobs.app.API.ModelClasses.InviteCodeCategoryAmountModelData
import com.bdjobs.app.API.ModelClasses.OwnerInviteCodeModel
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.InviteCode.InviteCodeCommunicator
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.getBlueCollarUserId
import com.bdjobs.app.Utilities.logException
import kotlinx.android.synthetic.main.invite_code_owner_invite_code_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class OwnerInviteCodeFragment : Fragment() {
    private var dataStorage: DataStorage? = null
    private var promoCode = "------"
    private var bdjobsUserSession: BdjobsUserSession? = null
    private var inviteCodeCommunicator: InviteCodeCommunicator? = null
    private var categoryAmountList = ArrayList<InviteCodeCategoryAmountModelData>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.invite_code_owner_invite_code_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dataStorage = DataStorage(activity!!)
        bdjobsUserSession = BdjobsUserSession(activity!!)
        inviteCodeCommunicator = activity as InviteCodeCommunicator
        setupData()
        getInformation(bdjobsUserSession?.userId, bdjobsUserSession?.decodId, inviteCodeCommunicator?.getInviteCodepcOwnerID())
        onClicks()

    }

    private fun getInformation(userId: String?, decodId: String?, inviteCodepcOwnerID: String?) {

        ApiServiceMyBdjobs.create().getOwnerInviteCode(
                userID = userId,
                decodeID = decodId,
                ownerID = inviteCodepcOwnerID
        ).enqueue(object : Callback<OwnerInviteCodeModel> {
            override fun onFailure(call: Call<OwnerInviteCodeModel>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<OwnerInviteCodeModel>, response: Response<OwnerInviteCodeModel>) {

                try {
                    if (response?.body()?.statuscode == Constants.api_request_result_code_ok) {
                        promoCode = response.body()?.data?.get(0)?.inviteCode!!
                        inviteCodeTV.text = promoCode
                        shareIMGV.isEnabled = true
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }
        })

        ApiServiceMyBdjobs.create().getCategoryAmount(
                userID = userId,
                decodeID = decodId,
                pcOwnerId = inviteCodepcOwnerID
        ).enqueue(object : Callback<InviteCodeCategoryAmountModel> {
            override fun onFailure(call: Call<InviteCodeCategoryAmountModel>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<InviteCodeCategoryAmountModel>, response: Response<InviteCodeCategoryAmountModel>) {

                try {
                    if (response?.body()?.statuscode == Constants.api_request_result_code_ok) {
                        categoryAmountList.clear()
                        response.body()?.data?.let { dt ->
                            dt.forEach { dtt->
                                 categoryAmountList.add(dtt)
                             }
                        }
                        categoryRL.isEnabled = true
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }

        })
    }

    private fun showCategoryDialog() {

        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.invite_category_dialog_layout)

        val categoryLV = dialog.findViewById(R.id.categoryLV) as ListView
        val cancelIconImgv = dialog.findViewById(R.id.cancelIconImgv) as ImageView
        val invitecodeCategoryListAdapter = InvitecodeCategoryListAdapter(activity!!, categoryAmountList)
        categoryLV.setAdapter(invitecodeCategoryListAdapter)

        dialog.setCancelable(true)
        dialog.show()
        cancelIconImgv.setOnClickListener {
            try {
                dialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun onClicks() {
        shareIMGV.setOnClickListener {
            val data = "আপনার বিডিজবস প্রমো কোডটি হল $promoCode।এই  কোডটি ব্যবহার করে স্পেশাল স্কিলড ক্যাটাগরিগুলোতে অ্যাকাউন্ট খুললেই আপনি পেয়ে যাবেন নগদ ২০-৫০/- টাকা ।"
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, data)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }

        categoryRL.setOnClickListener {
            showCategoryDialog()
        }

    }

    private fun setupData() {
        val category = dataStorage?.getCategoryBanglaNameByID(activity?.getBlueCollarUserId().toString())
        val categeroryMsg = "<b>$category</b> সহ অন্যান্য পদে চাকরি খুঁজছেন এমন পরিচিত মানুষকে ইনভাইট করুন।"
        categeroryMsgTV.text = Html.fromHtml(categeroryMsg)
        inviteCodeTV.text = promoCode
        shareIMGV.isEnabled = false
        categoryRL.isEnabled = false
    }
}