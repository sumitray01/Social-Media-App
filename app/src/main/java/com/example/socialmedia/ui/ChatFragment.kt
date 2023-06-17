package com.example.socialmedia.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.R
import com.example.socialmedia.adapters.ChatAdapter
import com.example.socialmedia.models.Chat
import com.example.socialmedia.utils.UserUtils
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ChatFragment : Fragment() {
    private var chatroomId: String? = null

    private lateinit var chatRV: RecyclerView
    private lateinit var chatAdapter: ChatAdapter

    companion object {
        const val CHATROOM_ID = "ChatroomID"

        fun newInstance(chatroomId: String): ChatFragment {
            return ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(CHATROOM_ID, chatroomId)
                }
            }
        }

        fun show(
            fragmentManager: FragmentManager,
            chatroomId: String
        ) {
            val instance = newInstance(chatroomId)
            fragmentManager.beginTransaction().apply {
                add(instance, "ChatFragment")
                commit()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            chatroomId = it.getString(CHATROOM_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatRV = view.findViewById(R.id.chat_rv)

        setUpRecyclerView()

        val enterMessage: EditText = view.findViewById(R.id.enter_message)
        val sendMessage: ImageView = view.findViewById(R.id.send_message)

        sendMessage.setOnClickListener {
            if (enterMessage.text.isBlank()) {
                return@setOnClickListener
            }

            val chatText = enterMessage.text.toString()

            val messagesCollection = FirebaseFirestore.getInstance().collection("Chatrooms")
                .document(chatroomId.toString()).collection("Messages")

            val chat = Chat(chatText, UserUtils.user!!, System.currentTimeMillis(), chatroomId.toString())

            messagesCollection.document().set(chat).addOnCompleteListener {
                if (it.isSuccessful) {
                    chatRV.adapter?.itemCount?.let { count ->
                        chatRV.scrollToPosition(count - 1)
                    }
                    enterMessage.text.clear()
                } else {
                    Toast.makeText(context, it.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setUpRecyclerView() {
        val firestore = FirebaseFirestore.getInstance()

        val query = firestore.collection("Chatrooms")
            .document(chatroomId.toString()).collection("Messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)

        val firestoreRecyclerOptions = FirestoreRecyclerOptions.Builder<Chat>()
            .setQuery(query, Chat::class.java).build()

        chatAdapter = ChatAdapter(firestoreRecyclerOptions)

        chatRV.adapter = chatAdapter
        chatRV.layoutManager = LinearLayoutManager(context)
    }

    override fun onResume() {
        super.onResume()

        // If you want to check if chatAdapter is initialized or not
        if (::chatAdapter.isInitialized) {
        }

        chatAdapter.startListening()
    }

    override fun onPause() {
        super.onPause()
        chatAdapter.stopListening()
    }
}