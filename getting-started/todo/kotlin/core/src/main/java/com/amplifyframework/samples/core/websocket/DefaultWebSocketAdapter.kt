package com.amplifyframework.samples.core.websocket

import android.util.Log
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

class DefaultWebSocketAdapter : IWebSocketAdapter {
    private val client: OkHttpClient =
        OkHttpClient.Builder()
            .pingInterval(60, TimeUnit.SECONDS)
            .build()
    private var session: WebSocket? = null
    var hasActiveConnection = false
        private set
    var isConnectRequested = false
        private set

    private val TAG = "DefaultWebSocketAdapter"

    /**
    * @return true if initiated, else false
    * */
    override fun create(url: String, observer: WebSocketAdapterObserver): Boolean {
        Log.d(TAG, "create:: creating connection for url= $url..")
        if (hasActiveConnection || isConnectRequested) {
            Log.w(TAG, "create:: already have an active connection! please close it first & retry.")
            return false
        }
        isConnectRequested = true
        val request = Request.Builder().url(url).build()
        client.newWebSocket(request, object : WebSocketListener() {
           override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "onOpen::")
               session = webSocket
               hasActiveConnection = true
               isConnectRequested = false
               observer.onConnect()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.w(TAG, "onFailure::", t)
                isConnectRequested = false
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
        return true
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


}