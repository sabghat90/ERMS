package com.kust.ermsmanager.ui.task

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kust.ermsmanager.data.models.TaskModel
import com.kust.ermsmanager.databinding.TaskItemBinding

// task adapter using list adapter
class TaskListingAdapter(
    val onItemClicked: (Int, TaskModel) -> Unit
) : ListAdapter<TaskModel, TaskListingAdapter.TaskViewHolder>(DiffUtilCallback()) {

    var taskList: MutableList<TaskModel> = arrayListOf()

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.bind(task, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    // receive event call from handler to update task list
    fun updateTaskList(taskList: MutableList<TaskModel>) {
        this.taskList = taskList
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(private val binding: TaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: TaskModel, position: Int) {
            binding.tvTaskName.text = task.name
            binding.tvTaskDescription.text = task.description
            binding.tvTaskStatus.text = task.status
            binding.tvTaskCreatedDate.text = task.createdDate
            binding.cardTask.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked(position, taskList[position])
                }
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<TaskModel>() {
        override fun areItemsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TaskModel, newItem: TaskModel): Boolean {
            return oldItem == newItem
        }
    }

}