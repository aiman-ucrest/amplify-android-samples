package com.amplifyframework.samples.core.websocket

abstract class WebSocketAdapterObserver {
    abstract fun onConnect()

    abstract fun onMessage(message: String)

    abstract fun onClose(status: String)

    abstract fun onFailure(errorMsg: String)
}