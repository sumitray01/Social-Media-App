package com.example.socialmedia.models

data class Comment(
    val commentText: String = "",
    val commentAuthor: User = User(),
    val commentTime: Long = 0L
)