package com.amplifyframework

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.core.Amplify
import com.amplifyframework.samples.gettingstarted.databinding.ActivitySigninBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.handler = this
    }

    fun onSignIn() {
        Log.d(TAG, "onSignIn::")
        if (binding.signinEmailInput.text.isNullOrBlank() || binding.signinPasswordInput.text.isNullOrBlank()) {
            showToastMessage("Empty field(s)!")
            return
        }
        trySignIn(
            email = binding.signinEmailInput.text.toString(),
            pwd = binding.signinPasswordInput.text.toString()
        )

    }

    fun onLaunchSignUp() {
        Log.d(TAG, "onLaunchSignUp::")

    }

    fun onLaunchForgotPassword() {
        Log.d(TAG, "onLaunchForgotPassword::")

    }


    private fun trySignIn(email: String, pwd: String) {
        Amplify.Auth.signIn(
            email,
            pwd,
            { result ->
                if (result.isSignInComplete) {
                    launchMainAppScreen()
                } else {
                    showToastMessage("Sign in not complete")
                }
            },
            { error -> showToastMessage(error.message) }
        )
    }

    private fun launchMainAppScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun showToastMessage(msg: String?) {
        if (!msg.isNullOrBlank()) {
            handler.post {
                Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    companion object {
        private const val TAG = "SignInActivity"
    }
}