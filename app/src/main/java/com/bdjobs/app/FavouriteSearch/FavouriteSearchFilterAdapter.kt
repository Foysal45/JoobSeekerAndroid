package com.bdjobs.app.FavouriteSearch

import android.app.Activity
import android.content.Context
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.API.ApiServiceMyBdjobs
import com.bdjobs.app.API.ModelClasses.FavouriteSearchCountDataModelWithID
import com.bdjobs.app.API.ModelClasses.FavouriteSearchCountModel
import com.bdjobs.app.BackgroundJob.FavSearchDeleteJob
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.Databases.Internal.BdjobsDB
import com.bdjobs.app.Databases.Internal.FavouriteSearch
import com.bdjobs.app.LoggedInUserLanding.HomeCommunicator
import com.bdjobs.app.LoggedInUserLanding.MainLandingActivity
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.Utilities.*
import com.bdjobs.app.Utilities.Constants.Companion.api_request_result_code_ok
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FavouriteSearchFilterAdapter(private val context: Context, private val items: MutableList<FavouriteSearch>) : RecyclerView.Adapter<ViewHolder>() {


    val dataStorage = DataStorage(context)
    val bdjobsDB = BdjobsDB.getInstance(context)
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


        try {
            if (items[position].updatedon?.toSimpleTimeString().isNullOrBlank()) {
                holder.timeTV.text = items[position].createdon?.toSimpleTimeString()
                holder.dateTV.text = items[position].createdon?.toSimpleDateString()
            } else {
                holder.timeTV.text = items[position].updatedon?.toSimpleTimeString()
                holder.dateTV.text = items[position].updatedon?.toSimpleDateString()
            }
        } catch (e: Exception) {
        }

        holder.filter1TV.text = getFilterString(items[position])

        holder.parentView.setOnClickListener {
            favCommunicator?.let { comm ->
                try {
                    comm.goToJobSearch(items[position].filterid!!)
                } catch (e: Exception) {
                    logException(e)
                }
            }
            homeCommunicator?.let { comm ->
                try {
                    comm.goToJobSearch(items[position].filterid!!)
                } catch (e: Exception) {
                    logException(e)
                }
            }
        }

        if (favCommunicator != null) {
            holder.deleteTV.show()
            holder.editTV.show()

            holder.deleteTV.setOnClickListener {
                activity?.alert("Are you sure you want to delete this favorite search?", "Confirmation") {
                    yesButton {
                        deleteFavSearch(position)
                    }
                    noButton { dialog ->
                        dialog.dismiss()
                    }
                }.show()
            }

            holder.editTV.setOnClickListener {
                try {
                    favCommunicator?.goToEditMode(items[position].filterid!!)
                } catch (e: Exception) {
                    logException(e)
                }
            }
        } else {
            holder.deleteTV.hide()
            holder.editTV.hide()
        }


        holder.progressBar.show()

        val filterId = items[position].filterid

        val filterCount = Constants.Favcounts.filter { it ->
            it.id == filterId
        }

        if (filterCount.isNullOrEmpty()) {

            ApiServiceMyBdjobs.create().getFavFilterCount(userId = bdjobsUserSession.userId, decodeId = bdjobsUserSession.decodId, intFId = filterId).enqueue(object : Callback<FavouriteSearchCountModel> {
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
                                val favCountWithID = FavouriteSearchCountDataModelWithID(
                                        intCount = response.body()?.data?.get(0)?.intCount,
                                        id = filterId
                                )
                                Constants.Favcounts.add(favCountWithID)
                            }
                        }
                    } catch (e: Exception) {
                        logException(e)
                    }
                }
            })
        } else {
            try {
                Log.d("filterCount", "filterCount= $filterCount")
                holder.progressBar.hide()
                holder.favcounter1BTN.textSize = 18.0F
                if (filterCount.get(0)?.intCount?.length!! > 3) {
                    holder.favcounter1BTN.textSize = 14.0F
                }
                holder.favcounter1BTN.text = filterCount.get(0)?.intCount
            } catch (e: Exception) {
                logException(e)
            }
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }


    private fun deleteFavSearch(position: Int) {
        Log.d("czcx", "position: $position")

        try {
            if (items.size != 0) {
                val deletedItem = items.get(position)
                items.removeAt(position)
                notifyItemRemoved(position)
                Log.d("ububua", "ububua = " + deletedItem.filterid)

                val deleteJobID = FavSearchDeleteJob.scheduleAdvancedJob(deletedItem.filterid!!)
                //undoRemove(activity.baseCL, deletedItem, position, deleteJobID)
                favCommunicator?.decrementCounter()
            } else {
                context.toast("No items left here!")
            }
        } catch (e: Exception) {
            logException(e)
        }
    }

    private fun undoRemove(v: View, deletedItem: FavouriteSearch?, deletedIndex: Int, deleteJobID: Int) {
        val msg = Html.fromHtml("<font color=\"#ffffff\">The information has been deleted successfully</font>")
        val snack = Snackbar.make(v, "$msg", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    FavSearchDeleteJob.cancelJob(deleteJobID)
                    restoreMe(deletedItem!!, deletedIndex)
                    favCommunicator?.scrollToUndoPosition(deletedIndex)
                    Log.d("comid", "comid")
                }

        snack.show()
        Log.d("swipe", "dir to LEFT")
    }

    private fun restoreMe(item: FavouriteSearch, pos: Int) {
        items.add(pos, item)
        notifyItemInserted(pos)
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