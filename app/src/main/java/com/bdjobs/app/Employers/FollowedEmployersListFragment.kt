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
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.FollowedEmployer

import com.bdjobs.app.R
import kotlinx.android.synthetic.main.fragment_followed_employers_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FollowedEmployersListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FollowedEmployersListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FollowedEmployersListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var bdjobsDB: BdjobsDB
    private var followedEmployersAdapter: FollowedEmployersAdapter? = null
    lateinit var employersCommunicator: EmployersCommunicator
    private var layoutManager: androidx.recyclerview.widget.LinearLayoutManager? = null

    private var followedEmployerList: List<FollowedEmployer>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_followed_employers_list, container, false)
    }

    @SuppressLint("WrongConstant")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        employersCommunicator = activity as EmployersCommunicator
        bdjobsDB = BdjobsDB.getInstance(activity)

        backIMV.setOnClickListener {
            employersCommunicator.backButtonPressed()
        }


        doAsync {
            followedEmployerList = bdjobsDB.followedEmployerDao().getAllFollowedEmployer()
            val followedEmployerJobCount = bdjobsDB.followedEmployerDao().getJobCountOfFollowedEmployer()
            Log.d("follow", followedEmployerList.toString())
            uiThread {

                followedEmployersAdapter = FollowedEmployersAdapter(activity!!)
                followedRV!!.adapter = followedEmployersAdapter
                followedRV!!.setHasFixedSize(true)
                followedRV?.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
                Log.d("initPag", "called")
                followedRV?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
                followedEmployersAdapter?.addAll(followedEmployerList!!)

                val styledText = "<b><font color='#13A10E'>${followedEmployerJobCount}</font></b> Followed Employers"
                favCountTV.text = Html.fromHtml(styledText)
            }
        }
    }



}
