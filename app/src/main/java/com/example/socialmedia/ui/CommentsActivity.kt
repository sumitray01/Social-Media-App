package com.example.socialmedia.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.R
import com.example.socialmedia.adapters.CommentsAdapter
import com.example.socialmedia.models.Comment
import com.example.socialmedia.utils.UserUtils
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore

class CommentsActivity : AppCompatActivity() {

    private lateinit var commentsRV: RecyclerView
    private var postId: String? = null

    private var commentsAdapter: CommentsAdapter? = null

    companion object {
        const val POST_ID = "PostID"
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        if (intent.hasExtra(POST_ID)) {
            postId = intent.getStringExtra(POST_ID)
        }

        commentsRV = findViewById(R.id.comments_rv)

        setUpRecyclerView()

        val commentET: EditText = findViewById(R.id.enter_comment)
        val sendIcon: ImageView = findViewById(R.id.send_comment)

        sendIcon.setOnClickListener {
            val commentText = commentET.text.toString()

            if (TextUtils.isEmpty(commentText)) {
                Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val firestore = FirebaseFirestore.getInstance()

            val comment = UserUtils.user?.let { currentUser ->
                Comment(commentText, currentUser, System.currentTimeMillis())
            }

            postId?.let { postId ->
                comment?.let {
                    firestore.collection("Posts").document(postId)
                        .collection("Comments").document().set(comment)
                }
            }

            commentET.text.clear()
        }
    }

    private fun setUpRecyclerView() {
        val firestore = FirebaseFirestore.getInstance()

        val query = postId?.let { postId ->
            firestore.collection("Posts").document(postId)
                .collection("Comments").orderBy("commentTime")
        }

        val firestoreRecyclerOptions = query?.let {
            FirestoreRecyclerOptions.Builder<Comment>().setQuery(it, Comment::class.java).build()
        }

        commentsAdapter = firestoreRecyclerOptions?.let { CommentsAdapter(it, ::showZeroCommentsMessageOrRecyclerView) }

        commentsRV.adapter = commentsAdapter
        commentsRV.layoutManager = LinearLayoutManager(this)
    }

    private fun showZeroCommentsMessageOrRecyclerView() {
        if (commentsAdapter?.itemCount == 0) {
            findViewById<TextView>(R.id.zero_comments).visibility = View.VISIBLE
            commentsRV.visibility = View.GONE
        } else {
            findViewById<TextView>(R.id.zero_comments).visibility = View.GONE
            commentsRV.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        commentsAdapter?.startListening()
    }

    override fun onPause() {
        super.onPause()
        commentsAdapter?.stopListening()
    }
}























