package com.amplifyframework

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MainViewModel(private val app: Application): AndroidViewModel(app) {
    val isConnected = MutableLiveData(false)
}