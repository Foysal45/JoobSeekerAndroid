package com.bdjobs.app.API.ModelClasses

data class FollowUnfollowModelClass(
    val common: Any,
    val `data`: List<FollowUnfollowData>,
    val message: String,
    val statuscode: String
)

data class FollowUnfollowData(
    val jobcount: String,
    val message: String,
    val status: String
)