package com.kust.ermsmanager.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kust.ermsmanager.data.models.TaskModel
import com.kust.ermsmanager.utils.UiState
import kotlinx.coroutines.tasks.await

class TaskRepositoryImpl(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore
) : TaskRepository {
    override fun getTasks(
        taskModel: TaskModel,
        result: (UiState<List<TaskModel>>) -> Unit
    ) {
        // get tasks from database and update UiState with result or error message
        database.collection("tasks").get().addOnSuccessListener { documents ->
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


    override suspend fun getTask(id: Int, result: (UiState<TaskModel>) -> Unit) {
        // get task from database
        val documentSnapshot = database.collection("tasks").document(id.toString()).get().await()
        val task = documentSnapshot.toObject(TaskModel::class.java)
        task?.let {
            result(UiState.Success(it))
        }
    }

    override suspend fun createTask(
        task: TaskModel,
        result: (UiState<Pair<TaskModel, String>>) -> Unit
    ) {
        // create task to database
        database.collection("tasks").document().set(task).await()
        result(UiState.Success(Pair(task, "Success")))
    }

    override suspend fun updateTask(
        task: TaskModel,
        result: (UiState<Pair<TaskModel, String>>) -> Unit
    ) {
        // update task to database
        database.collection("tasks").document(task.id.toString()).set(task).await()
        result(UiState.Success(Pair(task, "Success")))
    }

    override suspend fun deleteTask(id: Int, result: (UiState<Pair<TaskModel, String>>) -> Unit) {
        // delete task from database
        database.collection("tasks").document(id.toString()).delete().await()
        result(UiState.Success(Pair(TaskModel(), "Success")))
    }

}