package com.kust.ermsemployee.data.repository

import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.models.Event
import com.kust.ermslibrary.utils.FireStoreCollectionConstants
import com.kust.ermslibrary.utils.SharedPreferencesConstants
import com.kust.ermslibrary.utils.UiState

class EventRepositoryImpl(
    private val database: FirebaseFirestore,
    private val sharedPreferences: SharedPreferences
) : EventRepository {

    override fun getEventList(result: (UiState<List<Event>>) -> Unit) {
        // get company id from shared preferences object
        val employeeJson = sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION, null)
        val employee = Gson().fromJson(employeeJson, Employee::class.java)
        val companyId = employee.companyId
        // get event list from firebase where companyId is equal to eventModel.companyId
        val documentReference = database.collection("events")
        documentReference.whereEqualTo("companyId", companyId)
            .get()
            .addOnSuccessListener { documents ->
                val eventList = mutableListOf<Event>()
                for (document in documents) {
                    val event = document.toObject(Event::class.java)
                    eventList.add(event)
                }
                result(UiState.Success(eventList))
            }
            .addOnFailureListener { exception ->
                result(UiState.Error(exception.localizedMessage?.toString() ?: "Error"))
            }
    }

    override fun createEvent(
        event: Event?,
        result: (UiState<Pair<Event, String>>) -> Unit
    ) {
        // get company id from shared preferences object
        val employeeJson = sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION, null)
        val employee = Gson().fromJson(employeeJson, Employee::class.java)
        val companyId = employee.companyId
        // create event in firebase
        val documentReference = database.collection(FireStoreCollectionConstants.EVENTS).document()
        if (event != null) {
            event.companyId = companyId
        }
        if (event != null) {
            event.id = documentReference.id
        }
        if (event != null) {
            documentReference.set(event)
                .addOnSuccessListener {
                    result(UiState.Success(Pair(event, "Event created successfully")))
                }
                .addOnFailureListener { exception ->
                    result(UiState.Error(exception.localizedMessage?.toString() ?: "Error"))
                }
        }
    }

    override fun updateEvent(
        event: Event?,
        result: (UiState<Pair<Event, String>>) -> Unit
    ) {
        // update event in firebase
        val documentReference = event?.let { database.collection(FireStoreCollectionConstants.EVENTS).document(it.id) }
        if (event != null) {
            documentReference?.set(event)?.addOnSuccessListener {
                result(UiState.Success(Pair(event, "Event updated successfully")))
            }?.addOnFailureListener { exception ->
                result(UiState.Error(exception.localizedMessage?.toString() ?: "Error"))
            }
        }
    }

    override fun deleteEvent(
        event: Event,
        result: (UiState<Pair<Event, String>>) -> Unit
    ) {
        // delete event from firebase
        val documentReference = database.collection(FireStoreCollectionConstants.EVENTS).document(event.id)
        documentReference.delete()
            .addOnSuccessListener {
                result(UiState.Success(Pair(event, "Event deleted successfully")))
            }
            .addOnFailureListener { exception ->
                result(UiState.Error(exception.localizedMessage?.toString() ?: "Error"))
            }
    }
}