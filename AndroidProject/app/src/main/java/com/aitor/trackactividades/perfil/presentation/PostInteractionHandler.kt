package com.aitor.trackactividades.perfil.presentation

import com.aitor.trackactividades.core.token.TokenManager
import com.aitor.trackactividades.core.userPreferences.UserPreferences
import com.aitor.trackactividades.feed.domain.AddCommentUseCase
import com.aitor.trackactividades.feed.domain.AddLikeUseCase
import com.aitor.trackactividades.feed.domain.GetCommentsUseCase
import com.aitor.trackactividades.feed.domain.RemoveLikeUseCase
import com.aitor.trackactividades.feed.presentation.model.Comment
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

class PostInteractionHandler @Inject constructor(
    private val tokenManager: TokenManager,
    private val userPreferences: UserPreferences,
    private val addLike: AddLikeUseCase,
    private val removeLike: RemoveLikeUseCase,
    private val getComments: GetCommentsUseCase,
    private val addComment: AddCommentUseCase
) {
    suspend fun toggleLike(publicationId: Long, isLiked: Boolean): Result<Boolean> {
        return try {
            val token = tokenManager.getToken() ?: throw Exception("No hay token")
            val userId = userPreferences.getId() ?: throw Exception("No hay userId")
            if (isLiked) {
                removeLike(token, publicationId, userId)
                Result.success(false)
            } else {
                addLike(token, publicationId, userId)
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getComments(publicationId: Long): Result<List<Comment>> {
        return try {
            val token = tokenManager.getToken() ?: throw Exception("No hay token")
            Result.success(getComments(token, publicationId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addComment(publicationId: Long, comentario: String): Result<Comment> {
        return try {
            val token = tokenManager.getToken() ?: throw Exception("No hay token")
            val userId = userPreferences.getId() ?: throw Exception("No hay userId")
            val comment = addComment(token, publicationId, userId, comentario)
            Result.success(comment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun tiempoTranscurrido(fecha: LocalDateTime): String {
        val ahora = LocalDateTime.now()
        val duracion = Duration.between(fecha, ahora)

        return when {
            duracion.toMinutes() < 1 -> "justo ahora"
            duracion.toMinutes() < 60 -> "hace ${duracion.toMinutes()} minuto(s)"
            duracion.toHours() < 24 -> "hace ${duracion.toHours()} hora(s)"
            duracion.toDays() == 1L -> "ayer"
            duracion.toDays() < 7 -> "hace ${duracion.toDays()} día(s)"
            duracion.toDays() < 30 -> "hace ${duracion.toDays() / 7} semana(s)"
            duracion.toDays() < 365 -> "hace ${duracion.toDays() / 30} mes(es)"
            else -> "hace ${duracion.toDays() / 365} año(s)"
        }
    }
}
