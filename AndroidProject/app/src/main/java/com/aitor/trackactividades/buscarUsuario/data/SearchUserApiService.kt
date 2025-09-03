package com.aitor.trackactividades.buscarUsuario.data

import com.aitor.trackactividades.buscarUsuario.data.response.UserSearchPageResponse
import com.aitor.trackactividades.buscarUsuario.data.response.UserSearchResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SearchUserApiService {
    @GET("/user/search")
    suspend fun searchUsers(
        @Header("Authorization") token: String,
        @Query("text") text: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): UserSearchPageResponse
}
