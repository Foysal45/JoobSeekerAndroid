package com.bdjobs.app.editResume.otherInfo.fragments.languagePref


import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.LanguageAdapter
import com.bdjobs.app.editResume.adapters.models.LanguageDataModel
import com.bdjobs.app.editResume.adapters.models.LanguageModel
import com.bdjobs.app.editResume.callbacks.OtherInfo
import kotlinx.android.synthetic.main.fragment_lang_pr_view.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LangPrViewFragment : Fragment() {

    private lateinit var eduCB: OtherInfo
    private var adapter: LanguageAdapter? = null
    private var arr: ArrayList<LanguageDataModel>? = null
    private lateinit var session: BdjobsUserSession
    var isEdit: Boolean = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        //------------------------------
        return inflater.inflate(R.layout.fragment_lang_pr_view, container, false)
    }

    override fun onResume() {
        super.onResume()
        session = BdjobsUserSession(activity)
        eduCB = activity as OtherInfo
        eduCB.setDeleteButton(false)
        try {
            if (eduCB.getBackFrom() == "") {
                if (eduCB.getLanguageList() != null) {
                    setupRV(eduCB.getLanguageList()!!) // add message if needed in the else part
                    if (eduCB.getLanguageList()!!.size > 2){
                        fab_language_add?.hide()
                    } else {
                        fab_language_add?.show()
                    }
                }
                ////Log.d("academic", "value : ->|${eduCB.getBackFrom()}| and ->|${eduCB.getAcademicList()?.size}|")

            } else {
                ////Log.d("academic1", "value : ->|${eduCB.getBackFrom()}|")
                doWork()
            }
        } catch (e: Exception) {
        }
        eduCB.setTitle(resources.getString(R.string.title_language))

        fab_language_add?.setOnClickListener {
            eduCB.goToEditInfo("addLanguage")
        }

    }


    private fun shimmerStart() {
        try {
            shimmer_view_container_langList?.show()
            shimmer_view_container_langList?.startShimmer()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

    private fun shimmerStop() {
        try {
            shimmer_view_container_langList?.hide()
            shimmer_view_container_langList?.stopShimmer()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }


    private fun doWork() {
        shimmerStart()
        populateData()
        eduCB.setBackFrom("")
    }

    private fun setupRV(items: ArrayList<LanguageDataModel>) {
        rv_lang_view.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(activity.applicationContext)
        rv_lang_view.layoutManager = mLayoutManager
        adapter = LanguageAdapter(items, activity)
        rv_lang_view.itemAnimator = DefaultItemAnimator()
        rv_lang_view.adapter = adapter
    }

    private fun populateData() {
        rv_lang_view?.hide()
        val call = ApiServiceMyBdjobs.create().getLanguageInfoList(session.userId, session.decodId)
        call.enqueue(object : Callback<LanguageModel> {
            override fun onFailure(call: Call<LanguageModel>, t: Throwable) {
                try {
                    shimmerStop()
                    rv_lang_view?.show()
                    activity?.toast("Error occurred")
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onResponse(call: Call<LanguageModel>, response: Response<LanguageModel>) {
                try {
                    if (response.isSuccessful) {
                        shimmerStop()
                        rv_lang_view?.show()
                        val respo = response.body()

                        //Log.d("dsfklhgjfd;h", "$respo")

                        arr = respo?.data as ArrayList<LanguageDataModel>

                        arr?.let {
                            eduCB.getLanguageList()?.clear()
                            eduCB.setLanguageList(arr!!)
                        }

                        try {
                            if (arr!!.size < 3) {
                                fab_language_add?.show()
                            } else{
                                fab_language_add?.hide()
                            }
                        } catch (e: Exception) {
                        }


                        //activity.toast("${arr?.size}")
                        if (arr != null) {
                            setupRV(arr!!)
                        }
                    }
                } catch (e: Exception) {
                    shimmerStop()

                    if (activity != null) {
                        /* activity.toast("${response.body()?.message}")*/
                        activity.logException(e)
                        activity.error("++${e.message}")
                        fab_language_add?.show()
                    }
                }
                adapter?.notifyDataSetChanged()
            }
        })
    }


}
