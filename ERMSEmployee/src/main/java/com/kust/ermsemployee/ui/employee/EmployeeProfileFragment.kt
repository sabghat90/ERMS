package com.kust.ermsemployee.ui.employee

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kust.ermsemployee.databinding.FragmentEmployeeProfileBinding
import com.kust.ermslibrary.models.Employee
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EmployeeProfileFragment : Fragment() {
    private var _binding: FragmentEmployeeProfileBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var employee: Employee
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEmployeeProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        employee = arguments?.getParcelable("employee")!!

        updateUI()
    }

    private fun updateUI() {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}