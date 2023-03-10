package com.kust.erms_company.ui.employee

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kust.erms_company.R
import com.kust.erms_company.data.model.EmployeeModel
import com.kust.erms_company.databinding.FragmentEmployeeListingBinding
import com.kust.erms_company.utils.UiState
import com.kust.erms_company.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class EmployeeListingFragment : Fragment() {

    private val TAG = "EmployeeListingFragment"
    private var _binding : FragmentEmployeeListingBinding? = null
    private val binding get() = _binding!!

    private val viewModel : EmployeeViewModel by viewModels()

    private val adapter by lazy { EmployeeListingAdapter(
        onItemClicked = {pos, employee ->
            findNavController().navigate(R.id.action_manageEmployeeFragment_to_profileFragment, Bundle().apply {
                putParcelable("employee", employee)
            })
        }
    ) }

    private val progressDialog : ProgressDialog by lazy {
        ProgressDialog(requireContext())
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEmployeeListingBinding.inflate(inflater, container, false)

        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()

        binding.rvEmployee.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEmployee.adapter = adapter

//        adapter.setOnItemClickListener(object : EmployeeListingAdapter.OnItemClickListener {
//            override fun onItemClick(position: Int) {
//                toast(adapter.employees[position].toString())
//            }
//        })

    }

    private fun observer() {
        viewModel.getEmployeeList.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Loading -> {
                    progressDialog.show()
                }
                is UiState.Success -> {
                    adapter.employees = it.data as MutableList<EmployeeModel>
                    adapter.updateList(it.data.toMutableList())
                    progressDialog.dismiss()
                }
                is UiState.Error -> {
                    progressDialog.dismiss()
                    toast(it.error)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}