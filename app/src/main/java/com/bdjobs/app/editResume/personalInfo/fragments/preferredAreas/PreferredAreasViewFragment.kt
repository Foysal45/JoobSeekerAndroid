package com.bdjobs.app.editResume.personalInfo.fragments.preferredAreas


import android.app.Fragment
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.editResume.adapters.models.GetPreferredAreas
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
        prefCallBack = activity as PersonalInfo
        dataStorage = DataStorage(activity)
    }

    override fun onResume() {
        super.onResume()
        prefCallBack.setTitle(getString(R.string.title_pref_areas))
        doWork()
    }

    private fun doWork() {
        clPrefAreaView.hide()
        shimmerStart()
        populateData()
    }


    private fun populateData() {

        val call = ApiServiceMyBdjobs.create().getPreferredAreaInfo(session.userId, session.decodId)
        call.enqueue(object : Callback<GetPreferredAreas> {
            override fun onFailure(call: Call<GetPreferredAreas>, t: Throwable) {
                shimmerStop()
                clPrefAreaView.hide()
                activity.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<GetPreferredAreas>, response: Response<GetPreferredAreas>) {
                try {
                    if (response.isSuccessful) {
                        shimmerStop()
                        clPrefAreaView.show()
                        val respo = response.body()
                        prefCallBack.setEditButton(true, "editPrefAreas")
                        prefCallBack.passPrefAreasData(respo?.prefData?.get(0)!!)
                        setupView(respo)
                    }
                } catch (e: Exception) {
                    if (activity != null) {
                        //activity.toast("${response.body()?.message}")
                        activity.logException(e)
                        activity.error("++${e.message}")
                    }
                }
            }
        })
    }

    private fun setupView(data: GetPreferredAreas) {
        val preferredJobCategories = data.prefData?.get(0)?.preferredJobCategories
        val preferredBlueCategories = data.prefData?.get(0)?.preferredBlueCategories
        val preferredOrgTypes = data.prefData?.get(0)?.preferredOrganizationType
        val preferredInsideBDLocs = data.prefData?.get(0)?.inside
        val preferredOutsideBDLocs = data.prefData?.get(0)?.outside
        //for ((i, value) in areaOfexps?.withIndex()!!)
        preferredJobCategories?.forEach {
            addChip(it?.prefCatName!!, cg_functional_pref)
        }
        preferredBlueCategories?.forEach {
            addChip(it?.prefBlueCatName!!, cg_skilled_pref)
        }
        preferredOrgTypes?.forEach {
            addChip(it?.prefOrgName!!, cg_org_type)
        }
        preferredInsideBDLocs?.forEach {
            addChip(it?.districtName!!, cg_org_pref_locs)
        }
        if (preferredOutsideBDLocs != null) {
            textView51.show()
            preferredOutsideBDLocs.forEach {
                addChip(it?.countryName!!, cg_org_pref_out_locs)
            }
        } else textView51.hide()
    }


    private fun addChip(input: String, cg: ChipGroup) {
        val c1 = getChip(input, R.xml.chip_highlighted)
        cg.addView(c1)
        //LL_ORI_Keyword?.closeKeyboard(activity)
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
            shimmer_view_container_JobList.startShimmerAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

    private fun shimmerStop() {
        try {
            shimmer_view_container_JobList.hide()
            shimmer_view_container_JobList.stopShimmerAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }
}
