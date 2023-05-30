package com.kust.ermsmanager.data.repositories

import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.models.Task
import com.kust.ermslibrary.utils.FireStoreCollectionConstants
import com.kust.ermslibrary.utils.SharedPreferencesConstants
import com.kust.ermslibrary.utils.UiState
import kotlinx.coroutines.tasks.await

class TaskRepositoryImpl(
    private val database: FirebaseFirestore,
    private val sharedPreferences: SharedPreferences
) : TaskRepository {
    override fun getTasks(
        result: (UiState<List<Task>>) -> Unit
    ) {
        // get company id from shared preferences object
        val employeeJson = sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION, null)
        val employee = Gson().fromJson(employeeJson, Employee::class.java)
        val companyId = employee.companyId

        // get tasks from database and update UiState with result or error message
        val dbRef = database.collection(FireStoreCollectionConstants.TASKS)
            .whereEqualTo("companyId", companyId)
            .get().addOnSuccessListener { documents ->
            val tasks = mutableListOf<Task>()
            for (document in documents) {
                val task = document.toObject(Task::class.java)
                tasks.add(task)
            }
            result(UiState.Success(tasks))
        }.addOnFailureListener {
            result(UiState.Error(it.message.toString()))
        }
    }

    override fun createTask(
        task: Task,
        result: (UiState<Pair<Task, String>>) -> Unit
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

    override fun updateTask(
        task: Task,
        result: (UiState<Pair<Task, String>>) -> Unit
    ) {
        // update task to database
        database.collection(FireStoreCollectionConstants.TASKS).document(task.id).set(task)
            .addOnSuccessListener {
                result(UiState.Success(Pair(task, "Success")))
            }
            .addOnFailureListener {
                result(UiState.Error("Failed to Update Task"))
            }
    }

    override suspend fun deleteTask(
        id: String,
        result: (UiState<Pair<Task, String>>) -> Unit
    ) {
        // hash map to store the task id and the task object to use it in the result function
        val task = hashMapOf<String, Task>()
        // get task from database
        val documentSnapshot = database.collection(FireStoreCollectionConstants.TASKS).document(id).get().await()
        val taskModel = documentSnapshot.toObject(Task::class.java)
        taskModel?.let {
            // add the task id and the task object to the hash map
            task[id] = it
            // delete task from database
            database.collection(FireStoreCollectionConstants.TASKS).document(id).delete().await()
            result(UiState.Success(Pair(taskModel, "Successfully deleted task !")))
        }
    }
}