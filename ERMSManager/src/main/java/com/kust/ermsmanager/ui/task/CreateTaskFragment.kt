package com.kust.ermsmanager.ui.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.kust.ermsmanager.R
import com.kust.ermsmanager.data.models.TaskModel
import com.kust.ermsmanager.databinding.FragmentCreateTaskBinding
import com.kust.ermsmanager.ui.employee.EmployeeViewModel
import com.kust.ermsmanager.utils.UiState
import com.kust.ermsmanager.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CreateTaskFragment : Fragment() {

    private var _binding: FragmentCreateTaskBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by viewModels()

    private val employeeViewModel: EmployeeViewModel by viewModels()

    // employee list for spinner
    private val employeeList = mutableListOf<String>()

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

        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, employeeList)
        binding.ddmEmployeeList.setAdapter(arrayAdapter)


        binding.taskDueDateButton.setOnClickListener { getDueDateAndTime() }

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
            status = "Pending",
            deadline = binding.taskDueDateButton.text.toString(),
            createdDate = getCurrentDateAndTime(),
            createdBy = FirebaseAuth.getInstance().currentUser?.email.toString(),
            assignedTo = "Someone"
        )
    }

    private fun observer() {
        taskViewModel.createTask.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    binding.progressBar.show()
                    binding.btnCreateTask.text = ""
                }
                is UiState.Success -> {
                    binding.btnCreateTask.text = getString(R.string.create_task)
                    binding.progressBar.hide()
                    it.data.let {
                        toast("Task created")
                        findNavController().navigate(R.id.action_createTaskFragment_to_taskListingFragment)
                    }
                }
                is UiState.Error -> {
                    binding.btnCreateTask.text = getString(R.string.create_task)
                    binding.progressBar.hide()
                    toast(it.error)
                }
            }
        }

        employeeViewModel.getEmployeeList.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    it.data.let { employees ->
                        employees.forEach { employee ->
                            employeeList.add(employee.name)
                        }
                    }
                }
                is UiState.Error -> {
                    toast(it.error)
                }
            }
        }
    }

    private fun getCurrentDateAndTime(): String {
        val calendar = Calendar.getInstance()
        val currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.time)
        val currentTime = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.time)
        return "$currentDate $currentTime"
    }

    // get due date and time from date picker using due date button
    private fun getDueDateAndTime(): String {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val timePicker = TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        binding.taskDueDateButton.text = DateFormat.getDateTimeInstance().format(calendar.time)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )
                timePicker.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
        return DateFormat.getDateTimeInstance().format(calendar.time)
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

        if (taskDeadline.isEmpty()) {
            binding.taskDueDateButton.error = "Task deadline cannot be empty"
            binding.taskDueDateButton.requestFocus()
            isValid = false
        }

        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}