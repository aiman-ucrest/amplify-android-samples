package com.amplifyframework.samples.core.websocket

interface IWebSocketAdapter {
    fun create(url: String, observer: WebSocketAdapterObserver): Boolean

    fun close(): Boolean

    fun send(message: String): Boolean
}