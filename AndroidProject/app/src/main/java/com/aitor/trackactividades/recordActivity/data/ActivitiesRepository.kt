package com.aitor.trackactividades.recordActivity.data

import com.aitor.trackactividades.recordActivity.data.request.ActivityRequest
import com.aitor.trackactividades.recordActivity.data.response.ActivityResponse
import javax.inject.Inject

class ActivitiesRepository @Inject constructor(private val activitiesApiService: ActivitiesApiService) {

    suspend fun createActivity(token: String, activity: ActivityRequest): Activity {
        return activitiesApiService.createActivity(token, activity).toPresentation()
    }

}