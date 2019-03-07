package com.bdjobs.app.editResume.personalInfo.fragments.otherRelevantInfo


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
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.editResume.adapters.models.GetORIResponse
import com.bdjobs.app.editResume.callbacks.PersonalInfo
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
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
        session = BdjobsUserSession(activity)
        oriCallBack = activity as PersonalInfo
        dataStorage = DataStorage(activity)
        doWork()
    }

    override fun onResume() {
        super.onResume()
        oriCallBack.setTitle(getString(R.string.title_ORI))
    }

    private fun doWork() {
        clORIMainLayout.hide()
        shimmerStart()
        populateData()
    }

    private fun populateData() {

        val call = ApiServiceMyBdjobs.create().getORIInfo(session.userId, session.decodId)
        call.enqueue(object : Callback<GetORIResponse> {
            override fun onFailure(call: Call<GetORIResponse>, t: Throwable) {
                try {
                    shimmerStop()
                    clORIMainLayout?.hide()
                    activity?.toast(R.string.message_common_error)
                } catch (e: Exception) {
                }
            }

            override fun onResponse(call: Call<GetORIResponse>, response: Response<GetORIResponse>) {
                try {
                    if (response.isSuccessful) {
                        shimmerStop()
                        clORIMainLayout?.show()
                        val respo = response.body()
                        oriCallBack.setEditButton(true, "editORI")
                        oriCallBack.passOriData(respo?.data?.get(0)!!)
                        setupView(respo)
                    }
                } catch (e: Exception) {
                    activity?.logException(e)
                    activity?.error("++${e.message}")
                }
            }
        })

    }

    private fun setupView(info: GetORIResponse) {
        val data = info.data?.get(0)
        tvORICareerSummary.text = data?.careerSummery
        tvORISpecialQualificaiton.text = data?.specialQualifications
        val keywords = data?.keywords?.removeLastComma()
        val keyArray: List<String>? = keywords?.split(",")?.map { it.trim() }
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
            shimmer_view_container_JobList?.hide()
            shimmer_view_container_JobList?.stopShimmerAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }
}
