package com.bdjobs.app.editResume.employmentHistory.fragments


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.R
import com.bdjobs.app.editResume.adapters.EmpHistoryAdapter
import com.bdjobs.app.editResume.adapters.models.sampledata
import com.facebook.FacebookSdk.getApplicationContext
import kotlinx.android.synthetic.main.fragment_emp_history_view.*


class EmpHistoryViewFragment : Fragment() {
    private var adapter: EmpHistoryAdapter? = null
    private var arr: ArrayList<sampledata>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        populateData()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emp_history_view, container, false)

        //return inflater.inflate(R.layout.item_experiece_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRV()
    }

    private fun setupRV() {
        rv_eh_view.setHasFixedSize(true)
        val mLayoutManager = LinearLayoutManager(getApplicationContext())
        rv_eh_view.layoutManager = mLayoutManager
        adapter = EmpHistoryAdapter(arr!!, activity)
        rv_eh_view.itemAnimator = DefaultItemAnimator()
        rv_eh_view.adapter = adapter
    }

    private fun populateData() {
        var a = sampledata("asdfdasf", "sdafds", "asdffasdf", "adfs")
        arr?.add(a)
        a = sampledata("asdfdasf", "sdafds", "asdffasdf", "adfs")
        arr?.add(a)
        a = sampledata("asdfdasf", "sdafds", "asdffasdf", "adfs")
        arr?.add(a)
        a = sampledata("asdfdasf", "sdafds", "asdffasdf", "adfs")
        arr?.add(a)

        adapter?.notifyDataSetChanged()
    }
}
