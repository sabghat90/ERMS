package com.kust.ermsmanager.ui.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kust.ermslibrary.models.Event
import com.kust.ermslibrary.utils.UiState
import com.kust.ermsmanager.data.repositories.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    init {
        getEventList()
    }

    fun getEventList() {
        _getEventList.value = UiState.Loading
        eventRepository.getEventList {
            _getEventList.value = it
        }
    }

    fun createEvent(event: Event) {
        _createEvent.value = UiState.Loading
        eventRepository.createEvent(event) {
            _createEvent.value = it
        }
    }

    fun updateEvent(event: Event) {
        _updateEvent.value = UiState.Loading
        eventRepository.updateEvent(event) {
            _updateEvent.value = it
        }
    }

    fun deleteEvent(event: Event) {
        _deleteEvent.value = UiState.Loading
        eventRepository.deleteEvent(event) {
            _deleteEvent.value = it
        }
    }
}