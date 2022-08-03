package com.amplifyframework

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.amplifyframework.samples.gettingstarted.databinding.ActivityMainBinding

class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchIdentityId()
    }

    private fun fetchIdentityId() {
        Amplify.Auth.fetchAuthSession(
            { result ->
                val cognitoAuthSession = result as AWSCognitoAuthSession
                when (cognitoAuthSession.identityId.type) {
                    AuthSessionResult.Type.SUCCESS -> {
                        setIdentityId("IdentityId: " + cognitoAuthSession.identityId.value)
                    }
                    AuthSessionResult.Type.FAILURE -> {
                        val error = cognitoAuthSession.identityId.error?.message ?: cognitoAuthSession.identityId.error.toString()
                        showToastMessage("IdentityId not present because: $error")
                        setIdentityId(error)
                    }
                }
            },
            { error ->
                showToastMessage(error.message)
                setIdentityId(error.message ?: error.toString())
            }
        )
    }

    private fun setIdentityId(text: String) {
        handler.post {
            binding.identityIdTextview.visibility = View.VISIBLE
            binding.identityIdTextview.text = text
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
}