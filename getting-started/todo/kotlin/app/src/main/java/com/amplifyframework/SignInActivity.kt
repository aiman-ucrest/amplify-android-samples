package com.amplifyframework

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.amplifyframework.samples.core.ActivityNavigationUtil
import com.amplifyframework.samples.gettingstarted.TodoListActivity
import com.amplifyframework.samples.gettingstarted.databinding.ActivitySigninBinding

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySigninBinding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.handler = this
        fetchIdentityId()

    }

    private fun fetchIdentityId() {
        Amplify.Auth.fetchAuthSession(
            { result ->
                val cognitoAuthSession = result as AWSCognitoAuthSession
                when (cognitoAuthSession.identityId.type) {
                    AuthSessionResult.Type.SUCCESS -> {
                        showToastMessage("Welcome back!")
                        launchTodoScreen()
                    }
                    AuthSessionResult.Type.FAILURE -> {
                        showToastMessage("Please sign in")
                        val error = cognitoAuthSession.identityId.error?.message ?: cognitoAuthSession.identityId.error.toString()
                        Log.d(TAG, "IdentityId not present because: $error")
                    }
                }
            },
            { error ->
                showToastMessage(error.message)
            }
        )
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
        ActivityNavigationUtil.navigateToActivity(
            this,
            SignUpActivity::class.java,
            ActivityNavigationUtil.ActivityFinishMode.KEEP_ACTIVITY
        )
    }

    fun onLaunchForgotPassword() {
        Log.d(TAG, "onLaunchForgotPassword::")
        ActivityNavigationUtil.navigateToActivity(
            this,
            ForgotPasswordActivity::class.java,
            ActivityNavigationUtil.ActivityFinishMode.KEEP_ACTIVITY
        )
    }


    private fun trySignIn(email: String, pwd: String) {
        Amplify.Auth.signIn(
            email,
            pwd,
            { result ->
                if (result.isSignInComplete) {
                    launchTodoScreen()
                } else {
                    showToastMessage("Sign in not complete!")
                }
            },
            { error -> showToastMessage(error.message) }
        )
    }

    private fun launchTodoScreen() {
        ActivityNavigationUtil.navigateToActivity(
            this,
            TodoListActivity::class.java,
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
        private const val TAG = "SignInActivity"
    }
}