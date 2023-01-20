package com.example.whatsappapp.registration

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.whatsappapp.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : Fragment() {
    // VARIABLES DECLARED
    private lateinit var enterEmail: TextInputEditText
    private lateinit var enterPassword: TextInputEditText
    private lateinit var confirmPassword: TextInputEditText
    private lateinit var signUpButton: Button
    private lateinit var progressBar: ProgressBar

    // FIREBASE CONNECTED FROM TOOLS/ANDROID
    private lateinit var fauth: FirebaseAuth
    private lateinit var fstore: FirebaseFirestore
    private lateinit var db: DocumentReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // FIND VIEWS BY ID
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        enterEmail = view.findViewById(R.id.etSignUpEmail)
        enterPassword = view.findViewById(R.id.etSignUpPassword)
        progressBar = view.findViewById(R.id.signupProgressBar)
        confirmPassword = view.findViewById(R.id.etConfirmPassword)
        signUpButton = view.findViewById(R.id.signupButton)
        fauth = FirebaseAuth.getInstance()
        fstore = FirebaseFirestore.getInstance()

        // EMAIL & PASSWORD VALIDATIONS
        signUpButton.setOnClickListener {
            val email = enterEmail.text.toString()
            val password = enterPassword.text.toString()
            val confirmpass = confirmPassword.text.toString()
            if (TextUtils.isEmpty(email)) {
                enterEmail.error = "Email is Required To Create Account"
            } else if (TextUtils.isEmpty(password)) {
                enterPassword.error = "Password is Required To Create Account"
            } else if (password.length < 6) {
                enterPassword.error = "Password must be Greater Than 6 Characters in Size"
            } else if (password != confirmpass) {
                confirmPassword.error = "Both Password Not Matched"
            } else {
                progressBar.visibility = View.VISIBLE
                createAccount(email, password)
            }
        }
        return view
    }

    // FUNCTION FOR CREATE ACCOUNT
    private fun createAccount(em: String, pass: String) {
        fauth.createUserWithEmailAndPassword(em, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userinfo = fauth.currentUser?.uid
                db = fstore.collection("users").document(userinfo.toString())
                val obj = mutableMapOf<String, String>()
                obj["userEmail"] = em
                obj["userPassword"] = pass
                obj["userStatus"] = ""
                obj["userName"] = ""
                db.set(obj).addOnSuccessListener {
                    Log.d("onSuccess", "User Created Successfully")
                    progressBar.visibility = View.GONE
                }
            }
        }
    }
}
