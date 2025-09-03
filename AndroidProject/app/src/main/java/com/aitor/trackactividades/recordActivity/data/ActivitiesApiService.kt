package com.aitor.trackactividades.recordActivity.data

import com.aitor.trackactividades.recordActivity.data.request.ActivityRequest
import com.aitor.trackactividades.recordActivity.data.response.ActivityResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ActivitiesApiService {

    @POST("/activities")
    suspend fun createActivity(
        @Header("Authorization") token: String,
        @Body activity: ActivityRequest
    ): ActivityResponse
}