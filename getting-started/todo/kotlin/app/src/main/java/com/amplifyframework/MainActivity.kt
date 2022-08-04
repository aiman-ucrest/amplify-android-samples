package com.amplifyframework

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import com.amplifyframework.samples.core.ActivityNavigationUtil
import com.amplifyframework.samples.core.Constants
import com.amplifyframework.samples.core.websocket.DefaultWebSocketAdapter
import com.amplifyframework.samples.core.websocket.WebSocketAdapterObserver
import com.amplifyframework.samples.gettingstarted.R
import com.amplifyframework.samples.gettingstarted.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity: AppCompatActivity() {
    private val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding
    private val handler = Handler(Looper.getMainLooper())
    private val webSocketAdapter = DefaultWebSocketAdapter()
    private val observer = object : WebSocketAdapterObserver() {
        override fun onConnect() {
            updateDescTextView("===Connected===")
        }

        override fun onMessage(message: String) {
            updateDescTextView("\tServer: $message")
        }

        override fun onClose(status: String) {
            updateDescTextView("===Closed===")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.handler = this

        binding.descTextview.movementMethod = ScrollingMovementMethod()

        //fetchIdentityId()
    }

    override fun onResume() {
        initWebSocket()
        super.onResume()
    }

    override fun onPause() {
        closeWebSocket()
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.signout -> {
                Amplify.Auth.signOut(
                    {
                        showToastMessage("Signed out")
                        ActivityNavigationUtil.navigateToActivity(
                            this,
                            SignInActivity::class.java,
                            ActivityNavigationUtil.ActivityFinishMode.FINISH_ALL
                        )
                    },
                    { e ->
                        showToastMessage(e.message ?: e.toString())
                        Log.w(TAG, "Sign out unsuccessful", e)
                    }
                )
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    fun onPing() {
        Log.d(TAG, "onPing::")
        val msg= "\tMe: Ping!"
        val enqueued = webSocketAdapter.send(msg)
        if (enqueued) {
            updateDescTextView(msg)
        } else {
            updateDescTextView(("ERROR:: Ping failed!"))
        }
    }

    private fun closeWebSocket() {
        val initiated = webSocketAdapter.close()
        Log.d(TAG, "closeWebSocket:: initiated= $initiated")
    }

    private fun initWebSocket() {
        val initiated = webSocketAdapter.create(Constants.WEB_SOCKET_URL, observer)
        Log.d(TAG, "initWebSocket:: initiated= $initiated")
    }

    private fun fetchIdentityId() {
        Amplify.Auth.fetchAuthSession(
            { result ->
                val cognitoAuthSession = result as AWSCognitoAuthSession
                when (cognitoAuthSession.identityId.type) {
                    AuthSessionResult.Type.SUCCESS -> {
                        updateDescTextView("IdentityId: " + cognitoAuthSession.identityId.value)
                    }
                    AuthSessionResult.Type.FAILURE -> {
                        val error = cognitoAuthSession.identityId.error?.message ?: cognitoAuthSession.identityId.error.toString()
                        showToastMessage("IdentityId not present because: $error")
                        updateDescTextView(error)
                    }
                }
            },
            { error ->
                showToastMessage(error.message)
                updateDescTextView(error.message ?: error.toString())
            }
        )
    }

    private fun updateDescTextView(text: String) {
        handler.post {
            val date = Calendar.getInstance().time
            // val formatter = SimpleDateFormat("dd MMM yyyy HH:mm:ss.SSS", Locale.ENGLISH) //or use getDateTimeInstance()
            val formatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.ENGLISH) //or use getDateTimeInstance()
            val formattedDate = formatter.format(date)

            //binding.descTextview.append("\n$formattedDate")
            binding.descTextview.append("\n[$formattedDate]\t\t$text\n")
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