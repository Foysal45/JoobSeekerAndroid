package com.bdjobs.app.editResume.otherInfo.fragments


import android.app.Fragment
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.d
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.editResume.adapters.models.Skill
import com.bdjobs.app.editResume.adapters.models.SpecializationDataModel
import com.bdjobs.app.editResume.adapters.models.SpecialzationModel
import com.bdjobs.app.editResume.callbacks.OtherInfo
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.fragment_view_specialization.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Response

class SpecializationViewFragment : Fragment() {
    private lateinit var eduCB: OtherInfo
    private var arr: ArrayList<Skill?>? = null
    private lateinit var session: BdjobsUserSession


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_specialization, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        session = BdjobsUserSession(activity)
        eduCB = activity as OtherInfo
        doWork()

    }

    private fun doWork() {
        eduCB.setTitle("Specialization")
        fab_specialization_add?.setOnClickListener {

            eduCB.goToEditInfo("addSpecialization")

        }
        populateData()

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

    private fun populateData() {

        mainlayout?.hide()
        shimmerStart()
        val call = ApiServiceMyBdjobs.create().getSpecializationInfo(session.userId, session.decodId)
        call.enqueue(object : retrofit2.Callback<SpecialzationModel> {
            override fun onFailure(call: Call<SpecialzationModel>, t: Throwable) {
                try {
                    shimmerStop()
                    activity?.toast("Error occurred")
                } catch (e: Exception) {
                    logException(e)
                }
            }

            override fun onResponse(call: Call<SpecialzationModel>, response: Response<SpecialzationModel>) {
                try {
                    if (response.isSuccessful) {
                        shimmerStop()

                        val respo = response.body()
                        arr = respo?.data!![0]?.skills as ArrayList<Skill?>
                        setData(arr!!, respo.data[0]?.description!!, respo.data[0]?.extracurricular!!, respo.data[0]!!)


                    }
                } catch (e: Exception) {
                    shimmerStop()
                    if (activity != null) {
                        //activity.toast("${response.body()?.message}")
                        logException(e)
                        d("++${e.message}")
                    }
                }

            }
        })
    }


    private fun setData(array: ArrayList<Skill?>, skillDes: String, curricular: String, response: SpecializationDataModel) {

        if (array.size == 0 && TextUtils.isEmpty(skillDes) && TextUtils.isEmpty(curricular)) {
            mainlayout?.hide()
            fab_specialization_add?.show()
            eduCB.setEditButton(false)
        } else {
            mainlayout.show()
            fab_specialization_add?.hide()
            skillDescriptionTV?.text = skillDes
            curricularTV?.text = curricular
            for (item in arr!!) {

                addChip(item?.skillName.toString(), cg_skill)

            }

            eduCB.setDeleteButton(false)
            eduCB.setEditButton(true)

            d("specialization in view fragment ${response.skills!!.size}")

            eduCB.passSpacializationData(response)

        }


    }

    private fun shimmerStart() {
        try {
            shimmer_view_container_specialization?.show()
            shimmer_view_container_specialization?.startShimmerAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

    private fun shimmerStop() {
        try {
            shimmer_view_container_specialization?.hide()
            shimmer_view_container_specialization?.stopShimmerAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }


}
