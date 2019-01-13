package com.bdjobs.app.API.ModelClasses

data class InvitationDetailModels(
    val common: InvitationDetailModelsCommon,
    val `data`: List<InvitationDetailModelsData>,
    val message: String,
    val statuscode: String
)

data class InvitationDetailModelsCommon(
    val applyDate: String,
    val applyId: String,
    val jobClosed: String,
    val rating: String,
    val ratingDate: String,
    val ratingMessage: String
)

data class InvitationDetailModelsData(
    val activity: String,
    val confimationStatus: String,
    val confirmationDate: String,
    val confirmationMessage: String,
    val direction: Direction,
    val examDate: String,
    val examMessage: String,
    val examTime: String,
    val examTitle: String,
    val invitationDate: String,
    val invitationId: String,
    val previousScheduleDate: String,
    val previousScheduleTime: String,
    val venue: String
)

data class Direction(
    val lan: String,
    val lat: String
)