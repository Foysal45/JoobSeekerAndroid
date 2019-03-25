package com.bdjobs.app.Employers

import android.app.Fragment
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.MessageDetailModel
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.d
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import kotlinx.android.synthetic.main.fragment_employer_message_detail.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EmployerMessageDetailFragment : Fragment() {

    private var toogleStatus = false
    private var employersCommunicator: EmployersCommunicator? = null
    lateinit var bdjobsUserSession: BdjobsUserSession
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_employer_message_detail, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        employersCommunicator = activity as EmployersCommunicator
        bdjobsUserSession = BdjobsUserSession(activity)
        d("messageId ${employersCommunicator!!.getMessageId()}")

        onClick()
        getDataFromServer()


    }


    private fun onClick() {

        expand_arrow.onClick {

            if (toogleStatus) {
                toogleStatus = false
                expand_arrow.setImageResource(R.drawable.ic_arrow_down)
                positionLayout.hide()
                dateLayout.hide()

            } else {
                toogleStatus = true
                expand_arrow.setImageResource(R.drawable.ic_arrow_up)
                positionLayout.show()
                dateLayout.show()
            }


        }


       messageDetailBackIMV.onClick {

           employersCommunicator?.backButtonPressed()

       }

    }

    private fun getDataFromServer() {
        try {

            shimmer_view_container_employerMessageDetail?.show()
            shimmer_view_container_employerMessageDetail?.startShimmerAnimation()


            ApiServiceMyBdjobs.create().getMessageDetail(bdjobsUserSession.userId, bdjobsUserSession.decodId, employersCommunicator!!.getMessageId()
            ).enqueue(object : Callback<MessageDetailModel> {
                override fun onFailure(call: Call<MessageDetailModel>, t: Throwable) {
                    try {
                        activity?.toast("${t.message}")
                        shimmer_view_container_employerMessageDetail?.hide()
                        shimmer_view_container_employerMessageDetail?.stopShimmerAnimation()
                    } catch (e: Exception) {
                        logException(e)
                    }
                }

                override fun onResponse(call: Call<MessageDetailModel>, response: Response<MessageDetailModel>) {

                    d("fdjkghfjkg onResponse called")




                    try {

                        if (!response.body()?.data.isNullOrEmpty()) {

                            subjectTV.text = response?.body()?.data!![0]!!.subject

                            val from = Html.fromHtml(response?.body()?.data?.get(0)?.from)

                            fromTV.text = from
                            positionTV.text = response?.body()!!.data!![0]!!.jobtitle
                            messageDateTV.text = response?.body()!!.data!![0]!!.mailedon

                            val  messageBody = Html.fromHtml(response?.body()?.data?.get(0)?.msgBody)

                            messageBodyTV.text = messageBody
                            lastContentTV.text = response?.body()!!.data!![0]!!.lastcontent
                            shimmer_view_container_employerMessageDetail?.hide()
                            shimmer_view_container_employerMessageDetail?.stopShimmerAnimation()
                            linearLayoutMain.show()

                        } else {

                        }


                    } catch (e: Exception) {
                        logException(e)
                    }


                    shimmer_view_container_employerMessageDetail?.hide()
                    shimmer_view_container_employerMessageDetail?.stopShimmerAnimation()
                }

            })
        } catch (e: Exception) {
            logException(e)
        }


    }


    private  fun setViews (){


    }


}
