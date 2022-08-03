package com.amplifyframework

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.auth.result.step.AuthSignUpStep
import com.amplifyframework.core.Amplify
import com.amplifyframework.samples.core.ActivityNavigationUtil
import com.amplifyframework.samples.core.Constants.EXTRA_EMAIL
import com.amplifyframework.samples.gettingstarted.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.handler = this
    }

    fun onSignUp() {
        Log.d(TAG, "onSignUp::")
        if (binding.signupEmailInput.text.isNullOrBlank() || binding.signupPasswordInput.text.isNullOrBlank()
            || binding.signupUsernameInput.text.isNullOrBlank()
        ) {
            showToastMessage("Empty field(s)!")
            return
        }
        trySignUp(
            email = binding.signupEmailInput.text.toString(),
            pwd = binding.signupPasswordInput.text.toString(),
            username = binding.signupUsernameInput.text.toString()
        )

    }

    fun onLaunchSignIn() {
        Log.d(TAG, "onLaunchSignIn::")
        ActivityNavigationUtil.navigateToActivity(
            this,
            SignInActivity::class.java,
            ActivityNavigationUtil.ActivityFinishMode.FINISH_ALL
        )

    }


    private fun trySignUp(email: String, pwd: String, username: String) {
        Log.d(TAG, "trySignUp:: email= $email, pwd= $pwd, username= $username")
        Amplify.Auth.signUp(
            email,
            pwd,
            AuthSignUpOptions.builder()
                .userAttribute(AuthUserAttributeKey.preferredUsername(), username)
                .userAttribute(AuthUserAttributeKey.email(), email)
                .build(),
            { result ->
                if (result.isSignUpComplete) {
                    when (result.nextStep.signUpStep) {
                        AuthSignUpStep.CONFIRM_SIGN_UP_STEP -> {
                            launchToSignUpConfirmation(email)
                        }
                        AuthSignUpStep.DONE -> {
                            onLaunchSignIn()
                        }
                    }

                } else {
                    showToastMessage("Something went wrong! Please retry.")
                }
            },
            { error ->
                showToastMessage(error.message)
                Log.e(TAG, error.message, error)
            }
        )
    }

    private fun launchToSignUpConfirmation(email: String) {
        ActivityNavigationUtil.navigateToActivity(
            this,
            SignUpConfirmationActivity::class.java,
            Bundle().apply {
                putString(EXTRA_EMAIL, email)
            },
            ActivityNavigationUtil.ActivityFinishMode.FINISH_ALL
        )
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
        private const val TAG = "SignUpActivity"
    }
}