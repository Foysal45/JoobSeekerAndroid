package com.bdjobs.app.API.ModelClasses
import com.google.gson.annotations.SerializedName


data class FavouritSearchFilterModelClass(
    @SerializedName("common")
    val common: Any?,
    @SerializedName("data")
    val `data`: List<FavouritSearchFilterData>,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)

data class FavouritSearchFilterData(
    @SerializedName("age")
    val age: String?,
    @SerializedName("createdon")
    val createdon: String?,
    @SerializedName("deadline")
    val deadline: String?,
    @SerializedName("experience")
    val experience: String?,
    @SerializedName("filterid")
    val filterid: String?,
    @SerializedName("filtername")
    val filtername: String?,
    @SerializedName("functionalCat")
    val functionalCat: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("genderb")
    val genderb: String?,
    @SerializedName("industrialCat")
    val industrialCat: String?,
    @SerializedName("joblevel")
    val joblevel: String?,
    @SerializedName("jobnature")
    val jobnature: String?,
    @SerializedName("jobtype")
    val jobtype: String?,
    @SerializedName("keyword")
    val keyword: String?,
    @SerializedName("location")
    val location: String?,
    @SerializedName("newspaper")
    val newspaper: String?,
    @SerializedName("organization")
    val organization: String?,
    @SerializedName("postedon")
    val postedon: String?,
    @SerializedName("retiredarmy")
    val retiredarmy: String?,
    @SerializedName("totaljobs")
    val totaljobs: String?,
    @SerializedName("updatedon")
    val updatedon: String?

)

data class FavouriteSearchCountModel(
    @SerializedName("data")
    val `data`: List<FavouriteSearchCountDataModel?>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("statuscode")
    val statuscode: String?
)

data class FavouriteSearchCountDataModel(
    @SerializedName("intCount")
    val intCount: String?
)

