package com.kust.ermsemployee.data.repository

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kust.ermsemployee.data.model.TaskModel
import com.kust.ermsemployee.utils.FireStoreCollectionConstants
import com.kust.ermsemployee.utils.TaskStatus
import com.kust.ermsemployee.utils.UiState

class TaskRepositoryImpl(
    private val database: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val sharedPreferences: SharedPreferences
) : TaskRepository {
    override fun getTasks(
        result: (UiState<List<TaskModel>>) -> Unit
    ) {

        val uid = auth.currentUser?.uid
        // get tasks from database and update UiState with result or error message
        database.collection(FireStoreCollectionConstants.TASKS)
            .whereEqualTo("assigneeId", uid)
            .get().addOnSuccessListener { documents ->
            val tasks = mutableListOf<TaskModel>()
            for (document in documents) {
                val task = document.toObject(TaskModel::class.java)
                tasks.add(task)
            }
            result(UiState.Success(tasks))
        }.addOnFailureListener {
            result(UiState.Error(it.localizedMessage ?: "Error occurred"))
        }
    }

    override suspend fun updateTask(
        task: TaskModel,
        result: (UiState<Pair<TaskModel, String>>) -> Unit
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