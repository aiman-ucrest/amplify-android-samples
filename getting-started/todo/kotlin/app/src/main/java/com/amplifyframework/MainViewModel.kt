package com.amplifyframework

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MainViewModel(private val app: Application): AndroidViewModel(app) {
    val isConnected = MutableLiveData(false)
    val messageToSend = MutableLiveData(MESSAGES[0])

    fun nextMessage() {
        messageToSend.value?.let {
            val currIdx = MESSAGES.indexOf(it)
            var nextIdx = currIdx + 1
            if (nextIdx > MESSAGES.lastIndex) {
                nextIdx = 0
            }
            messageToSend.postValue(MESSAGES[nextIdx])
        }
    }

    companion object {
        private val MESSAGES = arrayListOf("Hey!", "Hello~", "Good Day")

    }
}