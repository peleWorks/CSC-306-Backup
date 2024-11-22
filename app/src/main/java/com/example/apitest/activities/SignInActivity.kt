package com.example.apitest.activities


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.apitest.R
import com.example.apitest.data_classes.UserModel
import com.example.apitest.databinding.ActivitySignInBinding
import com.example.apitest.firebase.FirebaseSetup
import com.example.apitest.utilities.Utils
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignInActivity : AppCompatActivity() {
    private var binding: ActivitySignInBinding? = null
    private lateinit var auth:FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()


        binding?.tvRegister?.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }


        binding?.btnSignIn?.setOnClickListener {
            signInUser()
        }



    }

    private fun signInUser()
    {
        val email = binding?.etSinInEmail?.text.toString()
        val password = binding?.etSinInPassword?.text.toString()
        if (validateForm(email, password))
        {
            val pb = Utils.showProgressBar(this)
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task->
                    if (task.isSuccessful)
                    {
                        pb.cancel()
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }
                    else
                    {
                        Utils.showToast(this,"Can't login currently. Try after sometime")
                        pb.cancel()
                    }
                }
        }
    }





    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful)
        {
            val account:GoogleSignInAccount? = task.result
            if (account!=null){
                updateUI(account)
            }
        }
        else
        {
            Utils.showToast(this,"SignIn Failed, Try again later")
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val pb = Utils.showProgressBar(this)
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful)
            {
                val name = account.displayName!!
                val id = auth.uid!!
                val email = account.email!!
                FirebaseSetup().doesDocumentExist(FirebaseSetup().getCurrentUserId())
                    .addOnSuccessListener { exist->
                        if (!exist){
                            val userInfo = UserModel(name = name, id = id, emailId = email )
                            FirebaseSetup().registerUser(userInfo)
                        }
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                        pb.cancel()
                    }
            }
            else
            {
                Utils.showToast(this,"Can't login currently. Try after sometime")
                pb.cancel()
            }
        }

    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()->{
                binding?.tilEmail?.error = "Enter valid email address"
                false
            }
            TextUtils.isEmpty(password)->{
                binding?.tilPassword?.error = "Enter password"
                false
            }
            else -> { true }
        }
    }
}