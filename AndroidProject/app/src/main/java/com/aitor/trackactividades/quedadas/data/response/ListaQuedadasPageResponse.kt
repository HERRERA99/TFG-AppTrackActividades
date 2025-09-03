package com.aitor.trackactividades.quedadas.data.response

import com.aitor.trackactividades.buscarUsuario.data.response.UserSearchResponse
import com.aitor.trackactividades.feed.data.response.InfoResponse
import com.google.gson.annotations.SerializedName

data class ListaQuedadasPageResponse(
    @SerializedName("results") val content: List<ItemListaQuedadasResponse>,
    @SerializedName("info") val information: InfoResponse,
)
