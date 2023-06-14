package com.kust.ermsemployee.ui.events

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kust.ermsemployee.data.repository.EventRepository
import com.kust.ermslibrary.models.Event
import com.kust.ermslibrary.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {
    private val _getEventList = MutableLiveData<UiState<List<Event>>>()
    val getEventList : LiveData<UiState<List<Event>>>
        get() = _getEventList

    private val _createEvent = MutableLiveData<UiState<Pair<Event, String>>>()
    val createEvent : LiveData<UiState<Pair<Event, String>>>
        get() = _createEvent

    private val _updateEvent = MutableLiveData<UiState<Pair<Event, String>>>()
    val updateEvent : LiveData<UiState<Pair<Event, String>>>
        get() = _updateEvent

    private val _deleteEvent = MutableLiveData<UiState<Pair<Event, String>>>()
    val deleteEvent : LiveData<UiState<Pair<Event, String>>>
        get() = _deleteEvent

    fun getEventList() {
        _getEventList.value = UiState.Loading
        viewModelScope.launch {
            eventRepository.getEventList {
                _getEventList.value = it
            }
        }
    }

    suspend fun createEvent(event: Event) {
        _createEvent.value = UiState.Loading
        viewModelScope.launch {
            eventRepository.createEvent(event) {
                _createEvent.value = it
            }
        }
    }

    suspend fun updateEvent(event: Event) {
        _updateEvent.value = UiState.Loading
        viewModelScope.launch {
            eventRepository.updateEvent(event) {
                _updateEvent.value = it
            }
        }
    }

    suspend fun deleteEvent(event: Event) {
        _deleteEvent.value = UiState.Loading
        viewModelScope.launch {
            eventRepository.deleteEvent(event) {
                _deleteEvent.value = it
            }
        }
    }
}