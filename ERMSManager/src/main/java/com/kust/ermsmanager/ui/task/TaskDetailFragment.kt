package com.kust.ermsmanager.ui.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kust.ermsmanager.R
import com.kust.ermsmanager.data.models.TaskModel
import com.kust.ermsmanager.databinding.FragmentTaskDetailBinding
import com.kust.ermsmanager.utils.UiState
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
        // observe the delete task result
        taskViewModel.deleteTask.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.btnDeleteTask.isEnabled = false
                    binding.btnDeleteTask.text = "Delete Task"
                }
                is UiState.Success -> {
                    binding.btnDeleteTask.isEnabled = true
                    binding.btnDeleteTask.text = "Delete Task"
                    findNavController().navigate(R.id.action_taskDetailFragment_to_taskListingFragment)
                }
                is UiState.Error -> {
                    binding.btnDeleteTask.isEnabled = true
                    binding.btnDeleteTask.text = "Delete Task"
                }
            }
        }
    }

    private fun updateUI() {
        // update the UI with task bundle data
        task = arguments?.getParcelable("task")!!
        binding.tvTaskName.text = task.name
        binding.tvTaskDescription.text = task.description
        binding.tvAssignedTo.text = task.assigneeName
        binding.tvCreatedDate.text = task.createdDate
        binding.tvDeadline.text = task.deadline
        binding.tvTaskStatus.text = task.status

        // delete task and call the function from TaskViewModel with couroutine to delete the task
        binding.btnDeleteTask.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                taskViewModel.deleteTask(task.id)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}