package com.bdjobs.app.API

import com.bdjobs.app.API.ModelClasses.DatabaseUpdateModel
import com.bdjobs.app.Utilities.Constants
import com.bdjobs.app.Utilities.Constants.Companion.api_jobs_db_update
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import okhttp3.ResponseBody



interface ApiServiceJobs {
    @GET(api_jobs_db_update)
    fun getDbInfo(@Query("lastUpdateDate") lastUpdateDate: String): Call<DatabaseUpdateModel>

    @GET
    fun downloadDatabaseFile(@Url fileUrl: String): Call<ResponseBody>

    companion object Factory {

        fun create(): ApiServiceJobs {
            val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.baseUrlJobs)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            return retrofit.create(ApiServiceJobs::class.java)
        }
    }

}