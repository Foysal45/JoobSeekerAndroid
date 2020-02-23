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
import com.bdjobs.app.Web.WebActivity
import com.bdjobs.app.assessment.enums.Status
import com.bdjobs.app.assessment.viewmodels.HomeViewModel
import com.bdjobs.app.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.layout_need_more_information.view.*
import kotlinx.android.synthetic.main.layout_what_is_employability_certification.*
import org.jetbrains.anko.startActivity

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

        learn_more_btn?.setOnClickListener { view ->
            view.findNavController().navigate(R.id.action_viewPagerFragment_to_testInstructionFragment)
        }

        binding.btnCl?.setOnClickListener {
            it.findNavController().navigate(R.id.action_viewPagerFragment_to_modulesViewFragment)
        }

        binding.noPendingTest?.takeNewTestBtn?.setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_modulesViewFragment)
        }

        binding.assessmentInfo?.changeBtn?.setOnClickListener {
            Log.d("rakib", "${viewModel.homeData.value?.paymentStatus}")
            findNavController().navigate(R.id.action_viewPagerFragment_to_chooseScheduleFragment)
        }

        binding.startTest.startBtn.setOnClickListener {
            snackbar = Snackbar.make(binding.homeCl, "Action needs to complete from website", Snackbar.LENGTH_INDEFINITE)
            snackbar.apply {
                setAction("Go To Website") {
                    val url = "https://mybdjobs.bdjobs.com/mybdjobs/assessment/smnt_certification_home.asp?device=app"
                    context.startActivity<WebActivity>("url" to url, "from" to "assessment")
                }.show()
            }
        }

        binding.needMoreInfoCl.call_cl.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:01844519336")
            startActivity(intent)
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.takeIf { it.containsKey("status") }?.apply {
            Log.d("rakibe in home", getString("status"))
            if (getString("status").equals("true")) {
                viewModel.getHomeInfo()
                arguments?.putString("status", "false")
            }
        }

        viewModel.status.observe(viewLifecycleOwner, Observer {
            try {
                snackbar = Snackbar.make(binding.homeCl, "Something went wrong", Snackbar.LENGTH_INDEFINITE)
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
