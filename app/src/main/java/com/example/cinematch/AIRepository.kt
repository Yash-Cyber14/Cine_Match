package com.example.cinematch


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class AIRepository(private val apiService: OpenRouterService) {

    // Send user message and get AI response
    suspend fun sendMessage(
        messages: List<Message>,
        model: String = "tngtech/deepseek-r1t2-chimera:free"
    ): Message = safeApiCall {
        val request = ChatRequest(
            model = model,
            messages = messages
        )
        val response = apiService.createChat(
            auth = "Bearer ${BuildConfig.openrouterai_api_key}",
            request = request
        )
        response.choices.firstOrNull()?.message
            ?: Message(role = "assistant", content = "No response from AI")
    }

    // Generic safe API call wrapper
    private suspend fun <T> safeApiCall(block: suspend () -> T): T = withContext(Dispatchers.IO) {
        try {
            block()
        } catch (e: IOException) {
            throw NetworkException("No Internet Connection", e)
        } catch (e: HttpException) {
            throw UnknownException("HTTP ${e.code()}: ${e.response()?.errorBody()?.string()}", e)
        } catch (e: Exception) {
            throw UnknownException("Unknown Error: ${e.localizedMessage}", e)
        }
    }

}
