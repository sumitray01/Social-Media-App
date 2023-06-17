package com.example.socialmedia.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.R
import com.example.socialmedia.models.Comment
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.github.thunder413.datetimeutils.DateTimeStyle
import com.github.thunder413.datetimeutils.DateTimeUtils

class CommentsAdapter(
    options: FirestoreRecyclerOptions<Comment>,
    val showZeroCommentsMessageOrRecyclerView: () -> Unit
) :
    FirestoreRecyclerAdapter<Comment, CommentsAdapter.CommentsViewHolder>(options) {

    class CommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val commentText: TextView = itemView.findViewById(R.id.comment_text)
        val commentAuthor: TextView = itemView.findViewById(R.id.comment_author)
        val commentTime: TextView = itemView.findViewById(R.id.comment_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return CommentsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int, comment: Comment) {
        holder.commentText.text = comment.commentText
        holder.commentAuthor.text = comment.commentAuthor.name

        val date = DateTimeUtils.formatDate(comment.commentTime)
        val formattedDate = DateTimeUtils.formatWithStyle(date, DateTimeStyle.LONG)

        holder.commentTime.text = formattedDate
    }

    override fun onDataChanged() {
        super.onDataChanged()
        showZeroCommentsMessageOrRecyclerView()
    }
}