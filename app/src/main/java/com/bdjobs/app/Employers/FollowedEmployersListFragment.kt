package com.bdjobs.app.Employers

import android.app.Fragment
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.FollowedEmployer
import com.bdjobs.app.R
import kotlinx.android.synthetic.main.fragment_followed_employers_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*


class FollowedEmployersListFragment : Fragment() {
    private lateinit var bdjobsDB: BdjobsDB
    private var followedEmployersAdapter: FollowedEmployersAdapter? = null
    lateinit var employersCommunicator: EmployersCommunicator
    private var layoutManager: androidx.recyclerview.widget.LinearLayoutManager? = null
    private lateinit var isActivityDate: String
    var followedListSize = 0
    private var followedEmployerList: List<FollowedEmployer>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        //let do pushing
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
            followedEmployerList = if (isActivityDate == "0") {
                bdjobsDB.followedEmployerDao().getAllFollowedEmployer()
            } else {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -30)
                val lastmonth = calendar.time
                bdjobsDB.followedEmployerDao().getFollowedEmployerbyDate(lastmonth)

            }

            Log.d("follow", followedEmployerList.toString())
            uiThread {
                followedListSize = followedEmployerList?.size!!
                followedEmployersAdapter = FollowedEmployersAdapter(activity)
                followedRV!!.adapter = followedEmployersAdapter
                followedRV!!.setHasFixedSize(true)
                followedRV?.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                Log.d("initPag", "called")
                followedRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
                followedEmployersAdapter?.addAll(followedEmployerList!!)

          /*      val styledText = "<b><font color='#13A10E'>${followedEmployerList?.size}</font></b> Followed Employer(s)"
                favCountTV?.text = Html.fromHtml(styledText)*/

                if (followedEmployerList?.size!! > 1) {
                    val styledText = "<b><font color='#13A10E'>${followedEmployerList?.size}</font></b> Followed Employers"
                    favCountTV?.text = Html.fromHtml(styledText)
                } else {
                    val styledText = "<b><font color='#13A10E'>${followedEmployerList?.size}</font></b> Followed Employer"
                    favCountTV?.text = Html.fromHtml(styledText)
                }


            }
        }
    }

  /*  fun scrollToUndoPosition(position:Int){
        followedRV?.scrollToPosition(position)
        followedListSize++
        val styledText = "<b><font color='#13A10E'>$followedListSize</font></b> Followed Employer(s)"
        favCountTV?.text = Html.fromHtml(styledText)
    }

    fun decrementCounter(){
        followedListSize--
        val styledText = "<b><font color='#13A10E'>$followedListSize</font></b> Followed Employer(s)"
        favCountTV?.text = Html.fromHtml(styledText)
    }*/

    fun scrollToUndoPosition(position:Int){
        followedRV?.scrollToPosition(position)
        followedListSize++
        if (followedListSize> 1) {
            val styledText = "<b><font color='#13A10E'>$followedListSize</font></b> Followed Employers"
            favCountTV?.text = Html.fromHtml(styledText)
        } else {
            val styledText = "<b><font color='#13A10E'>$followedListSize</font></b> Followed Employer"
            favCountTV?.text = Html.fromHtml(styledText)
        }

    }

    fun decrementCounter(){
        followedListSize--
        if (followedListSize> 1) {
            val styledText = "<b><font color='#13A10E'>$followedListSize</font></b> Followed Employers"
            favCountTV?.text = Html.fromHtml(styledText)
        } else {
            val styledText = "<b><font color='#13A10E'>$followedListSize</font></b> Followed Employer"
            favCountTV?.text = Html.fromHtml(styledText)
        }
    }


}
