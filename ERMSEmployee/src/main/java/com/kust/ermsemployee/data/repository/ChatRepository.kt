package com.kust.ermsemployee.data.repository

import com.kust.ermslibrary.models.Message
import com.kust.ermslibrary.utils.UiState

interface ChatRepository {
    suspend fun sendMessage(message: Message, receiverId: String, result: (UiState<String>) -> Unit)
    suspend fun getMessages(receiverId: String, result: (List<Message>) -> Unit)
}