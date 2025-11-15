package com.example.cinematch

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenRouterService {
    @POST("api/v1/chat/completions")
    suspend fun createChat(
        @Header("Authorization") auth: String,
        @Body request: ChatRequest
    ): ChatResponse
}


data class Message(val role: String, val content: String)
data class ChatRequest(val model: String, val messages: List<Message>, val temperature: Float? = 0.7f)
data class ChatResponse(val choices: List<Choice>)
data class Choice(val message: Message)
