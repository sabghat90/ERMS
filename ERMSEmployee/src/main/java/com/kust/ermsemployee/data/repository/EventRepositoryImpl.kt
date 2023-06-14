package com.kust.ermsemployee.data.repository

import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.models.Event
import com.kust.ermslibrary.utils.FireStoreCollectionConstants
import com.kust.ermslibrary.utils.SharedPreferencesConstants
import com.kust.ermslibrary.utils.UiState
import kotlinx.coroutines.tasks.await

class EventRepositoryImpl(
    private val database: FirebaseFirestore,
    private val sharedPreferences: SharedPreferences
) : EventRepository {

    private val employeeJson =
        sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION, null)
    val employee: Employee = Gson().fromJson(employeeJson, Employee::class.java)
    private val companyId = employee.companyId

    override suspend fun getEventList(result: (UiState<List<Event>>) -> Unit) {
        return try {
            val docRef = database.collection(FireStoreCollectionConstants.EVENTS)
                .whereEqualTo("companyId", companyId)

            docRef.get().await()
                .let { querySnapshot ->
                    val eventList = mutableListOf<Event>()
                    querySnapshot.forEach { documentSnapshot ->
                        val event = documentSnapshot.toObject(Event::class.java)
                        eventList.add(event)
                    }
                    result.invoke(UiState.Success(eventList))
                }
        } catch (e: Exception) {
            result.invoke(UiState.Error(e.localizedMessage ?: "Error"))
        }
    }

    override suspend fun createEvent(
        event: Event?,
        result: (UiState<Pair<Event, String>>) -> Unit
    ) {
        return try {
            val docRef = database.collection(FireStoreCollectionConstants.EVENTS).document()
            event?.id = docRef.id

            docRef.set(event!!).await()
                .let {
                    result.invoke(UiState.Success(Pair(event, "Event created successfully")))
                }
        } catch (e: Exception) {
            result.invoke(UiState.Error(e.localizedMessage ?: "Error"))
        }
    }

    override suspend fun updateEvent(
        event: Event?,
        result: (UiState<Pair<Event, String>>) -> Unit
    ) {
        return try {
            val docRef = database.collection(FireStoreCollectionConstants.EVENTS).document(event!!.id)
            docRef.set(event).await()
                .let {
                    result.invoke(UiState.Success(Pair(event, "Event updated successfully")))
                }
        } catch (e: Exception) {
            result.invoke(UiState.Error(e.localizedMessage ?: "Error"))
        }
    }

    override suspend fun deleteEvent(
        event: Event,
        result: (UiState<Pair<Event, String>>) -> Unit
    ) {
        return try {
            val docRef = database.collection(FireStoreCollectionConstants.EVENTS).document(event.id)
            docRef.delete().await()
                .let {
                    result.invoke(UiState.Success(Pair(event, "Event deleted successfully")))
                }
        } catch (e: Exception) {
            result.invoke(UiState.Error(e.localizedMessage ?: "Error"))
        }
    }
}