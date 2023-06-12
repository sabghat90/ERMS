package com.kust.ermsmanager.ui.task

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.kust.ermslibrary.models.Task
import com.kust.ermslibrary.utils.ConvertDateAndTimeFormat
import com.kust.ermslibrary.utils.TaskStatus
import com.kust.ermsmanager.databinding.TaskItemBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import com.kust.ermslibrary.R as LibraryR

// task adapter using list adapter
class TaskListingAdapter(
    val context: Context,
    val onItemClicked: (Int, Task) -> Unit
) : ListAdapter<Task, TaskListingAdapter.TaskViewHolder>(DiffUtilCallback()) {

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }


    inner class TaskViewHolder(private val binding: TaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {

            val taskDueDate = task.deadline

            if (task.status == TaskStatus.COMPLETED) {
                binding.imgTaskStatus.visibility = RecyclerView.VISIBLE
            } else {
                val format = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
                try {
                    val date = format.parse(taskDueDate)
                    val currentDate = Timestamp.now().toDate()

                    if (date != null) {
                        if (date.before(currentDate)) {
                            binding.tvTaskDeadlineStatus.visibility = RecyclerView.VISIBLE
                            binding.tvTaskDeadlineStatus.text = context.getString(LibraryR.string.expired)
                            binding.tvTaskDeadlineStatus.setTextColor(
                                binding.root.context.getColor(
                                    android.R.color.holo_red_dark
                                )
                            )
                        } else {
                            binding.tvTaskDeadlineStatus.visibility = RecyclerView.VISIBLE
                            binding.tvTaskDeadlineStatus.text = context.getString(LibraryR.string.upcoming)
                            // change status color to green
                            binding.tvTaskDeadlineStatus.setTextColor(
                                binding.root.context.getColor(
                                    android.R.color.holo_green_dark
                                )
                            )
                        }
                    } else {
                        // Handle invalid date format
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                    // Handle date parsing error
                }
            }

            val taskCreationDate = task.createdDate

            Log.d("TaskListingAdapter", "taskCreationDate: $taskCreationDate")

            val creationDateFormatted = ConvertDateAndTimeFormat().formatDate(taskCreationDate)
            val creationTimeFormatted = ConvertDateAndTimeFormat().formatTime(taskCreationDate)

            binding.tvTaskName.text = task.title
            binding.tvTaskDescription.text = task.description
            binding.taskStatus.text = task.status
            binding.tvTaskCreatedDate.text =
                context.getString(LibraryR.string.date_time, creationDateFormatted, creationTimeFormatted)
            binding.cardTask.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked(position, getItem(position))
                }
            }
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }

}