package com.kust.ermsmanager.data.repositories

import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.kust.ermsmanager.data.models.EmployeeModel
import com.kust.ermsmanager.data.models.TaskModel
import com.kust.ermsmanager.utils.FireStoreCollectionConstants
import com.kust.ermsmanager.utils.SharedPreferencesConstants
import com.kust.ermsmanager.utils.UiState
import kotlinx.coroutines.tasks.await

class TaskRepositoryImpl(
    private val database: FirebaseFirestore,
    private val sharedPreferences: SharedPreferences
) : TaskRepository {
    override fun getTasks(
        result: (UiState<List<TaskModel>>) -> Unit
    ) {
        // get company id from shared preferences object
        val employeeJson = sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION, null)
        val employee = Gson().fromJson(employeeJson, EmployeeModel::class.java)
        val companyId = employee.companyId

        // get tasks from database and update UiState with result or error message
        database.collection(FireStoreCollectionConstants.TASKS)
            .whereEqualTo("companyId", companyId)
            .get().addOnSuccessListener { documents ->
            val tasks = mutableListOf<TaskModel>()
            for (document in documents) {
                val task = document.toObject(TaskModel::class.java)
                tasks.add(task)
            }
            result(UiState.Success(tasks))
        }.addOnFailureListener {
            result(UiState.Error(it.message.toString()))
        }
    }

    override fun createTask(
        task: TaskModel,
        result: (UiState<Pair<TaskModel, String>>) -> Unit
    ) {
        val documentReference = database.collection(FireStoreCollectionConstants.TASKS).document()
        task.id = documentReference.id
        // create task to database
        documentReference.set(task).addOnSuccessListener {
            result(UiState.Success(Pair(task, "Success")))
        }.addOnFailureListener {
            result(UiState.Error(it.message.toString()))
        }
    }

    override suspend fun updateTask(
        task: TaskModel,
        result: (UiState<Pair<TaskModel, String>>) -> Unit
    ) {
        // update task to database
        database.collection(FireStoreCollectionConstants.TASKS).document(task.id).set(task).await()
        result(UiState.Success(Pair(task, "Success")))
    }

    override suspend fun deleteTask(
        id: String,
        result: (UiState<Pair<TaskModel, String>>) -> Unit
    ) {
        // hash map to store the task id and the task object to use it in the result function
        val task = hashMapOf<String, TaskModel>()
        // get task from database
        val documentSnapshot = database.collection(FireStoreCollectionConstants.TASKS).document(id).get().await()
        val taskModel = documentSnapshot.toObject(TaskModel::class.java)
        taskModel?.let {
            // add the task id and the task object to the hash map
            task[id] = it
            // delete task from database
            database.collection(FireStoreCollectionConstants.TASKS).document(id).delete().await()
            result(UiState.Success(Pair(taskModel, "Successfully deleted task !")))
        }
    }

}