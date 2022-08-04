package com.amplifyframework.samples.core.websocket

import android.util.Log
import okhttp3.*
import okio.ByteString
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class DefaultWebSocketAdapter : IWebSocketAdapter {
    private val TAG = "DefaultWebSocketAdapter"
    val TIMEOUT_INTERVAL = 60L
    val RECONNECT_DELAY = 5L

    private val client: OkHttpClient =
        OkHttpClient.Builder()
            .pingInterval(TIMEOUT_INTERVAL, TimeUnit.SECONDS)
            .build()
    private var session: WebSocket? = null
    var hasActiveConnection = false
        private set
    var isConnectRequested = false
        private set

    /**
     * @return true if initiated, else false
     * */
    override fun create(url: String, observer: WebSocketAdapterObserver): Boolean {
        Log.d(TAG, "create:: creating connection for url= $url..")
        if (isHasConnection()) {
            Log.w(TAG, "create:: already have an active connection! please close it first & retry.")
            return false
        }
        /*if (client.dispatcher.executorService.isShutdown) {
            Log.w(TAG, "create:: executorService already shutdown!")
            return false
        }*/
        isConnectRequested = true

        client.connectionPool.evictAll()

        val request = Request.Builder().url(url).build()
        session = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "onOpen::")
                // session = webSocket
                hasActiveConnection = true
                isConnectRequested = false
                observer.onConnect()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.w(TAG, "onFailure:: response= $response", t)
                hasActiveConnection = false
                isConnectRequested = false
                observer.onFailure(t.message ?: t.toString())
                closeAndReconnect(url, observer)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d(TAG, "onMessage:: text= $text")
                observer.onMessage(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                observer.onMessage(bytes.toString())
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "onClosing:: code= $code, reason= $code")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d(TAG, "onClosed:: code= $code, reason= $code")
                hasActiveConnection = false
                isConnectRequested = false
                observer.onClose(reason)
            }
        })

        // client.connectionPool.evictAll()

        return true
    }

    private fun isHasConnection() = hasActiveConnection || isConnectRequested

    private fun closeAndReconnect(url: String, observer: WebSocketAdapterObserver) {
        Log.d(TAG, "closeAndReconnect::")
        Executors.newSingleThreadScheduledExecutor().schedule({
            if (client.dispatcher.executorService.isShutdown) {
                Log.w(TAG, "closeAndReconnect:: executorService already shutdown!")
                return@schedule
            }
            Log.d(TAG, "closeAndReconnect:: execute after 5s delay")
            close()
            if (!isHasConnection()) {
                create(url, observer)
            }
        }, RECONNECT_DELAY, TimeUnit.SECONDS) // reconnect in after 5s
    }

    /**
     * @return true if initiated, else false
     * */
    override fun close(): Boolean {
        Log.d(TAG, "close:: closing connection..")
        isConnectRequested = false
        if (session == null) {
            Log.w(TAG, "close:: no active session, do nothing..")
            return false
        }
        // ref: https://datatracker.ietf.org/doc/html/rfc6455#section-7.4
        session?.close(1000, "Close Requested")
        session = null
        return true
    }

    fun shutdown() {
        client.connectionPool.evictAll()
        client.dispatcher.executorService.shutdown()
    }

    /**
     * @return true if enqueued, else false
     * */
    override fun send(message: String): Boolean {
        Log.d(TAG, "send:: sending message= $message..")
        return session?.send(message) ?: kotlin.run {
            Log.w(TAG, "send:: no active session, do nothing..")
            false
        }
    }


}