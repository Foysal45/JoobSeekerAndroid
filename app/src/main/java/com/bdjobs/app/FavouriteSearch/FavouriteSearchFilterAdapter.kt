package com.bdjobs.app.FavouriteSearch

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.Databases.Internal.FavouriteSearch
import com.bdjobs.app.R
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.FavouriteSearchCountModel
import com.bdjobs.app.LoggedInUserLanding.HomeCommunicator
import com.bdjobs.app.LoggedInUserLanding.MainLandingActivity
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.api_request_result_code_ok
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FavouriteSearchFilterAdapter(private val context: Context, private val items: List<FavouriteSearch>) : RecyclerView.Adapter<ViewHolder>() {


    val dataStorage = DataStorage(context)
    val bdjobsUserSession = BdjobsUserSession(context)
    val activity = context as Activity
    var favCommunicator: FavCommunicator? = null
    var homeCommunicator: HomeCommunicator? = null

    init {

        if (activity is MainLandingActivity) {
            homeCommunicator = context as HomeCommunicator
        }

        if (activity is FavouriteSearchBaseActivity) {
            favCommunicator = context as FavCommunicator
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.fav_list_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.favTitle1TV.text = items[position].filtername
        holder.dateTV.text = items[position].createdon?.toSimpleDateString()
        holder.timeTV.text = items[position].createdon?.toSimpleTimeString()
        holder.filter1TV.text = getFilterString(items[position])

        holder.parentView.setOnClickListener {
            favCommunicator?.let { comm ->
                comm.goToJobSearch(items[position].filterid!!)
            }
            homeCommunicator?.let { comm ->
                comm.goToJobSearch(items[position].filterid!!)
            }
        }

        if (favCommunicator != null) {
            holder.deleteTV.show()
            holder.editTV.show()

            holder.deleteTV.setOnClickListener {
                activity.alert("Are you sure you want to delete this favorite search?", "Confirmation") {
                    yesButton {
                        deleteFavSearch(items[position].filterid!!)
                    }
                    noButton { dialog ->
                        dialog.dismiss()
                    }
                }.show()
            }

            holder.editTV.setOnClickListener {
                favCommunicator?.goToEditMode(items[position].filterid!!)
            }
        } else {
            holder.deleteTV.hide()
            holder.editTV.hide()
        }


        holder.progressBar.show()
        ApiServiceMyBdjobs.create().getFavFilterCount(userId = bdjobsUserSession.userId, decodeId = bdjobsUserSession.decodId, intFId = items[position].filterid).enqueue(object : Callback<FavouriteSearchCountModel> {
            override fun onFailure(call: Call<FavouriteSearchCountModel>, t: Throwable) {
                error("onFailure", t)
            }

            override fun onResponse(call: Call<FavouriteSearchCountModel>, response: Response<FavouriteSearchCountModel>) {
                try {
                    response.body()?.statuscode?.let { status ->
                        if (status.equalIgnoreCase(api_request_result_code_ok)) {
                            holder.progressBar.hide()
                            holder.favcounter1BTN.textSize = 18.0F
                            if (response.body()?.data?.get(0)?.intCount?.length!! > 3) {
                                holder.favcounter1BTN.textSize = 14.0F
                            }
                            holder.favcounter1BTN.text = response.body()?.data?.get(0)?.intCount
                            Log.d("favouriteSearch", "favouriteSearch.intCount = ${response.body()?.data?.get(0)?.intCount}")
                        }
                    }
                } catch (e: Exception) {
                    logException(e)
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return items?.size!!
    }


    private fun deleteFavSearch(filterID: String) {


    }

    private fun getFilterString(favouriteSearch: FavouriteSearch): String? {

        Log.d("favouriteSearch", "favouriteSearch.filterid= ${favouriteSearch.filterid}")

        val age = dataStorage.getAgeRangeNameByID(favouriteSearch.age)
        val keyword = favouriteSearch.keyword
        val newsPaper = dataStorage.getNewspaperNameById(favouriteSearch.newspaper)
        val functionalCat = dataStorage.getCategoryNameByID(favouriteSearch.functionalCat)
        val location = dataStorage.getLocationNameByID(favouriteSearch.location)
        val organization = dataStorage.getJobSearcOrgTypeByID(favouriteSearch.organization)
        val jobNature = dataStorage.getJobNatureByID(favouriteSearch.jobnature)
        val jobLevel = dataStorage.getJobLevelByID(favouriteSearch.joblevel)
        val industrialCat = dataStorage.getJobSearcIndustryNameByID(favouriteSearch.industrialCat)
        val experience = dataStorage.getJobExperineceByID(favouriteSearch.experience)
        val jobtype = dataStorage.getJobTypeByID(favouriteSearch.jobtype)
        val genderb = dataStorage.getGenderByID(favouriteSearch.genderb)
        var retiredArmy = ""

        favouriteSearch.retiredarmy?.let { string ->
            if (string == "1")
                retiredArmy = "Preferred Retired Army"
        }

        var gender = ""
        favouriteSearch.gender?.let { string ->
            val result: List<String> = string.split(",").map { it.trim() }
            result.forEach {
                gender += dataStorage.getGenderByID(it) + ","
            }
        }

        Log.d("gender", "gender:  ${favouriteSearch.gender}")

        Log.d("gender", "genderb: ${favouriteSearch.genderb}")

        var allValues = ("$keyword,$functionalCat,$organization,$gender,$genderb,$industrialCat,$location,$age,$jobNature,$jobLevel,$experience,$jobtype,$retiredArmy,$newsPaper")
        Log.d("allValuesN", allValues)
        allValues = allValues.replace("Any".toRegex(), "")
        allValues = allValues.replace("null".toRegex(), "")
        Log.d("allValues", allValues)
        for (i in 0..15) {
            allValues = allValues.replace(",,".toRegex(), ",")
        }
        allValues = allValues.replace(",$".toRegex(), "")
        allValues = if (allValues.startsWith(",")) allValues.substring(1) else allValues

        Log.d("allValuesN", allValues)

        return allValues.removeLastComma()
    }
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to

    val deleteTV = view.findViewById(R.id.deleteTV) as TextView
    val editTV = view.findViewById(R.id.editTV) as TextView
    val favTitle1TV = view.findViewById(R.id.favTitle1TV) as TextView
    val dateTV = view.findViewById(R.id.createdOnDateTV) as TextView
    val timeTV = view.findViewById(R.id.time1TV) as TextView
    val filter1TV = view.findViewById(R.id.filter1TV) as TextView
    val favcounter1BTN = view.findViewById(R.id.favcounter1BTN) as Button
    val progressBar = view.findViewById(R.id.progressBar2) as ProgressBar
    val parentView = view.findViewById(R.id.itemView) as ConstraintLayout

}