package com.kust.erms_company.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.kust.erms_company.databinding.FragmentChatBinding
import com.kust.ermslibrary.models.Employee
import com.kust.ermslibrary.models.Message
import com.kust.ermslibrary.models.NotificationData
import com.kust.ermslibrary.models.PushNotification
import com.kust.ermslibrary.services.NotificationService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val chatViewModel: ChatViewModel by viewModels()
    private val messageAdapter: MessageAdapter by lazy {
        MessageAdapter()
    }

    @Inject
    lateinit var employee: Employee

    @Inject
    lateinit var notificationService: NotificationService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        employee = requireArguments().getParcelable("employee")!!

        // set action bar title to employee name
        requireActivity().title = employee.name

        observer()
        chatViewModel.getChatList(employee.id)

        binding.rvChat.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChat.adapter = messageAdapter

        binding.btnSend.setOnClickListener {
            if (binding.etMessage.text.toString().isNotEmpty()) {
                chatViewModel.sendMessage(getMessage(), employee.id)
                binding.etMessage.text = null
                sendNotification()
            }
        }
    }

    private fun sendNotification() {
        val notificationData = "${employee.name} you have a new message"
        PushNotification(
            NotificationData(
                title = "New Message",
                body = notificationData
            ),
            to = employee.fcmToken
        ).also {
            notificationService.sendNotification(it)
        }
    }

    private fun observer() {
        chatViewModel.getChatList.observe(viewLifecycleOwner) {
            messageAdapter.submitList(it)
        }
    }

    private fun getMessage(): Message {
        val message = binding.etMessage.text.toString()
        return Message(
            body = message,
            senderId = FirebaseAuth.getInstance().currentUser?.uid!!,
            time = Timestamp.now().toDate().toString()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}