package com.kust.ermsemployee.data.repository

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kust.ermslibrary.models.Task
import com.kust.ermslibrary.utils.FireStoreCollectionConstants
import com.kust.ermslibrary.utils.UiState

class TaskRepositoryImpl(
    private val database: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val sharedPreferences: SharedPreferences
) : TaskRepository {
    override fun getTasks(
        result: (UiState<List<Task>>) -> Unit
    ) {

        val uid = auth.currentUser?.uid
        // get tasks from database and update UiState with result or error message
        database.collection(FireStoreCollectionConstants.TASKS)
            .whereEqualTo("assigneeId", uid)
            .get().addOnSuccessListener { documents ->
            val tasks = mutableListOf<Task>()
            for (document in documents) {
                val task = document.toObject(Task::class.java)
                tasks.add(task)
            }
            result(UiState.Success(tasks))
        }.addOnFailureListener {
            result(UiState.Error(it.localizedMessage ?: "Error occurred"))
        }
    }

    override suspend fun updateTask(
        task: Task,
        result: (UiState<Pair<Task, String>>) -> Unit
    ) {
        // update the task status field in database and update UiState with result or error message
        database.collection(FireStoreCollectionConstants.TASKS)
            .document(task.id)
            .set(task)
            .addOnSuccessListener {
                result(UiState.Success(Pair(task, "Task updated successfully")))
            }.addOnFailureListener {
                result(UiState.Error(it.localizedMessage ?: "Error occurred"))
            }
    }

}