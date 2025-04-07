package com.aitor.trackactividades.feed.data.response

import com.google.gson.annotations.SerializedName

data class PageResponse (
    @SerializedName("info") val information: InfoResponse,
    @SerializedName("results") val results: List<PublicationResponse>
)
