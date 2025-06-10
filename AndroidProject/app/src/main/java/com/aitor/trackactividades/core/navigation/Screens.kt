package com.aitor.trackactividades.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object Splash

@Serializable
object Home

@Serializable
object Login

@Serializable
object Register

@Serializable
object Feed

@Serializable
object RecordActivity

@Serializable
object Search

@Serializable
object Historial

@Serializable
object Quedadas

@Serializable
object FormularioQuedada

@Serializable
data class DetallesQuedada(val quedadaId: Long) {
    companion object {
        const val ROUTE = "detallesQuedada/{quedadaId}"
        fun createRoute(quedadaId: Long) = "detallesQuedada/$quedadaId"
    }
}


@Serializable
data class Activity(val publicationId: Long) {
    companion object {
        const val ROUTE = "activity/{publicationId}"
    }
}

@Serializable
data class Profile(val userId: Int) {
    companion object {
        const val ROUTE = "profile/{userId}"
    }

}