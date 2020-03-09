package com.bdjobs.app.assessment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.Utilities.getString
import com.bdjobs.app.assessment.adapters.ModuleListAdapter
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_module_view.*
import kotlinx.android.synthetic.main.layout_view_module_instruction.*
import org.jetbrains.anko.support.v4.selector
import org.jetbrains.anko.support.v4.toast


/**
 * A simple [Fragment] subclass.
 */
class ModulesViewFragment : Fragment() {

    private lateinit var dataStorage: DataStorage
    private var moduleListAdapter: ModuleListAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
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

        showHideView()

        val degreeLevels = dataStorage.getDegreeLevels

        et_degree_level.setOnClickListener {
            selector("Please select degree level", degreeLevels.toList()) { dialogInterface, i ->
                et_degree_level?.setText(degreeLevels[i])
                upDateView()
            }
        }

        btn_view_module.setOnClickListener{
            toast("Select Degree Levels")
        }

        btn_cl?.setOnClickListener {
            findNavController().navigate(R.id.action_modulesViewFragment_to_testLocationFragment)
        }

        learn_more_btn.setOnClickListener{
            findNavController().navigate(R.id.action_modulesViewFragment_to_testInstructionFragment)

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

        btn_view_module.setOnClickListener{
            showHideView()
        }

        moduleListAdapter = ModuleListAdapter(context!!, dataStorage.getCompulsoryModulebyDegreeID(degreeID.toString()))

        rv_compulsory_modules?.adapter = moduleListAdapter
        rv_compulsory_modules?.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        rv_compulsory_modules?.layoutManager = layoutManager
        rv_compulsory_modules?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()


        moduleListAdapter = ModuleListAdapter(context!!, dataStorage.getOptionalModulebyDegreeID(degreeID.toString()))

        rv_optional_modules?.adapter = moduleListAdapter
        rv_optional_modules?.setHasFixedSize(true)
        val layoutManager2 = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        rv_optional_modules?.layoutManager = layoutManager2
        rv_optional_modules?.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
    }

    fun showHideView(){

        tv_compulsory_modules_title.visibility = if (tv_compulsory_modules_title.visibility == View.VISIBLE){
            View.GONE
        } else{
            View.VISIBLE
        }

        tv_optional_modules_title.visibility = if (tv_optional_modules_title.visibility == View.VISIBLE){
            View.GONE
        } else{
            View.VISIBLE
        }

        rv_compulsory_modules.visibility = if (rv_compulsory_modules.visibility == View.VISIBLE){
            View.GONE
        } else{
            View.VISIBLE
        }

        rv_optional_modules.visibility = if (rv_optional_modules.visibility == View.VISIBLE){
            View.GONE
        } else{
            View.VISIBLE
        }
    }
}
