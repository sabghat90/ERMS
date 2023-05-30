package com.kust.ermsmanager.data.repositories

import com.kust.ermslibrary.models.Event
import com.kust.ermslibrary.utils.UiState

interface EventRepository {
    fun getEventList(result: (UiState<List<Event>>) -> Unit)
    fun createEvent(event: Event?, result: (UiState<Pair<Event, String>>) -> Unit)
    fun updateEvent(event: Event?, result: (UiState<Pair<Event, String>>) -> Unit)
    fun deleteEvent(event: Event, result: (UiState<Pair<Event, String>>) -> Unit)
}