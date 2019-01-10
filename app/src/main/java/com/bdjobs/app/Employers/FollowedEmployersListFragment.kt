package com.bdjobs.app.Employers

import android.annotation.SuppressLint
import android.os.Bundle
import android.app.Fragment
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.FollowedEmployer

import com.bdjobs.app.R
import kotlinx.android.synthetic.main.fragment_followed_employers_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class FollowedEmployersListFragment : Fragment() {
    private lateinit var bdjobsDB: BdjobsDB
    private var followedEmployersAdapter: FollowedEmployersAdapter? = null
    lateinit var employersCommunicator: EmployersCommunicator
    private var layoutManager: androidx.recyclerview.widget.LinearLayoutManager? = null
    private lateinit var isActivityDate: String

    private var followedEmployerList: List<FollowedEmployer>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_followed_employers_list, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        employersCommunicator = activity as EmployersCommunicator
        isActivityDate = employersCommunicator.getTime()
        bdjobsDB = BdjobsDB.getInstance(activity)

        backIMV.setOnClickListener {
            employersCommunicator?.backButtonPressed()
        }



        doAsync {
            if (isActivityDate == "0") {
                followedEmployerList = bdjobsDB.followedEmployerDao().getAllFollowedEmployer()
            } else {
                followedEmployerList = bdjobsDB.followedEmployerDao().getAllFollowedEmployer()
            }

            //   val followedEmployerJobCount = followedEmployerList?.size
            //  getJobCountOfFollowedEmployer()
            Log.d("follow", followedEmployerList.toString())
            uiThread {

                followedEmployersAdapter = FollowedEmployersAdapter(activity!!)
                followedRV!!.adapter = followedEmployersAdapter
                followedRV!!.setHasFixedSize(true)
                followedRV?.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                Log.d("initPag", "called")
                followedRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
                followedEmployersAdapter?.addAll(followedEmployerList!!)

                val styledText = "<b><font color='#13A10E'>${followedEmployerList?.size}</font></b> Followed Employers"
                favCountTV?.text = Html.fromHtml(styledText)
            }
        }
    }


}
