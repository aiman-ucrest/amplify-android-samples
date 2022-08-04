package com.amplifyframework

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.core.Amplify
import com.amplifyframework.samples.core.ActivityNavigationUtil
import com.amplifyframework.samples.core.Constants.EXTRA_IS_NEED_CODE
import com.amplifyframework.samples.gettingstarted.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding
    private val handler = Handler(Looper.getMainLooper())
    var isNeedCode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isNeedCode = intent.extras?.getBoolean(EXTRA_IS_NEED_CODE, false) ?: false

        binding.handler = this

    }

    fun onChangeRequest() {
        Log.d(TAG, "onChangeRequest::")
        val hasMissingInputs = (isNeedCode && binding.resetCodeInput.text.isNullOrBlank())
                || (!isNeedCode && binding.currentPasswordInput.text.isNullOrBlank())
                || binding.newPasswordInput.text.isNullOrBlank()
        if (hasMissingInputs) {
            showToastMessage("Empty field(s)!")
            return
        }
        if (isNeedCode) {
            tryResetPassword(
                code = binding.resetCodeInput.text.toString(),
                newPwd = binding.newPasswordInput.text.toString()
            )
        } else {
            tryChangePassword(
                currentPwd = binding.currentPasswordInput.text.toString(),
                newPwd = binding.newPasswordInput.text.toString()
            )
        }


    }

    private fun onLaunchSignIn() {
        Log.d(TAG, "onLaunchSignIn::")
        ActivityNavigationUtil.navigateToActivity(
            this,
            SignInActivity::class.java,
            ActivityNavigationUtil.ActivityFinishMode.FINISH_ALL
        )
    }

    private fun tryChangePassword(currentPwd: String, newPwd: String) {
        Amplify.Auth.updatePassword(currentPwd, newPwd,
            {
                showToastMessage("Password change successful")
                finish() // go back to prev activity
            },
            { error ->
                showToastMessage(error.message ?: error.toString())
                Log.w(TAG, "Change Password Failed!", error)
            })
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