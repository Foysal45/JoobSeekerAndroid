package com.bdjobs.app.editResume.otherInfo.fragments


import android.app.Fragment
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.error
import com.bdjobs.app.Utilities.hide
import com.bdjobs.app.Utilities.logException
import com.bdjobs.app.Utilities.show
import com.bdjobs.app.editResume.adapters.TrainingInfoAdapter
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
    private var adapter: TrainingInfoAdapter? = null
    private var arr: ArrayList<Skill?>? = null
    private var arrNew: List<SpecializationDataModel?>? = null
    private var arrSkill: ArrayList<Skill?>? = null
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
        /*  populateData()*/
        /*   rv_lang_view.behaveYourself(fab_language_add)*/

        eduCB.setTitle("Specialization")

        /* val preferredOutsideBDLocs = data.prefData?.get(0)?.outside
         //for ((i, value) in areaOfexps?.withIndex()!!)
         preferredJobCategories?.forEach {
             addChip(it?.prefCatName!!, cg_functional_pref)
         }*/

        populateData()

    }


// end of the calling method

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

        mainlayout.hide()
        shimmerStart()
        val call = ApiServiceMyBdjobs.create().getSpecializationInfo(session.userId, session.decodId)
        call.enqueue(object : retrofit2.Callback<SpecialzationModel> {
            override fun onFailure(call: Call<SpecialzationModel>, t: Throwable) {
                /*  shimmerStop()
                  rv_lang_view.show()*/
                activity.toast("Error occurred")
            }

            override fun onResponse(call: Call<SpecialzationModel>, response: Response<SpecialzationModel>) {
                try {
                    if (response.isSuccessful) {
                        /*   shimmerStop()
                           rv_lang_view.show()*/
                        val respo = response.body()

                        /*  Log.d("dsfklhgjfd;h", "$respo")*/

                        arr = respo?.data!![0]?.skills as ArrayList<Skill?>

                        for (item in arr!!) {

                            addChip(item?.skillName.toString(), cg_skill)

                        }

                        skillDescriptionTV.text = respo.data[0]?.description
                        curricularTV.text = respo.data[0]?.extracurricular

                        eduCB.setDeleteButton(false)
                        eduCB.setEditButton()

                        /* if (arr!!.size < 3) {
                             fab_language_add.show()
                             //activity.toast("else : ${arr?.size}")
                         }*/


                        //activity.toast("${arr?.size}")
                        /* if (arr != null) {
                             setupRV(arr!!)
                         }*/

                        shimmerStop()
                        mainlayout.show()
                    }
                } catch (e: Exception) {
                    shimmerStop()
                    if (activity != null) {
                        //activity.toast("${response.body()?.message}")
                        activity.logException(e)
                        activity.error("++${e.message}")
                    }
                }
                adapter?.notifyDataSetChanged()
            }
        })
    }


    private fun shimmerStart() {
        try {
            shimmer_view_container_specialization.show()
            shimmer_view_container_specialization.startShimmerAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }

    private fun shimmerStop() {
        try {
            shimmer_view_container_specialization.hide()
            shimmer_view_container_specialization.stopShimmerAnimation()
        } catch (e: Exception) {
            e.printStackTrace()
            logException(e)
        }
    }



}
