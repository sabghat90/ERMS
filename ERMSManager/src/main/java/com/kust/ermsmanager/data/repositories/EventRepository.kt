package com.kust.ermsmanager.data.repositories

import com.kust.ermsmanager.data.models.EventModel
import com.kust.ermsmanager.utils.UiState

interface EventRepository {
    fun getEventList(eventModel: EventModel?, result: (UiState<List<EventModel>>) -> Unit)
    fun createEvent(eventModel: EventModel?, result: (UiState<Pair<EventModel, String>>) -> Unit)
    fun updateEvent(eventModel: EventModel?, result: (UiState<Pair<EventModel, String>>) -> Unit)
    fun deleteEvent(eventModel: EventModel, result: (UiState<Pair<EventModel, String>>) -> Unit)
}