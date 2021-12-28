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
import com.bdjobs.app.utilities.d
import com.bdjobs.app.utilities.hide
import com.bdjobs.app.utilities.logException
import com.bdjobs.app.utilities.show
import kotlinx.android.synthetic.main.fragment_employer_message_detail.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EmployerMessageDetailFragment : Fragment() {

    private var toogleStatus = false
    private var positionDataNullStatus = false
    private var employersCommunicator: EmployersCommunicator? = null
    lateinit var bdjobsUserSession: BdjobsUserSession


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
        positionDataNullStatus = false
        toogleStatus = false

        onClick()
        getDataFromServer()


    }


    private fun onClick() {

        expand_arrow.onClick {

            if (toogleStatus) {

                //Log.d("uuuuuuuu", "click  $toogleStatus")

                toogleStatus = false
                expand_arrow.setImageResource(R.drawable.ic_arrow_down)
                positionLayout.hide()
                dateLayout.hide()


            } else {

                //Log.d("uuuuuuuu", "click  $toogleStatus")

                toogleStatus = true
                expand_arrow.setImageResource(R.drawable.ic_arrow_up)

                if(!positionDataNullStatus){

                    positionLayout.show()

                } else {

                    positionLayout.hide()
                }

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
            shimmer_view_container_employerMessageDetail?.startShimmer()


            ApiServiceMyBdjobs.create().getMessageDetail(bdjobsUserSession.userId, bdjobsUserSession.decodId, employersCommunicator!!.getMessageId()
            ).enqueue(object : Callback<MessageDetailModel> {
                override fun onFailure(call: Call<MessageDetailModel>, t: Throwable) {
                    try {
                        activity?.toast("${t.message}")
                        shimmer_view_container_employerMessageDetail?.hide()
                        shimmer_view_container_employerMessageDetail?.stopShimmer()
                    } catch (e: Exception) {
                        logException(e)
                    }
                }

                override fun onResponse(call: Call<MessageDetailModel>, response: Response<MessageDetailModel>) {

                    try {



                        if (!response.body()?.data.isNullOrEmpty()) {

                            subjectTV?.text = response.body()?.data?.get(0)?.subject

                            val from = Html.fromHtml(response.body()?.data?.get(0)?.from)

                            fromTV?.text = from
                            d("jobtitle onResponse called jobtitle: ${response.body()?.data?.get(0)?.jobtitle}")




                            messageDateTV?.text = response.body()?.data?.get(0)?.mailedon

                            val  messageBody = Html.fromHtml(response.body()?.data?.get(0)?.msgBody)

                            messageBodyTV?.text = messageBody
                            lastContentTV?.text = response.body()!!.data!![0]!!.lastcontent
                            shimmer_view_container_employerMessageDetail?.hide()
                            shimmer_view_container_employerMessageDetail?.stopShimmer()
                            linearLayoutMain.show()
                            if (response.body()?.data?.get(0)?.jobtitle!!.trim() == ""){

                                //Log.d("uuuuuuuu", "dd  $toogleStatus")

                                positionDataNullStatus = true

                              if (!toogleStatus){

                                  positionLayout?.hide()


                              } else {
                                  positionLayout?.show()


                              }



                            } else {

                                //Log.d("uuuuuuuu", "dd fgjh $toogleStatus")
                                positionTV?.text = response.body()?.data?.get(0)?.jobtitle

                                positionDataNullStatus = false

                                if (!toogleStatus){

                                    positionLayout?.hide()

                                } else {

                                    positionLayout?.show()


                                }




                            }

                        } else {

                        }


                    } catch (e: Exception) {
                        logException(e)
                    }


                    shimmer_view_container_employerMessageDetail?.hide()
                    shimmer_view_container_employerMessageDetail?.stopShimmer()
                }

            })
        } catch (e: Exception) {
            logException(e)
        }


    }





}
