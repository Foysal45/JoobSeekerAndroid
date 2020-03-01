package com.bdjobs.app.assessment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bdjobs.app.Utilities.openUrlInBrowser
import com.bdjobs.app.assessment.enums.Status
import com.bdjobs.app.assessment.viewmodels.ResultViewModel
import com.bdjobs.app.assessment.viewmodels.ResultViewModelFactory
import com.bdjobs.app.databinding.FragmentResultBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_result.*

/**
 * A simple [Fragment] subclass.
 */
class ResultFragment : Fragment() {

    lateinit var resultViewModel: ResultViewModel

    lateinit var snackbar: Snackbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val application = requireNotNull(activity).application

        val binding = FragmentResultBinding.inflate(inflater)

        val result = ResultFragmentArgs.fromBundle(arguments!!).certificateData

        val viewModelFactory = ResultViewModelFactory(result!!,application)

        resultViewModel = ViewModelProvider(this,viewModelFactory).get(ResultViewModel::class.java)

        binding.resultViewModel = resultViewModel

        binding.lifecycleOwner = this

        resultViewModel.resultMessage.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(activity, resultViewModel.resultMessage.value, Toast.LENGTH_SHORT).show()
            }
        })

        binding.btnCl.setOnClickListener {
            activity?.openUrlInBrowser(resultViewModel.downloadLink)
        }

        resultViewModel.status.observe(viewLifecycleOwner, Observer {
            try {
                snackbar = Snackbar.make(result_sv, "Something went wrong", Snackbar.LENGTH_LONG)
                when (it) {
                    Status.ERROR ->

                        snackbar.apply {
                            setAction(
                                    "Retry"
                            ) {
                                resultViewModel.getResults()
                            }.show()
                        }
                    else -> {
                        snackbar.dismiss()
                    }
                }
            } catch (e: Exception) {
            }
        })
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        try {
            snackbar.dismiss()
        } catch (e: Exception) {
        }
    }
}
