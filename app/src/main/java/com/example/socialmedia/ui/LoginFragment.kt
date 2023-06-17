package com.example.socialmedia.ui

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.socialmedia.R
import com.example.socialmedia.interfaces.AuthenticationInterface
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var callback: AuthenticationInterface? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as AuthenticationInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goToRegister: TextView = view.findViewById<TextView>(R.id.go_to_register)

        goToRegister.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.auth_fragment_container, RegisterFragment())
                ?.addToBackStack(null)
                ?.commit()
        }

        val emailText: TextInputLayout = view.findViewById(R.id.email_text)
        val passwordText: TextInputLayout = view.findViewById(R.id.password_text)
        val loginButton: Button = view.findViewById(R.id.login_button)
        val loginProgress: ProgressBar = view.findViewById(R.id.login_progress)

        fun validateAndLoginUser(email: String, password: String) {
            if (TextUtils.isEmpty(email)) {
                emailText.error = "Email is a required field"
                return
            }

            if (TextUtils.isEmpty(password)) {
                passwordText.error = "Password is a required field"
                return
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailText.error = "Please enter a valid email address"
                return
            }

            loginProgress.visibility = View.VISIBLE

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    loginProgress.visibility = View.GONE

                    if (task.isSuccessful) {
                        callback?.onSuccessfulAuth()
                    } else {
                        Toast.makeText(activity, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show()
                        Log.e("LoginFragment", task.exception.toString())
                    }
                }
        }

        loginButton.setOnClickListener {
            val email = emailText.editText?.text.toString()
            val password = passwordText.editText?.text.toString()

            emailText.error = null
            passwordText.error = null

            validateAndLoginUser(email, password)
        }
    }
}