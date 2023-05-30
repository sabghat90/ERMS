package com.kust.ermsmanager.data.repositories

import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.data.models.EventModel
import com.kust.ermslibrary.utils.FireStoreCollectionConstants
import com.kust.ermslibrary.utils.SharedPreferencesConstants
import com.kust.ermsmanager.utils.UiState

class EventRepositoryImpl(
    private val database: FirebaseFirestore,
    private val sharedPreferences: SharedPreferences
) : EventRepository {

    override fun getEventList(result: (UiState<List<EventModel>>) -> Unit) {
        // get company id from shared preferences object
        val employeeJson = sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION, null)
        val employee = Gson().fromJson(employeeJson, EmployeeModel::class.java)
        val companyId = employee.companyId
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
        // get company id from shared preferences object
        val employeeJson = sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION, null)
        val employee = Gson().fromJson(employeeJson, EmployeeModel::class.java)
        val companyId = employee.companyId
        // create event in firebase
        val documentReference = database.collection(FireStoreCollectionConstants.EVENTS).document()
        if (eventModel != null) {
            eventModel.id = documentReference.id
        }
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
        val documentReference = eventModel?.let { database.collection(FireStoreCollectionConstants.EVENTS).document(it.id) }
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
        val documentReference = database.collection(FireStoreCollectionConstants.EVENTS).document(eventModel.id)
        documentReference.delete()
            .addOnSuccessListener {
                result(UiState.Success(Pair(eventModel, "Event deleted successfully")))
            }
            .addOnFailureListener { exception ->
                result(UiState.Error(exception.message.toString()))
            }
    }
}