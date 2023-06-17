package com.example.socialmedia.utils

import com.example.socialmedia.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object UserUtils {
    var user: User? = null

    fun getCurrentUser(onSuccessful: (User) -> Unit = {}) {
        val firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser != null) {
            FirebaseFirestore.getInstance().collection("Users")
                .document(firebaseAuth.currentUser?.uid.toString())
                .get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        user = it.result?.toObject(User::class.java)
                        user?.let { user ->
                            onSuccessful(user)
                        }
                    }
                }
        }
    }
}