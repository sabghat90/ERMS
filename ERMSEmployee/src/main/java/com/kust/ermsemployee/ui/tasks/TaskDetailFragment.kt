package com.kust.ermsemployee.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kust.ermsemployee.databinding.FragmentTaskDetailBinding
import com.kust.ermslibrary.models.NotificationData
import com.kust.ermslibrary.models.PushNotification
import com.kust.ermslibrary.models.Task
import com.kust.ermslibrary.services.NotificationService
import com.kust.ermslibrary.utils.ConvertDateAndTimeFormat
import com.kust.ermslibrary.utils.TaskStatus
import com.kust.ermslibrary.utils.UiState
import com.kust.ermslibrary.utils.hide
import com.kust.ermslibrary.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.kust.ermslibrary.R as LibraryR


@AndroidEntryPoint
class TaskDetailFragment : Fragment() {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var task: Task

    private val taskViewModel: TaskViewModel by viewModels()
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

        updateUI()
        observer()
    }

    private fun observer() {
        taskViewModel.updateTask.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.btnAcceptTask.text = ""
                    binding.progressBarAccept.visibility = View.VISIBLE
                }

                is UiState.Success -> {
                    sendNotification()
                    binding.btnAcceptTask.text = getString(LibraryR.string.accept)
                    binding.progressBarAccept.visibility = View.GONE
                    binding.tvTaskStatus.text = task.status
                }

                is UiState.Error -> {
                    binding.btnAcceptTask.text = getString(LibraryR.string.accept)
                    binding.progressBarAccept.visibility = View.GONE
                }
            }
        }
    }

    private fun sendNotification() {
        val title = "Task Update"
        val message = "Task ${task.title} has been updated by ${task.assigneeName}"
        PushNotification(
            NotificationData(
                title,
                message,
            ),
            to = task.managerFCMToken
        )
            .also {
                notificationService.sendNotification(it)
            }
    }

    private fun updateUI() {
        // update the UI with task bundle data
        task = arguments?.getParcelable("task")!!

        val taskCreateDateFormatted = ConvertDateAndTimeFormat().formatDate(task.createdDate)
        val taskCreateTimeFormatted = ConvertDateAndTimeFormat().formatTime(task.createdDate)

        val taskDueDateFormatted = ConvertDateAndTimeFormat().formatDate(task.deadline)
        val taskDueTimeFormatted = ConvertDateAndTimeFormat().formatTime(task.deadline)

        binding.tvTaskName.text = task.title
        binding.tvTaskDescription.text = task.description
        binding.tvAssignedTo.text = task.assigneeName
        binding.tvCreatedDate.text =
            getString(LibraryR.string.date_time, taskCreateDateFormatted, taskCreateTimeFormatted)
        binding.tvDeadline.text =
            getString(LibraryR.string.date_time, taskDueDateFormatted, taskDueTimeFormatted)
        binding.tvTaskStatus.text = task.status
        binding.tvCreatedBy.text = task.managerName

        when (task.status) {
            TaskStatus.PENDING -> {
                binding.btnRejectLayout.show()
                binding.btnAcceptLayout.show()
                binding.btnAcceptTask.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        task.status = TaskStatus.IN_PROGRESS
                        taskViewModel.updateTask(task)
                    }
                }
            }

            TaskStatus.IN_PROGRESS -> {
                binding.btnAcceptLayout.show()
                binding.btnAcceptTask.text = getString(LibraryR.string.submit)
                binding.btnAcceptTask.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        task.status = TaskStatus.SUBMITTED
                        taskViewModel.updateTask(task)
                    }
                }
            }

            TaskStatus.COMPLETED -> {
                binding.btnAcceptLayout.hide()
                binding.btnRejectLayout.hide()
            }

            TaskStatus.REJECTED -> {
                binding.btnAcceptLayout.hide()
                binding.btnRejectLayout.hide()
            }

            TaskStatus.RESUBMITTED -> {
                binding.btnAcceptTask.text = getString(LibraryR.string.submit)
                binding.btnAcceptTask.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        task.status = TaskStatus.SUBMITTED
                        taskViewModel.updateTask(task)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}