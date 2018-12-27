package com.bdjobs.app.API.ModelClasses

data class EmployerJobListsModel(
       // val common: Any,
        val `data`: List<EmployerJobListsModelData>,
        val message: String,
        val statuscode: String
)
data class EmployerJobListsModelData(
        val address: String,
        val business: String,
        val deadline: String,
        val error: String,
        val isshowaddress: String,
        val jobid: String,
        val jobtitle: String,
        val ln: String
)