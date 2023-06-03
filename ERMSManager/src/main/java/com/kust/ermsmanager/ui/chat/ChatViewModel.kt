package com.kust.ermsmanager.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kust.ermslibrary.models.Message
import com.kust.ermslibrary.utils.UiState
import com.kust.ermsmanager.data.repositories.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val _sendMessage = MutableLiveData<UiState<String>>()
    val sendMessage: LiveData<UiState<String>>
        get() = _sendMessage

    private val _getChatList = MutableLiveData<List<Message>>()
    val getChatList: LiveData<List<Message>>
        get() = _getChatList

    fun sendMessage(message: Message, receiverId: String) {
        _sendMessage.value = UiState.Loading
        viewModelScope.launch {
            repository.sendMessage(message, receiverId) { result ->
                _sendMessage.value = result
            }
        }
    }

    fun getChatList(receiverId: String) {
        viewModelScope.launch {
            repository.getMessages(receiverId) { result ->
                _getChatList.value = result
            }
        }
    }
}