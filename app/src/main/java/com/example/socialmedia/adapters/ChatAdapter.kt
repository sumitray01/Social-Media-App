package com.example.socialmedia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.R
import com.example.socialmedia.models.Chat
import com.example.socialmedia.utils.UserUtils
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ChatAdapter(options: FirestoreRecyclerOptions<Chat>) :
    FirestoreRecyclerAdapter<Chat, ChatAdapter.ChatViewHolder>(options) {

    companion object {
        const val MSG_BY_SELF = 0
        const val MSG_BY_SOMEONE_ELSE = 1
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatText: TextView = itemView.findViewById(R.id.chat_text)
        val authorName: TextView = itemView.findViewById(R.id.chat_author)
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).author.id == UserUtils.user?.id) {
            MSG_BY_SELF
        } else {
            MSG_BY_SOMEONE_ELSE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        var itemView: View? = null

        itemView = if (viewType == MSG_BY_SELF) {
            LayoutInflater.from(parent.context).inflate(R.layout.self_chat_item, parent, false)
        } else {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.someone_else_chat_item, parent, false)
        }

        return ChatViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, chat: Chat) {
        holder.chatText.text = chat.text
        holder.authorName.text = chat.author.name
    }
}