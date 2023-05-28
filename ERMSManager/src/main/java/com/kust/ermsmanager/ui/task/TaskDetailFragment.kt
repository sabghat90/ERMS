package com.kust.ermsmanager.ui.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kust.ermsmanager.R
import com.kust.ermsmanager.data.models.NotificationModel
import com.kust.ermsmanager.data.models.PushNotification
import com.kust.ermsmanager.data.models.TaskModel
import com.kust.ermsmanager.databinding.FragmentTaskDetailBinding
import com.kust.ermsmanager.services.NotificationService
import com.kust.ermsmanager.ui.employee.EmployeeViewModel
import com.kust.ermsmanager.utils.ConvertDateAndTimeFormat
import com.kust.ermsmanager.utils.TaskStatus
import com.kust.ermsmanager.utils.UiState
import com.kust.ermsmanager.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TaskDetailFragment : Fragment() {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    // task object
    private lateinit var task: TaskModel

    private val taskViewModel: TaskViewModel by viewModels()
    private val employeeViewModel: EmployeeViewModel by viewModels()

    private val notificationService = NotificationService()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTaskDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        updateUI()
        observer()
    }

    private fun observer() {
        // observe the delete task result
        taskViewModel.deleteTask.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {

                }

                is UiState.Success -> {
                    toast(it.data.toString())
                }

                is UiState.Error -> {
                    toast(it.error)
                }
            }
        }
        employeeViewModel.addPoints.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {

                }

                is UiState.Success -> {
                    sendNotification(TaskStatus.APPROVED)
                    toast(it.data)
                }

                is UiState.Error -> {
                    toast(it.error)
                }
            }
        }
        employeeViewModel.removePoints.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {

                }

                is UiState.Success -> {
                    sendNotification(TaskStatus.RESUBMITTED)
                    toast(it.data)
                    findNavController().navigate(R.id.action_taskDetailFragment_to_taskListingFragment)
                }

                is UiState.Error -> {
                    toast(it.error)
                }
            }
        }
    }

    private fun sendNotification(state: String) {
        when (state) {
            TaskStatus.RESUBMITTED -> {
                val title = "Task Resubmitted"
                val message = "Your task has been resubmitted by your manager"
                PushNotification(
                    NotificationModel(
                        title = title,
                        body = message,
                    ),
                    to = task.assigneeFCMToken
                ).also {
                    notificationService.sendNotification(it)
                }
            }
            TaskStatus.APPROVED -> {
                val title = "Task Approved"
                val message = "Your task has been approved by your manager"
                PushNotification(
                    NotificationModel(
                        title = title,
                        body = message,
                    ),
                    to = task.assigneeFCMToken
                ).also {
                    notificationService.sendNotification(it)
                }
            }
        }
    }

    private fun updateUI() {
        // update the UI with task bundle data
        task = arguments?.getParcelable("task")!!

        val taskCreateDateFormatted = ConvertDateAndTimeFormat().formatDate(task.createdDate)
        val taskCreateTimeFormatted = ConvertDateAndTimeFormat().formatTime(task.createdDate)

        val taskDueDateFormatted = ConvertDateAndTimeFormat().formatDate(task.deadline)
        val taskDueTimeFormatted = ConvertDateAndTimeFormat().formatTime(task.deadline)

        binding.tvTaskName.text = task.name
        binding.tvTaskDescription.text = task.description
        binding.tvAssignTo.text = task.assigneeName
        binding.tvCreatedDate.text =
            getString(R.string.date_time, taskCreateDateFormatted, taskCreateTimeFormatted)
        binding.tvDeadline.text =
            getString(R.string.date_time, taskDueDateFormatted, taskDueTimeFormatted)
        binding.tvTaskStatus.text = task.status

        if (task.status == TaskStatus.APPROVED) {
            binding.btnApproveTask.visibility = View.GONE
        }

        when (task.status) {
            TaskStatus.PENDING -> {
                binding.btnApproveTask.visibility = View.GONE
                binding.btnResubmitTask.visibility = View.GONE
            }
            TaskStatus.RESUBMITTED -> {
                binding.btnApproveTask.visibility = View.VISIBLE
                binding.btnResubmitTask.visibility = View.VISIBLE
            }
            TaskStatus.APPROVED -> {
                binding.btnApproveTask.visibility = View.GONE
                binding.btnResubmitTask.visibility = View.VISIBLE
                binding.btnResubmitTask.setOnClickListener {
                    task.status = TaskStatus.RESUBMITTED
                    taskViewModel.updateTask(task)
                    lifecycleScope.launch {
                        employeeViewModel.removePoints(task.assigneeId)
                    }
                }
            }
            TaskStatus.IN_PROGRESS -> {
                binding.btnApproveTask.visibility = View.GONE
                binding.btnResubmitTask.visibility = View.GONE
            }
            TaskStatus.SUBMITTED -> {
                binding.btnApproveTask.visibility = View.VISIBLE
                binding.btnResubmitTask.visibility = View.VISIBLE
                binding.btnApproveTask.setOnClickListener {
                    task.status = TaskStatus.APPROVED
                    taskViewModel.updateTask(task)
                    lifecycleScope.launch {
                        employeeViewModel.addPoints(task.assigneeId)
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete -> {
                CoroutineScope(Dispatchers.IO).launch {
                    taskViewModel.deleteTask(task.id)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.task_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}