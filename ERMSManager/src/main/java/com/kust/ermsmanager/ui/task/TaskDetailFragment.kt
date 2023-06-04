package com.kust.ermsmanager.ui.task

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.kust.ermslibrary.models.NotificationData
import com.kust.ermslibrary.models.PushNotification
import com.kust.ermslibrary.models.Task
import com.kust.ermslibrary.services.NotificationService
import com.kust.ermslibrary.utils.ConvertDateAndTimeFormat
import com.kust.ermslibrary.utils.TaskPoints
import com.kust.ermslibrary.utils.TaskStatus
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.toast
import com.kust.ermsmanager.R
import com.kust.ermsmanager.databinding.FragmentTaskDetailBinding
import com.kust.ermsmanager.ui.employee.EmployeeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class TaskDetailFragment : Fragment() {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    // task object
    private lateinit var task: Task

    private val taskViewModel: TaskViewModel by viewModels()
    private val employeeViewModel: EmployeeViewModel by viewModels()

    @Inject
    lateinit var notificationService: NotificationService

    private lateinit var progressDialog: Dialog

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

        progressDialog = Dialog(requireContext())
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog.setContentView(R.layout.custom_progress_dialog)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)


        updateUI()
        observer()
    }

    private fun observer() {
        // observe the delete task result
        taskViewModel.deleteTask.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    progressDialog.show()
                }

                is UiState.Success -> {
                    progressDialog.dismiss()
                    toast(it.data.toString())
                }

                is UiState.Error -> {
                    progressDialog.dismiss()
                    toast(it.error)
                }
            }
        }
        employeeViewModel.addPoints.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    progressDialog.show()
                }

                is UiState.Success -> {
                    progressDialog.dismiss()
                    sendNotification(TaskStatus.APPROVED)
                    toast(it.data)
                }

                is UiState.Error -> {
                    progressDialog.dismiss()
                    toast(it.error)
                }
            }
        }
        employeeViewModel.removePoints.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    progressDialog.show()
                }

                is UiState.Success -> {
                    progressDialog.dismiss()
                    sendNotification(TaskStatus.RESUBMITTED)
                    toast(it.data)
                    findNavController().navigate(R.id.action_taskDetailFragment_to_taskListingFragment)
                }

                is UiState.Error -> {
                    progressDialog.dismiss()
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
                    NotificationData(
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
                    NotificationData(
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
                        employeeViewModel.removePoints(task.assigneeId, TaskPoints.APPROVED)
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
                        employeeViewModel.addPoints(task.assigneeId, TaskPoints.RESUBMITTED)
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