package com.aitor.trackactividades.buscarUsuario.data.response

import com.aitor.trackactividades.feed.data.response.InfoResponse
import com.google.gson.annotations.SerializedName

data class UserSearchPageResponse(
    @SerializedName("results") val content: List<UserSearchResponse>,
    @SerializedName("info") val information: InfoResponse,
)

