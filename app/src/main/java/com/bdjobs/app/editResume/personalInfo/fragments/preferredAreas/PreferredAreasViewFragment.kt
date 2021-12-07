package com.bdjobs.app.editResume.personalInfo.fragments.preferredAreas


import android.app.Fragment
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.utilities.d
import com.bdjobs.app.utilities.hide
import com.bdjobs.app.utilities.logException
import com.bdjobs.app.utilities.show
import com.bdjobs.app.editResume.adapters.models.GetPreferredAreas
import com.bdjobs.app.editResume.adapters.models.PreferredAreasData
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_preferred_areas_view.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PreferredAreasViewFragment : Fragment() {

    private lateinit var prefCallBack: PersonalInfo
    private lateinit var session: BdjobsUserSession
    private lateinit var dataStorage: DataStorage

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preferred_areas_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
    }

    override fun onResume() {
        super.onResume()
        dataStorage = DataStorage(activity)
        prefCallBack = activity as PersonalInfo
        prefCallBack.setTitle(getString(R.string.title_pref_areas))
        if (prefCallBack.getBackFrom() == "") {
            val respo = prefCallBack.getPrefAreasData()
            setupView(respo)
            prefCallBack.setEditButton(true, "editPrefAreas")
        } else {
            doWork()
        }
    }

    private fun doWork() {
        shimmerStart()
        populateData()
        prefCallBack.setBackFrom("")
    }


    private fun populateData() {
        //Log.d("rakib", "came here")
        clPrefAreaView.hide()
        val call = ApiServiceMyBdjobs.create().getPreferredAreaInfo(session.userId, session.decodId)
        call.enqueue(object : Callback<GetPreferredAreas> {
            override fun onFailure(call: Call<GetPreferredAreas>, t: Throwable) {
                shimmerStop()
                activity?.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<GetPreferredAreas>, response: Response<GetPreferredAreas>) {
                try {
                    if (response.isSuccessful) {
                        shimmerStop()
                        clPrefAreaView.show()
                        val respo = response.body()
                        prefCallBack.passPrefAreasData(respo?.prefData?.get(0)!!)
                        setupView(respo.prefData.get(0)!!)
                        prefCallBack.setEditButton(true, "editPrefAreas")
                    }
                } catch (e: Exception) {
                    if (activity != null) {
                        //activity.toast("${response.body()?.message}")
                        logException(e)
                        d("++${e.message}")
                    }
                }
            }
        })
    }

    private fun setupView(data: PreferredAreasData) {
        //Log.d("rakib", "came here testing")
        val preferredJobCategories = data.preferredJobCategories
        val preferredBlueCategories = data.preferredBlueCategories
        val preferredOrgTypes = data.preferredOrganizationType
        val preferredInsideBDLocs = data.inside
        val preferredOutsideBDLocs = data.outside
        //for ((i, value) in areaOfexps?.withIndex()!!)
        //Log.d("rakib", "Preffered job categories ${preferredJobCategories!!.size}")
        removeChips(cg_functional_pref)
        preferredJobCategories?.forEach {
            addChip(it?.prefCatName!!, cg_functional_pref)
        }
        removeChips(cg_skilled_pref)
        preferredBlueCategories?.forEach {
            addChip(it?.prefBlueCatName!!, cg_skilled_pref)
        }
        removeChips(cg_org_type)
        preferredOrgTypes?.forEach {
            addChip(it?.prefOrgName!!, cg_org_type)
        }
        removeChips(cg_org_pref_locs)

        try {
            if (preferredInsideBDLocs != null) {
                if (preferredInsideBDLocs.isNotEmpty()) {
                    preferredInsideBDLocs.forEach {
                        addChip(it?.districtName!!, cg_org_pref_locs)
                        textView50?.show()
                    }
                } else {
                    textView50?.hide()
                }
            }
        } catch (e: Exception) {
        }

        removeChips(cg_org_pref_out_locs)
        try {
            if (preferredOutsideBDLocs != null) {
                if (preferredOutsideBDLocs.isNotEmpty()) {
                    //Log.d("rakib", "preferred area if")
                    textView51.show()
                    preferredOutsideBDLocs.forEach {
                        addChip(it?.countryName!!, cg_org_pref_out_locs)
                    }
                } else {
                    //Log.d("rakib", "preferred area else")
                    textView51.hide()
                }
            }
        } catch (e: Exception) {
        }
    }


    private fun addChip(input: String, cg: ChipGroup) {
        val c1 = getChip(input, R.xml.chip_highlighted)
        cg.addView(c1)

        //LL_ORI_Keyword?.closeKeyboard(activity)
    }

    private fun removeChips(cg: ChipGroup) {
        cg.removeAllViews()

    }

    private fun getChip(text: String, item: Int): Chip {
        val chip = Chip(activity)
        chip.setChipDrawable(ChipDrawable.createFromResource(activity, item))
        val paddingDp = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10f,
                resources.displayMetrics
        ).toInt()
        chip.closeIcon = null
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        chip.text = text
        return chip
    }

    private fun shimmerStart() {
        try {
            shimmer_view_container_JobList.show()
            shimmer_view_container_JobList.startShimmer()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

    private fun shimmerStop() {
        try {
            shimmer_view_container_JobList.hide()
            shimmer_view_container_JobList.stopShimmer()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }
}
