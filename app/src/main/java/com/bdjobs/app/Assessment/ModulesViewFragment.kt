package com.bdjobs.app.Assessment


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.bdjobs.app.Databases.External.DataStorage

import com.bdjobs.app.R
import com.bdjobs.app.Utilities.TW
import kotlinx.android.synthetic.main.fragment_emp_history_edit.*
import kotlinx.android.synthetic.main.fragment_module_view.*
import org.jetbrains.anko.support.v4.selector
import kotlin.math.log

/**
 * A simple [Fragment] subclass.
 */
class ModulesViewFragment : Fragment() {

    private lateinit var dataStorage: DataStorage


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_module_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        try{
            dataStorage = DataStorage(context!!)
            initViews()

        }catch (e: Exception){
            Log.d("Exception", e.toString())
        }

    }

    private fun initViews(){

        val degreeLevels = dataStorage.getDegreeLevels

        et_degree_level.setOnClickListener {
            selector("Please select degree level", degreeLevels.toList()) { dialogInterface, i ->
                et_degree_level?.setText(degreeLevels[i])
            }


        }
    }
}
