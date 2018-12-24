package com.bdjobs.app.API.ModelClasses

data class EmployerListModelClass(
        val common: EmployerListModelCommon,
        val `data`: List<EmployerListModelData>,
        val message: String,
        val statuscode: String
)

data class EmployerListModelData(
        val companyid: String,
        val companyname: String,
        val isaliasname: String,
        val totaljobs: String
)

data class EmployerListModelCommon(
        val totalrecordsfound: String
)