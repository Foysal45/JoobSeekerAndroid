package com.bdjobs.app.training.ui


import android.app.Application
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.training.data.models.TrainingList
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Web.WebActivity
import com.bdjobs.app.databinding.FragmentUpcomingTrainingBinding
import com.bdjobs.app.training.TrainingCommunicator
import com.bdjobs.app.training.data.repository.TrainingRepository
import kotlinx.android.synthetic.main.fragment_upcoming_training.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpcomingTrainingFragment : Fragment() {

    private lateinit var mBinding: FragmentUpcomingTrainingBinding
    private val mViewModel: UpcomingTrainingViewModel by viewModels {
        UpcomingTrainingViewModelFactory(TrainingRepository(requireActivity().application as Application))
    }

    private lateinit var bdJobsUserSession: BdjobsUserSession

    private val mTrainingListAdapter: TrainingListAdapter by lazy {
        TrainingListAdapter {
            startActivity<WebActivity>(
                "from" to "training",
                "url" to "https://bdjobstraining.com/trainingdetails.asp?" + it.detailurl
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        mBinding = FragmentUpcomingTrainingBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = mViewModel
        }

        bdJobsUserSession = BdjobsUserSession(requireContext())

        return mBinding.root
    }


    override fun onResume() {
        super.onResume()

        setUpObserver()
        setUpRecyclerview()

    }

    private fun setUpRecyclerview() {
        mBinding.trainListRV.apply {
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
            setHasFixedSize(true)
            adapter = mTrainingListAdapter
        }
    }

    private fun setUpObserver() {
        mViewModel.apply {
            if (Constants.matchedTraining) {
                fetchTrainingList("")
            } else {
                fetchTrainingList(bdJobsUserSession.trainingId!!)
            }

            allSelected.observe(viewLifecycleOwner,{
                if (it) {
                    fetchTrainingList(bdJobsUserSession.trainingId!!)
                }
            })

            suggestedSelected.observe(viewLifecycleOwner,{
                if (it) {
                    fetchTrainingList("")
                }
            })

            trainingInfo.observe(viewLifecycleOwner,{
                when(it.status) {
                    Status.SUCCESS -> {
                        if (it.data?.data!=null && it.data.data.isNotEmpty()) {
                            mBinding.numberTV.text = it.data.data.size.toString()
                            mTrainingListAdapter.currentList.clear()
                            mTrainingListAdapter.submitList(it.data.data)
                        } else {
                            mTrainingListAdapter.submitList(emptyList())
                        }
                    }
                    Status.ERROR -> {
                        showSnackBar(mBinding.root,it.message?:"Something went wrong")
                    }
                    Status.LOADING -> {}
                }
            })
        }
    }

}
