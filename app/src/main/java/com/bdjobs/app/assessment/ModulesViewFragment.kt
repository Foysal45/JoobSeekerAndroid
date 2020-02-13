package com.bdjobs.app.Assessment


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.bdjobs.app.Databases.External.DataStorage

import com.bdjobs.app.R

import com.bdjobs.app.Utilities.getString
import com.google.android.material.chip.Chip

import kotlinx.android.synthetic.main.fragment_module_view.*

import org.jetbrains.anko.support.v4.selector


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
                upDateView()
            }
        }

        btn_cl?.setOnClickListener {
            findNavController().navigate(R.id.action_modulesViewFragment_to_testLocationFragment)
        }
    }

    private fun upDateView(){
        Log.d("degree Name " , et_degree_level.getString())
        val degreeID = dataStorage.getIDbyDegreeLevel(et_degree_level.getString())

        Log.d("degree ID " , degreeID.toString())

        val degreeSubjects = dataStorage.getSubjectsbyDegreeID(degreeID.toString())

        val subjectArray = degreeSubjects?.split("*")?.toTypedArray()

        cg_specialization.removeAllViews()
        for (index in subjectArray!!.indices) {
            val chip = Chip(cg_specialization.context)
            chip.text= subjectArray[index]
            chip.isClickable = false
            chip.isCheckable = false
            cg_specialization.addView(chip)
        }

    }
}
