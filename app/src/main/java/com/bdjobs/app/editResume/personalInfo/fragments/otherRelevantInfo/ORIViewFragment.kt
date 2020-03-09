package com.bdjobs.app.editResume.personalInfo.fragments.otherRelevantInfo


import android.app.Fragment
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.GetORIResponse
import com.bdjobs.app.editResume.adapters.models.ORIdataItem
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_ori_view.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ORIViewFragment : Fragment() {
    private lateinit var oriCallBack: PersonalInfo
    private lateinit var session: BdjobsUserSession
    private lateinit var dataStorage: DataStorage

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ori_view, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dataStorage = DataStorage(activity)
    }

    override fun onResume() {
        super.onResume()
        session = BdjobsUserSession(activity)
        oriCallBack = activity as PersonalInfo
        oriCallBack.setTitle(getString(R.string.title_ORI))
        if (oriCallBack.getBackFrom() == "") {
            val respo = oriCallBack.getOriData()
            setupView(respo)
            oriCallBack.setEditButton(true, "editORI")
        } else {
            doWork()
        }
    }

    private fun doWork() {
        shimmerStart()
        populateData()
        oriCallBack.setBackFrom("")
    }

    private fun populateData() {
        clORIMainLayout.hide()
        val call = ApiServiceMyBdjobs.create().getORIInfo(session.userId, session.decodId)
        call.enqueue(object : Callback<GetORIResponse> {
            override fun onFailure(call: Call<GetORIResponse>, t: Throwable) {
                shimmerStop()
                activity?.toast(R.string.message_common_error)
            }

            override fun onResponse(call: Call<GetORIResponse>, response: Response<GetORIResponse>) {
                try {
                    if (response.isSuccessful) {
                        shimmerStop()
                        clORIMainLayout?.show()
                        val respo = response.body()
                        oriCallBack.passOriData(respo?.data?.get(0)!!)
                        setupView(respo?.data?.get(0)!!)
                        oriCallBack.setEditButton(true, "editORI")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    logException(e)
                    d("++${e.message}")

                }
            }
        })

    }

    private fun setupView(data: ORIdataItem) {
        //Log.d("rakib", "${data.keywords?.length}")
        tvORICareerSummary.text = data?.careerSummery
        tvORISpecialQualificaiton.text = data?.specialQualifications
        //Log.d("rakib", "total commas ${data?.keywords?.countCommas()}")
        val keywords = data?.keywords?.removeLastComma()
        val keyArray: List<String>? = keywords?.split(",")?.map { it.trim() }
        removeChips()
        keyArray?.forEach {
            if (it.isNotBlank())
                addChip(it)
        }
        //tvORIS.text = data?.careerSummery
    }


    private fun addChip(input: String) {
        val c1 = getChip(input, R.xml.chip_highlighted)

        entry_chip_group.addView(c1)
        LL_ORI_Keyword?.closeKeyboard(activity)
    }

    private fun removeChips(){
       entry_chip_group.removeAllViews()
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
            shimmer_view_container_JobList?.show()
            shimmer_view_container_JobList?.startShimmer()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

    private fun shimmerStop() {
        try {
            shimmer_view_container_JobList?.hide()
            shimmer_view_container_JobList?.stopShimmer()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }
}
