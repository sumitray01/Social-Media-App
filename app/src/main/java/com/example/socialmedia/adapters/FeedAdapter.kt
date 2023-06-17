package com.example.socialmedia.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.models.Post
import com.example.socialmedia.ui.CommentsActivity
import com.example.socialmedia.ui.CommentsActivity.Companion.POST_ID
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.github.thunder413.datetimeutils.DateTimeStyle
import com.github.thunder413.datetimeutils.DateTimeUtils
import com.google.firebase.firestore.FirebaseFirestore

class FeedAdapter(options: FirestoreRecyclerOptions<Post>, val context: Context) :
    FirestoreRecyclerAdapter<Post, FeedAdapter.FeedViewHolder>(options) {

    class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.post_image)
        val postText: TextView = itemView.findViewById(R.id.post_text)
        val authorText: TextView = itemView.findViewById(R.id.post_author)
        val timeText: TextView = itemView.findViewById(R.id.post_time)
        val likeIcon: ImageView = itemView.findViewById(R.id.like_icon)
        val likeCount: TextView = itemView.findViewById(R.id.like_count)
        val commentIcon: ImageView = itemView.findViewById(R.id.comment_icon)
        val commentCount: TextView = itemView.findViewById(R.id.comment_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return FeedViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int, post: Post) {
        holder.postText.text = post.text
        holder.authorText.text = post.user.name

        val date = DateTimeUtils.formatDate(post.time)
        val dateFormatted = DateTimeUtils.formatWithStyle(date, DateTimeStyle.LONG)

        holder.timeText.text = dateFormatted

        Glide.with(context)
            .load(post.imageUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.postImage)

        holder.likeCount.text = post.likersList.size.toString()

        val firestore = FirebaseFirestore.getInstance()
        val userId = post.user.id

        fun setFilledLikeIcon() {
            holder.likeIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.like_icon_filled
                )
            )
        }

        fun setOutlinedLikeIcon() {
            holder.likeIcon.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.like_icon_outlined
                )
            )
        }


        if (post.likersList.contains(userId)) {
            // User has liked this post
            setFilledLikeIcon()
        } else {
            // User has not liked the post
            setOutlinedLikeIcon()
        }

        fun invertLike() {
            if (post.likersList.contains(userId)) {
                post.likersList.remove(userId)
                setOutlinedLikeIcon()
            } else {
                post.likersList.add(userId.toString())
                setFilledLikeIcon()
            }
        }

        val postId = snapshots.getSnapshot(position).id
        val postDocument = firestore.collection("Posts").document(postId)

        holder.likeIcon.setOnClickListener {
            invertLike()

            postDocument
                .set(post).addOnCompleteListener {
                    if (!it.isSuccessful) {
                        Toast.makeText(
                            context,
                            "Something went wrong. Please try again.",
                            Toast.LENGTH_LONG
                        ).show()
                        invertLike()
                    }
                }
        }

        holder.commentIcon.setOnClickListener {
            val intent = Intent(context, CommentsActivity::class.java)
            intent.putExtra(POST_ID, postId)
            context.startActivity(intent)
        }

        postDocument.collection("Comments").get().addOnCompleteListener {
            if (it.isSuccessful) {
                holder.commentCount.text = it.result?.size().toString()
            }
        }
    }
}