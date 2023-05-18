package com.kust.ermsmanager.data.repositories

import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.data.models.EventModel
import com.kust.ermsmanager.utils.SharedPreferencesConstants
import com.kust.ermsmanager.utils.UiState

class EventRepositoryImpl(
    private val database: FirebaseFirestore,
    sharedPreferences: SharedPreferences
) : EventRepository {

    // get companyId from shared preferences and store it in a variable
    private val employeeJson = sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION, null)
    private val employee = Gson().fromJson(employeeJson, EmployeeModel::class.java)
    private val companyId = employee.companyId

    override fun getEventList(eventModel: EventModel?, result: (UiState<List<EventModel>>) -> Unit) {
        // get event list from firebase where companyId is equal to eventModel.companyId
        val documentReference = database.collection("events")
        documentReference.whereEqualTo("companyId", companyId)
            .get()
            .addOnSuccessListener { documents ->
                val eventList = mutableListOf<EventModel>()
                for (document in documents) {
                    val event = document.toObject(EventModel::class.java)
                    eventList.add(event)
                }
                result(UiState.Success(eventList))
            }
            .addOnFailureListener { exception ->
                result(UiState.Error(exception.message.toString()))
            }
    }

    override fun createEvent(
        eventModel: EventModel?,
        result: (UiState<Pair<EventModel, String>>) -> Unit
    ) {
        // create event in firebase
        val documentReference = database.collection("events").document()
        if (eventModel != null) {
            eventModel.companyId = companyId
        }
        if (eventModel != null) {
            eventModel.id = documentReference.id
        }
        if (eventModel != null) {
            documentReference.set(eventModel)
                .addOnSuccessListener {
                    result(UiState.Success(Pair(eventModel, "Event created successfully")))
                }
                .addOnFailureListener { exception ->
                    result(UiState.Error(exception.message.toString()))
                }
        }
    }

    override fun updateEvent(
        eventModel: EventModel?,
        result: (UiState<Pair<EventModel, String>>) -> Unit
    ) {
        // update event in firebase
        val documentReference = eventModel?.let { database.collection("events").document(it.id) }
        if (eventModel != null) {
            documentReference?.set(eventModel)?.addOnSuccessListener {
                result(UiState.Success(Pair(eventModel, "Event updated successfully")))
            }?.addOnFailureListener { exception ->
                result(UiState.Error(exception.message.toString()))
            }
        }
    }

    override fun deleteEvent(
        eventModel: EventModel,
        result: (UiState<Pair<EventModel, String>>) -> Unit
    ) {
        // delete event from firebase
        val documentReference = database.collection("events").document(eventModel.id)
        documentReference.delete()
            .addOnSuccessListener {
                result(UiState.Success(Pair(eventModel, "Event deleted successfully")))
            }
            .addOnFailureListener { exception ->
                result(UiState.Error(exception.message.toString()))
            }
    }
}