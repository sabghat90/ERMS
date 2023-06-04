package com.kust.erms_company.data.repositroy

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kust.ermslibrary.models.Message
import com.kust.ermslibrary.utils.FirebaseRealtimeDatabaseConstants
import com.kust.ermslibrary.utils.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatRepositoryImpl(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : ChatRepository {

    override suspend fun sendMessage(
        message: Message,
        receiverId: String,
        result: (UiState<String>) -> Unit
    ) {
        try {
            val senderId = auth.currentUser?.uid ?: return
            val senderRoom = receiverId + senderId

            val senderReference = database.reference.child(FirebaseRealtimeDatabaseConstants.CHAT).child(senderRoom).child(FirebaseRealtimeDatabaseConstants.CHAT_MESSAGES).push()
            val messageId = senderReference.key ?: return
            message.id = messageId
            senderReference.setValue(message)
                .addOnSuccessListener {
                    val receiverRoom = senderId + receiverId
                    val receiverReference = database.reference.child(FirebaseRealtimeDatabaseConstants.CHAT).child(receiverRoom).child(FirebaseRealtimeDatabaseConstants.CHAT_MESSAGES).push()
                    receiverReference.setValue(message)
                        .addOnSuccessListener {
                            result(UiState.Success(messageId))
                        }
                        .addOnFailureListener {
                            result(UiState.Error(it.localizedMessage ?: "Unknown error"))
                        }
                }
        } catch (e: Exception) {
            result(UiState.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    override suspend fun getMessages(receiverId: String, result: (List<Message>) -> Unit): Unit = withContext(Dispatchers.IO) {
        try {
            val senderId = auth.currentUser?.uid ?: return@withContext
            val senderRoom = receiverId + senderId
            val receiverRoom = senderId + receiverId

            val senderReference = database.reference.child(FirebaseRealtimeDatabaseConstants.CHAT).child(senderRoom).child(FirebaseRealtimeDatabaseConstants.CHAT_MESSAGES)
            val receiverReference = database.reference.child(FirebaseRealtimeDatabaseConstants.CHAT).child(receiverRoom).child(FirebaseRealtimeDatabaseConstants.CHAT_MESSAGES)

            senderReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages = mutableListOf<Message>()
                    for (data in snapshot.children) {
                        val message = data.getValue(Message::class.java) ?: continue
                        messages.add(message)
                    }
                    result(messages)
                }

                override fun onCancelled(error: DatabaseError) {
                    result(emptyList())
                }
            })

            receiverReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages = mutableListOf<Message>()
                    for (data in snapshot.children) {
                        val message = data.getValue(Message::class.java) ?: continue
                        messages.add(message)
                    }
                    result(messages)
                }

                override fun onCancelled(error: DatabaseError) {
                    result(emptyList())
                }
            })

        } catch (e: Exception) {
            result(emptyList())
        }
    }
}