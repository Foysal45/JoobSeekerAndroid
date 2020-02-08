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
            val degreeLevels = dataStorage.getDegreeLevels
            val degreeLevelsAdapter = ArrayAdapter<String>(context!!,
                    android.R.layout.simple_dropdown_item_1line, degreeLevels)

            tie_degree_level?.addTextChangedListener(TW.CrossIconBehave(tie_degree_level))

            tie_degree_level?.setAdapter(degreeLevelsAdapter)
            tie_degree_level?.dropDownHeight = ViewGroup.LayoutParams.WRAP_CONTENT

        }catch (e: Exception){
            Log.d("Exception", e.toString())
        }

    }

}
