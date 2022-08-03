package com.amplifyframework

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.core.Amplify
import com.amplifyframework.samples.core.ActivityNavigationUtil
import com.amplifyframework.samples.gettingstarted.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.handler = this

    }

    fun onResetPassword() {
        Log.d(TAG, "onResetPassword::")
        if (binding.resetCodeInput.text.isNullOrBlank() || binding.resetPasswordPasswordInput.text.isNullOrBlank()) {
            showToastMessage("Empty field(s)!")
            return
        }
        tryResetPassword(
            code = binding.resetCodeInput.text.toString(),
            newPwd = binding.resetPasswordPasswordInput.text.toString()
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


    private fun tryResetPassword(code: String, newPwd: String) {
        Amplify.Auth.confirmResetPassword(
            newPwd,
            code,
            {
                showToastMessage("Password reset successful")
                onLaunchSignIn()
            },
            { error ->
                showToastMessage(error.message ?: error.toString())
                Log.w(TAG, "Reset Password Failed!", error)
            }
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
        private const val TAG = "ResetPasswordActivity"
    }
}