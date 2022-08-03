package com.amplifyframework

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.core.Amplify
import com.amplifyframework.samples.core.ActivityNavigationUtil
import com.amplifyframework.samples.core.Constants.EXTRA_EMAIL
import com.amplifyframework.samples.gettingstarted.databinding.ActivitySignupConfirmBinding

class SignUpConfirmationActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupConfirmBinding
    private val handler = Handler(Looper.getMainLooper())
    private var email: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.handler = this

        email = intent.extras?.getString(EXTRA_EMAIL)

        if (email.isNullOrBlank()) {
            showToastMessage("Something went wrong! Please retry.")
            finish()
        }
    }

    fun onConfirmSignUp() {
        Log.d(TAG, "onConfirmSignUp::")
        if (binding.signupConfirmationCodeInput.text.isNullOrBlank() || email.isNullOrBlank()
        ) {
            showToastMessage("Empty field(s)!")
            return
        }
        tryConfirmSignUp(
            code = binding.signupConfirmationCodeInput.text.toString()
        )

    }

    private fun onLaunchSignIn() {
        Log.d(TAG, "onLaunchSignIn::")
        ActivityNavigationUtil.navigateToActivity(
            this,
            SignInActivity::class.java,
            ActivityNavigationUtil.ActivityFinishMode.FINISH_ALL
        )

    }


    private fun tryConfirmSignUp(code: String) {
        email?.let {
            Amplify.Auth.confirmSignUp(
                it,
                code,
                {
                    onLaunchSignIn()
                },
                { error -> showToastMessage(error.message) }
            )
        }
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
        private const val TAG = "SignUpConfirmation"
    }
}