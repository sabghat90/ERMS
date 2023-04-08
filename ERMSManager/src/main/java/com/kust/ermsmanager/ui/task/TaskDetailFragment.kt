package com.kust.ermsmanager.ui.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kust.ermsmanager.data.models.TaskModel
import com.kust.ermsmanager.databinding.FragmentTaskDetailBinding


class TaskDetailFragment : Fragment() {

    private var _binding: FragmentTaskDetailBinding? = null
    private val binding get() = _binding!!

    // task object
    private lateinit var task: TaskModel

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
    }

    private fun updateUI() {
        // update the UI with task bundle data
        task = arguments?.getParcelable<TaskModel>("task")!!
        binding.tvTaskName.text = task.name
        binding.tvTaskDescription.text = task.description
        binding.tvAssignedTo.text = task.assignedTo
        binding.tvCreatedDate.text = task.createdDate
        binding.tvDeadline.text = task.deadline
        binding.tvTaskStatus.text = task.status
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}