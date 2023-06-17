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
import com.example.socialmedia.models.User
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFragment : Fragment() {

    private var callback: AuthenticationInterface? = null

    private val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as AuthenticationInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val goToLogin: TextView = view.findViewById<TextView>(R.id.go_to_login)

        goToLogin.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.auth_fragment_container, LoginFragment())
                ?.addToBackStack(null)
                ?.commit()
        }

        val emailText: TextInputLayout = view.findViewById(R.id.email_text)
        val nameText: TextInputLayout = view.findViewById(R.id.name_text)
        val passwordText: TextInputLayout = view.findViewById(R.id.password_text)
        val confirmPasswordText: TextInputLayout = view.findViewById(R.id.confirm_password_text)
        val registerButton: Button = view.findViewById(R.id.register_button)
        val registerProgress: ProgressBar = view.findViewById(R.id.register_progress)

        fun validateAndRegisterUser(
            email: String,
            name: String,
            password: String,
            confirmPassword: String
        ) {
            if (TextUtils.isEmpty(email)) {
                emailText.error = "Email is a required field"
                return
            }

            if (TextUtils.isEmpty(name)) {
                nameText.error = "Name is a required field"
                return
            }

            if (TextUtils.isEmpty(password)) {
                passwordText.error = "Password is a required field"
                return
            }

            if (TextUtils.isEmpty(confirmPassword)) {
                confirmPasswordText.error = "Confirm password is a required field"
                return
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailText.error = "Please enter a valid email address"
                return
            }

            if (password != confirmPassword) {
                confirmPasswordText.error = "Passwords do not match"
                return
            }

            if (!password.matches(passwordRegex)) {
                passwordText.error = "Password should contain minimum eight characters, at least one uppercase letter, one lowercase letter and one number"
                return
            }

            registerProgress.visibility = View.VISIBLE

            val auth = FirebaseAuth.getInstance()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task1 ->
                    registerProgress.visibility = View.GONE
                    if (task1.isSuccessful) {
                        val user = User(auth.currentUser?.uid, name, email)
                        val firestore = FirebaseFirestore.getInstance().collection("Users")

                        firestore.document(auth.currentUser?.uid.toString()).set(user)
                            .addOnCompleteListener {  task2 ->
                                registerProgress.visibility = View.GONE
                                if (task2.isSuccessful) {
                                    callback?.onSuccessfulAuth()
                                } else {
                                    Toast.makeText(activity, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show()
                                    Log.e("RegisterFragment", task1.exception.toString())
                                }
                            }
                    } else {
                        registerProgress.visibility = View.GONE
                        Toast.makeText(activity, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show()
                        Log.e("RegisterFragment", task1.exception.toString())
                    }
                }
        }

        registerButton.setOnClickListener {
            val email = emailText.editText?.text.toString()
            val name = nameText.editText?.text.toString()
            val password = passwordText.editText?.text.toString()
            val confirmPassword = confirmPasswordText.editText?.text.toString()

            emailText.error = null
            nameText.error = null
            passwordText.error = null
            confirmPasswordText.error = null

            validateAndRegisterUser(email, name, password, confirmPassword)
        }
    }
}