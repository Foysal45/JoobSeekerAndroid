package com.bdjobs.app.editResume.employmentHistory.fragments


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.editResume.adapters.EmpHistoryAdapter
import com.bdjobs.app.editResume.adapters.models.DataItem
import com.bdjobs.app.editResume.adapters.models.GetExps
import com.bdjobs.app.editResume.callbacks.EmpHisCB
import com.facebook.FacebookSdk.getApplicationContext
import kotlinx.android.synthetic.main.fragment_emp_history_view.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EmpHistoryViewFragment : Fragment() {
    private lateinit var call: EmpHisCB
    private var adapter: EmpHistoryAdapter? = null
    private var arr: ArrayList<DataItem>? = null
    private lateinit var session: BdjobsUserSession

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emp_history_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
        call = activity as EmpHisCB
        populateData()
        call.setDeleteButton(false)
    }

    private fun setupRV(items: ArrayList<DataItem>) {
        rv_eh_view.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(getApplicationContext())
        rv_eh_view.layoutManager = mLayoutManager
        adapter = EmpHistoryAdapter(items, activity)
        rv_eh_view.itemAnimator = DefaultItemAnimator()
        rv_eh_view.adapter = adapter
    }

    private fun populateData() {
        /*var a = sampledata("asdfdasf", "sdafds", "asdffasdf", "adfs")
        arr?.add(a)
        a = sampledata("asdfdasf", "sdafds", "asdffasdf", "adfs")
        arr?.add(a)
        a = sampledata("asdfdasf", "sdafds", "asdffasdf", "adfs")
        arr?.add(a)
        a = sampledata("asdfdasf", "sdafds", "asdffasdf", "adfs")
        arr?.add(a)*/
        loader_exps.show()
        val call = ApiServiceMyBdjobs.create().getExpsList(session.userId, session.decodId)
        call.enqueue(object : Callback<List<GetExps>> {
            override fun onFailure(call: Call<List<GetExps>>, t: Throwable) {
                activity.toast("Error occurred")
            }

            override fun onResponse(call: Call<List<GetExps>>, response: Response<List<GetExps>>) {
                try {
                    if (response.isSuccessful) {
                        loader_exps.hide()
                        val respo = response.body()?.get(0)
                        //activity.toast("${respo?.message}")
                        arr = respo?.data as ArrayList<DataItem>
                        activity.toast("${arr?.size}")
                        if (arr != null) {
                            //adapter?.addAll(arr!!)
                            setupRV(arr!!)
                        }
                    }
                } catch (e: Exception) {
                    loader_exps.hide()
                    activity.error("++${e.message}")
                }
                adapter?.notifyDataSetChanged()
            }
        })
    }
}
