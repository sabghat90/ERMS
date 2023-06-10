package com.kust.ermsmanager.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.kust.ermslibrary.models.Employee
import com.kust.ermsmanager.databinding.FragmentManagerProfileBinding
import com.kust.ermsmanager.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ManagerProfileFragment : Fragment() {

    private var _binding : FragmentManagerProfileBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var employee : Employee

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentManagerProfileBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observer()
        updateUI()
    }

    private fun observer() {
        authViewModel.getSession {
            employee.apply {
                id = it?.id.toString()
                name = it?.name.toString()
                email = it?.email.toString()
                phone = it?.phone.toString()
                address = it?.address.toString()
                role = it?.role.toString()
            }
        }
    }

    private fun updateUI() {
        binding.profile.apply {
            name.text = employee.name
            tvEmail.text = employee.email
            tvPhone.text = employee.phone
            tvFullAddress.text = employee.address
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}