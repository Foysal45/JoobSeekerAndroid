package com.bdjobs.app.assessment


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bdjobs.app.R
import com.bdjobs.app.utilities.Constants
import com.bdjobs.app.utilities.openUrlInBrowser
import com.bdjobs.app.assessment.enums.Status
import com.bdjobs.app.assessment.viewmodels.HomeViewModel
import com.bdjobs.app.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.layout_need_more_information.view.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    lateinit var viewModel: HomeViewModel
    lateinit var binding: FragmentHomeBinding
    lateinit var snackbar: Snackbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        Log.d("rakib", "called onCreateView")

        binding = FragmentHomeBinding.inflate(inflater)

        viewModel = ViewModelProvider(requireNotNull(activity)).get(HomeViewModel::class.java)

        binding.lifecycleOwner = this.viewLifecycleOwner

        binding.homeViewModel = viewModel

        binding.whatIsEmployabilityCertificationCl.learnMoreBtn.setOnClickListener {
            Log.d("rakib","clicked")
            context?.openUrlInBrowser(Constants.url_assessment_help)
        }

        binding.whatIsEmployabilityCertificationFirstTimeCl.learnMoreBtn.setOnClickListener {
            context?.openUrlInBrowser(Constants.url_assessment_help)
        }

        binding.btnCl.setOnClickListener {
            it.findNavController().navigate(R.id.action_viewPagerFragment_to_modulesViewFragment)
        }

        binding.noPendingTest.takeNewTestBtn.setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_modulesViewFragment)
        }

        binding.assessmentInfo.changeBtn.setOnClickListener {
            Log.d("rakib", "${viewModel.homeData.value?.paymentStatus}")
            findNavController().navigate(R.id.action_viewPagerFragment_to_chooseScheduleFragment)
        }

        binding.assessmentInfo.cancelBtn.setOnClickListener {
            Log.d("rakib", "${viewModel.homeData.value?.resumeTestBtnFormat}")


            when (viewModel.homeData.value?.resumeTestBtnFormat) {
                "1" -> {
                    snackbar = Snackbar.make(binding.homeCl, getString(R.string.assessment_requirement_instruction), Snackbar.LENGTH_LONG)
                    snackbar.show()
                }
                "4" -> {
                    snackbar = Snackbar.make(binding.homeCl, "Action needs to complete from website", Snackbar.LENGTH_LONG)
                    snackbar.apply {
                        setAction("Go To Website") {
                            context.openUrlInBrowser(Constants.base_url_assessment_web)
                        }.show()
                    }
                }
            }

        }

        binding.startTest.startBtn.setOnClickListener {
            snackbar = Snackbar.make(binding.homeCl, getString(R.string.assessment_requirement_instruction), Snackbar.LENGTH_LONG)
            snackbar.show()

        }

        binding.needMoreInfoFirstTimeCl.call_tv.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:01844519336")
            startActivity(intent)
        }

        binding.needMoreInfoFirstTimeCl.call_helpline_tv.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:16479")
            startActivity(intent)
        }

        binding.needMoreInfoCl.call_tv.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:01844519336")
            startActivity(intent)
        }

        binding.needMoreInfoCl.call_helpline_tv.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:16479")
            startActivity(intent)
        }


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.takeIf { it.containsKey("status") }?.apply {
            //Log.d("rakibe in home", getString("status"))
            if (getString("status").equals("true")) {
                viewModel.getHomeInfo()
                arguments?.putString("status", "false")
            }
        }

        viewModel.status.observe(viewLifecycleOwner, Observer {
            try {
                snackbar = Snackbar.make(binding.homeCl, "Something went wrong", Snackbar.LENGTH_LONG)
                when (it) {
                    Status.ERROR ->
                        snackbar.apply {
                            setAction(
                                    "Retry"
                            ) {
                                viewModel.getHomeInfo()
                            }.show()

                        }
                    else -> {
                        snackbar.dismiss()
                    }
                }
            } catch (e: Exception) {
            }
        })
    }

    override fun onPause() {
        super.onPause()
        try{
            snackbar.dismiss()
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

}
