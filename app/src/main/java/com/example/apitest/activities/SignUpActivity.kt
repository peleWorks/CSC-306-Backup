package com.example.apitest.activities


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.apitest.data_classes.UserModel
import com.example.apitest.databinding.ActivitySignUpBinding
import com.example.apitest.firebase.FirebaseSetup
import com.example.apitest.firebase.ScoreManagement
import com.example.apitest.utilities.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Locale

class SignUpActivity : AppCompatActivity() {
    private var binding: ActivitySignUpBinding? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        auth = Firebase.auth

        binding?.tvLoginPage?.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }

        binding?.btnSignUp?.setOnClickListener { registerUser() }
    }

    private fun registerUser() {
        val name = binding?.etSinUpName?.text.toString()
        val email = binding?.etSinUpEmail?.text.toString()
        val password = binding?.etSinUpPassword?.text.toString()
        if (validateForm(name, email, password)) {
            val pb = Utils.showProgressBar(this)
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = task.result.user?.uid
                        val currentDate = SimpleDateFormat("d MMMM yyyy",
                            Locale.getDefault()).format(System.currentTimeMillis())
                        val userInfo = UserModel(name = name, id = userId!!,
                            emailId = email, joinDate = currentDate, image = "avatar_1")

                        // Initialize the system document if needed
                        initializeSystemDocumentIfNeeded {
                            // Register user after system document check
                            FirebaseSetup().registerUser(userInfo)
                            Utils.showToast(this, "User Id created successfully")
                            pb.cancel()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    } else {
                        Utils.showToast(this, "User Id not created. Try again later")
                        pb.cancel()
                    }
                }
        }
    }

    private fun initializeSystemDocumentIfNeeded(onComplete: () -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("system")
            .document("scoreResets")
            .get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    // Document doesn't exist, so create it
                    ScoreManagement().initializeSystemDocument()
                }
                onComplete()
            }
            .addOnFailureListener {
                // Even if the check fails, we should still complete the registration
                onComplete()
            }
    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                binding?.tilName?.error = "Enter name"
                false
            }
            TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding?.tilEmail?.error = "Enter valid email address"
                false
            }
            TextUtils.isEmpty(password) -> {
                binding?.tilPassword?.error = "Enter password"
                false
            }
            else -> { true }
        }
    }


}