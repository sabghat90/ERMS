package com.kust.ermsemployee.ui.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.kust.ermsemployee.R
import com.kust.ermsemployee.databinding.TaskItemBinding
import com.kust.ermslibrary.models.Task
import com.kust.ermslibrary.utils.ConvertDateAndTimeFormat
import com.kust.ermslibrary.utils.TaskStatus
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

// task adapter using list adapter
class TaskListingAdapter(
    val context: Context,
    val onItemClicked: (Int, Task) -> Unit
) : ListAdapter<Task, TaskListingAdapter.TaskViewHolder>(DiffUtilCallback()) {

    var taskList: MutableList<Task> = arrayListOf()

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.bind(task)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    // receive event call from handler to update task list
    fun updateTaskList(taskList: MutableList<Task>) {
        this.taskList = taskList
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(private val binding: TaskItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {

            if (task.status == TaskStatus.APPROVED) {
                binding.imgStatus.visibility = RecyclerView.VISIBLE
            } else {
                val taskDueDate = task.deadline
                val format = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
                try {
                    val date = format.parse(taskDueDate)
                    val currentDate = Timestamp.now().toDate()

                    if (date != null) {
                        if (date.before(currentDate)) {
                            binding.tvTaskDeadlineStatus.visibility = RecyclerView.VISIBLE
                            binding.tvTaskDeadlineStatus.text = context.getString(R.string.expired)
                            binding.tvTaskDeadlineStatus.setTextColor(binding.root.context.getColor(android.R.color.holo_red_dark))
                        } else {
                            binding.tvTaskDeadlineStatus.visibility = RecyclerView.VISIBLE
                            binding.tvTaskDeadlineStatus.text = context.getString(R.string.upcoming)
                            // change status color to green
                            binding.tvTaskDeadlineStatus.setTextColor(binding.root.context.getColor(android.R.color.holo_green_dark))
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

            val creationDateFormatted = ConvertDateAndTimeFormat().formatDate(taskCreationDate)
            val creationTimeFormatted = ConvertDateAndTimeFormat().formatTime(taskCreationDate)

            binding.tvTaskName.text = task.name
            binding.tvTaskDescription.text = task.description
            binding.tvStatus.text = task.status
            binding.tvTaskCreatedDate.text = context.getString(R.string.date_time, creationDateFormatted, creationTimeFormatted)
            binding.cardTask.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClicked(position, taskList[position])
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