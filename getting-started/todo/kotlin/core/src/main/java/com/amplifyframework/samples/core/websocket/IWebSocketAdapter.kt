package com.amplifyframework.samples.core.websocket

interface IWebSocketAdapter {
    fun create(url: String, observer: WebSocketAdapterObserver)

    fun close()
}