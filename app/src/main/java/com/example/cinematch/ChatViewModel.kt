package com.example.cinematch

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: AIRepository
) : ViewModel() {

    var messages = mutableStateListOf<Message>()
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun sendUserMessage(content: String) {
        if (content.isBlank()) return
        messages.add(Message(role = "user", content = content))

        viewModelScope.launch {
            isLoading = true
            try {
                val aiMessage = repository.sendMessage(messages)
                messages.add(aiMessage)
            } catch (e: Exception) {
                messages.add(Message(role = "assistant", content = "Error: ${e.message}"))
            } finally {
                isLoading = false
            }
        }
    }
}
