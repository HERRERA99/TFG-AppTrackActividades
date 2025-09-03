package com.aitor.trackactividades.recordActivity.data

import android.util.Log
import com.aitor.trackactividades.recordActivity.data.request.ActivityRequest
import javax.inject.Inject

class ActivitiesRepository @Inject constructor(private val activitiesApiService: ActivitiesApiService) {

    suspend fun createActivity(token: String, activity: ActivityRequest): Activity {
        Log.d("Activity Request", activity.toString())
        return activitiesApiService.createActivity(token, activity).toPresentation()
    }

}