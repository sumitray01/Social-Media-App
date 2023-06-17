package com.example.socialmedia.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.socialmedia.R
import com.example.socialmedia.utils.UserUtils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if user is logged in and switch to auth screen if not
        if (FirebaseAuth.getInstance().currentUser == null) {
            val intent = Intent(this, AuthenticationActivity::class.java)
            startActivity(intent)
            finish()
        }

        UserUtils.getCurrentUser()

        setFragment(FeedFragment())

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav_view)

        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.feed_item -> {
                    setFragment(FeedFragment())
                }
                R.id.search_item -> {
                    setFragment(SearchFragment())
                }
                R.id.chatrooms_item -> {
                    setFragment(ChatroomsFragment())
                }
                R.id.profile_item -> {
                    setFragment(ProfileFragment())
                }
                else -> {
                    setFragment(FeedFragment())
                }
            }
            true
        }
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun logOut() {
        val intent = Intent(this, AuthenticationActivity::class.java)
        startActivity(intent)
        finish()
    }
}