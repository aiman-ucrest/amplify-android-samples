package com.amplifyframework

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.auth.result.step.AuthResetPasswordStep
import com.amplifyframework.core.Amplify
import com.amplifyframework.samples.core.ActivityNavigationUtil
import com.amplifyframework.samples.gettingstarted.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.handler = this

    }

    fun onRequestForgetPassword() {
        Log.d(TAG, "onRequestForgetPassword::")
        if (binding.emailInput.text.isNullOrBlank()) {
            showToastMessage("Empty field(s)!")
            return
        }
        tryRequestCode(
            email = binding.emailInput.text.toString()
        )

    }

    private fun tryRequestCode(email: String) {
        Amplify.Auth.resetPassword(
            email,
            { result ->
                when (result.nextStep.resetPasswordStep) {
                    AuthResetPasswordStep.CONFIRM_RESET_PASSWORD_WITH_CODE -> {
                        launchConfirmationResetPassword()
                    }
                    AuthResetPasswordStep.DONE -> {
                        showToastMessage("Password reset successful")
                        onLaunchSignIn()
                    }
                    else -> {
                        showToastMessage("Something went wrong! Please retry.")
                    }
                }

            },
            { error ->
                showToastMessage(error.message)
                Log.w(TAG, "Reset Password Failed!", error)
            }
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

    private fun launchConfirmationResetPassword() {
        Log.d(TAG, "launchConfirmationResetPassword::")
        ActivityNavigationUtil.navigateToActivity(
            this,
            ResetPasswordActivity::class.java,
            ActivityNavigationUtil.ActivityFinishMode.KEEP_ACTIVITY
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
        private const val TAG = "ForgotPasswordActivity"
    }
}