package com.kust.ermsemployee.ui.events

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kust.ermsemployee.data.model.EventModel
import com.kust.ermsemployee.data.repository.EventRepository
import com.kust.ermsemployee.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {
    private val _getEventList = MutableLiveData<UiState<List<EventModel>>>()
    val getEventList : LiveData<UiState<List<EventModel>>>
        get() = _getEventList

    private val _createEvent = MutableLiveData<UiState<Pair<EventModel, String>>>()
    val createEvent : LiveData<UiState<Pair<EventModel, String>>>
        get() = _createEvent

    private val _updateEvent = MutableLiveData<UiState<Pair<EventModel, String>>>()
    val updateEvent : LiveData<UiState<Pair<EventModel, String>>>
        get() = _updateEvent

    private val _deleteEvent = MutableLiveData<UiState<Pair<EventModel, String>>>()
    val deleteEvent : LiveData<UiState<Pair<EventModel, String>>>
        get() = _deleteEvent

    init {
        getEventList()
    }

    private fun getEventList() {
        _getEventList.value = UiState.Loading
        eventRepository.getEventList() {
            _getEventList.value = it
        }
    }

    fun createEvent(eventModel: EventModel) {
        _createEvent.value = UiState.Loading
        eventRepository.createEvent(eventModel) {
            _createEvent.value = it
        }
    }

    fun updateEvent(eventModel: EventModel) {
        _updateEvent.value = UiState.Loading
        eventRepository.updateEvent(eventModel) {
            _updateEvent.value = it
        }
    }

    fun deleteEvent(eventModel: EventModel) {
        _deleteEvent.value = UiState.Loading
        eventRepository.deleteEvent(eventModel) {
            _deleteEvent.value = it
        }
    }
}