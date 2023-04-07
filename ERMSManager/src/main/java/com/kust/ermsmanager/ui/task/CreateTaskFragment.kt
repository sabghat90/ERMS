package com.kust.ermsmanager.ui.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.kust.ermsmanager.R
import com.kust.ermsmanager.data.models.TaskModel
import com.kust.ermsmanager.databinding.FragmentCreateTaskBinding
import com.kust.ermsmanager.utils.UiState
import com.kust.ermsmanager.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateTaskFragment : Fragment() {

    private var _binding: FragmentCreateTaskBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by viewModels()

//    @Inject
//    var auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCreateTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()

        binding.btnCreateTask.setOnClickListener {
            if (validateInput()) {
                val task = taskObj()
                CoroutineScope(Dispatchers.IO).launch {
                    taskViewModel.createTask(task)
                }
            }
        }
    }

    private fun taskObj(): TaskModel {
        return TaskModel(
            name = binding.taskNameInput.text.toString(),
            description = binding.taskDescriptionInput.text.toString(),
            deadline = binding.taskDueDateButton.text.toString()
        )
    }

    private fun observer() {
        taskViewModel.createTask.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                }
                is UiState.Success -> {
                    binding.progressBar.hide()
                    it.data?.let {
                        toast("Task created")
                        findNavController().navigate(R.id.action_createTaskFragment_to_taskListingFragment)
                    }
                }
                is UiState.Error -> {
                    binding.progressBar.hide()
                    toast(it.error)
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true
        val taskName = binding.taskNameInput.text.toString()
        val taskDescription = binding.taskDescriptionInput.text.toString()
        val taskDeadline = binding.taskDueDateButton.text.toString()

        if (taskName.isEmpty()) {
            binding.taskNameInput.error = "Task name cannot be empty"
            binding.taskNameInput.requestFocus()
            isValid = false
        }

        if (taskDescription.isEmpty()) {
            binding.taskDescriptionInput.error = "Task description cannot be empty"
            binding.taskDescriptionInput.requestFocus()
            isValid = false
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}