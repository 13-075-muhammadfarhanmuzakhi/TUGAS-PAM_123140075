package com.notenest.app.platform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.net.InetSocketAddress
import java.net.Socket

actual class NetworkMonitor {
    actual fun isConnected(): Boolean {
        return try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    actual fun observeConnectivity(): Flow<Boolean> = flow {
        var lastState = isConnected()
        emit(lastState)
        while (true) {
            delay(3000)
            val current = isConnected()
            if (current != lastState) {
                lastState = current
                emit(current)
            }
        }
    }.flowOn(Dispatchers.IO)
}