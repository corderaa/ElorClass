package com.example.elorclass.socketIO

import android.app.Activity
import android.util.Log
import com.example.elorclass.data.User
import com.example.elorclass.socketIO.config.Events
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

class SocketClient (private val activity: Activity){

    private val ipPort = "http://localhost:4000"
    private val socket: Socket = IO.socket(ipPort)

    //Log info
    private var tag = "socket.io"

    // Add all the events

    init {
        socket.on(Socket.EVENT_CONNECT) {
            Log.d(tag, "Connected to the socket")
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            Log.d(tag, "Disconnected from the socket")
        }


        // We get the answer from the socket when we login
        socket.on(Events.ON_RESPONSE_LOGIN.value) { args ->

            // The response from the server is a JSON object
            val response = args[0] as JSONObject

            val message = response.getString("message") as String

            val gson = Gson()
            val user = gson.fromJson(message, User::class.java)


        }
    }
}