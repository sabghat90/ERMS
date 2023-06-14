package com.kust.ermsemployee.data.repository

import com.kust.ermslibrary.models.Event
import com.kust.ermslibrary.utils.UiState

interface EventRepository {
    suspend fun getEventList(result: (UiState<List<Event>>) -> Unit)
    suspend fun createEvent(event: Event?, result: (UiState<Pair<Event, String>>) -> Unit)
    suspend fun updateEvent(event: Event?, result: (UiState<Pair<Event, String>>) -> Unit)
    suspend fun deleteEvent(event: Event, result: (UiState<Pair<Event, String>>) -> Unit)
}