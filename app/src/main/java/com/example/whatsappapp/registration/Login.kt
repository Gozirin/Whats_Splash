package com.example.whatsappapp.registration

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.whatsappapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
class Login : Fragment() {

    private lateinit var enterEmail: TextInputEditText
    private lateinit var enterPassword: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var googleButton: Button
    private lateinit var progressBar: RelativeLayout

    // FIREBASE AUTHENTICATION FOR SIGNIN-METHOD
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var resultLaunch: ActivityResultLauncher<Intent>
    private val RC_SIGN_IN = 1011

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // FIND VIEWS BY IDS
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        enterEmail = view.findViewById(R.id.etLoginEmail)
        enterPassword = view.findViewById(R.id.etLoginPassword)
        loginButton = view.findViewById(R.id.loginButton)
        googleButton = view.findViewById(R.id.btGoogleLogin)
        progressBar = view.findViewById(R.id.loginProgressBar)

        // VALIDATIONS FOR EMAIL & PASSWORD
        loginButton.setOnClickListener {
            val email = enterEmail.text.toString()
            val password = enterPassword.text.toString()
            if (TextUtils.isEmpty(email)) {
                enterEmail.error = "Email is Required To Create Account"
            } else if (TextUtils.isEmpty(password)) {
                enterPassword.error = "Password is Required To Create Account"
            } else {
                progressBar.visibility = View.VISIBLE
                signIn(email, password)
            }
        }
        googleButton.setOnClickListener {
            createRequest()
        }
        resultLaunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val launchData = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(launchData)
                try {
                    val account = task.getResult(ApiException::class.java)
                    Log.d("Gmail ID", "firebaseAuthWith Google :$account")
                    firebaseAuthWithGoogle(account.idToken)
                } catch (e: ApiException) {
                    Log.w("Error", "Google Sign In Failed", e)
                }
            }
        }
        return view
    }

    private fun createRequest() {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("Google ID")
            .requestEmail()
            .build()
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener {
                val acct = GoogleSignIn.getLastSignedInAccount(requireContext())
                if (acct != null) {
                    val personName = acct.displayName!!
                    val personEmail = acct.email!!
                    val personPhoto = acct.photoUrl!!
                    val objLogin = mutableMapOf<String, String>()
                    objLogin["userEmail"] = personEmail
                    objLogin["userProfile"] = personPhoto.toString()
                    objLogin["userName"] = personName

                    FirebaseFirestore.getInstance().collection("users").document()
                        .set(objLogin).addOnSuccessListener {
                            Log.d("onSuccess", "Successfully Google Login")
                        }
                }
            }
    }

    private fun signIn(em: String, pass: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(em, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "LoginSuccessfully", Toast.LENGTH_LONG).show()
            }
        }
    }
}
