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

    override fun createTask(
        task: TaskModel,
        result: (UiState<Pair<TaskModel, String>>) -> Unit
    ) {
        val documentReference = database.collection("tasks").document()
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
        database.collection("tasks").document(task.id.toString()).set(task).await()
        result(UiState.Success(Pair(task, "Success")))
    }

    override suspend fun deleteTask(
        id: String,
        result: (UiState<Pair<TaskModel, String>>) -> Unit
    ) {
        // hash map to store the task id and the task object to use it in the result function
        val task = hashMapOf<String, TaskModel>()
        // get task from database
        val documentSnapshot = database.collection("tasks").document(id).get().await()
        val taskModel = documentSnapshot.toObject(TaskModel::class.java)
        taskModel?.let {
            // add the task id and the task object to the hash map
            task[id] = it
            // delete task from database
            database.collection("tasks").document(id).delete().await()
            result(UiState.Success(Pair(taskModel, "Successfully deleted task !")))
        }
    }

}