package com.kust.ermsemployee.data.repository

import com.kust.ermsemployee.data.model.EventModel
import com.kust.ermsemployee.utils.UiState

interface EventRepository {
    fun getEventList(result: (UiState<List<EventModel>>) -> Unit)
    fun createEvent(eventModel: EventModel?, result: (UiState<Pair<EventModel, String>>) -> Unit)
    fun updateEvent(eventModel: EventModel?, result: (UiState<Pair<EventModel, String>>) -> Unit)
    fun deleteEvent(eventModel: EventModel, result: (UiState<Pair<EventModel, String>>) -> Unit)
}