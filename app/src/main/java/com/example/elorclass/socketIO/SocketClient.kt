package com.example.elorclass.socketIO

import android.app.Activity
import android.util.Log
import com.example.elorclass.LoginActivity
import com.example.elorclass.data.User
import com.example.elorclass.socketIO.config.Events
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

class SocketClient(private val activity: LoginActivity) {

    private val ipPort = "http://10.0.2.2:4000"
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
            try {
                val response = args[0] as JSONObject
                val success = response.getBoolean("success")
                val message = response.getString("message")

                activity.runOnUiThread{
                    if(success){
                        val user = Gson().fromJson(message, User::class.java)
                        activity.loginSuccess(user)
                        Log.d(tag, "Answer to Login: $user")

                    } else {
                        activity.loginFailed(message)
                    }
                }


            } catch (e: Exception) {
                Log.e(tag, "Error parsing response: ${e.message}")
            }
        }


        socket.on(Events.ON_GET_ALL.value) { args ->

            try {
                val response = args[0] as JSONObject
                val message = response.getString("message") as String
                val gson = Gson()
                val itemType = object : TypeToken<List<User>>() {}.type
                val list = gson.fromJson<List<User>>(message, itemType)
                Log.d(tag, "Answer to GetAll: $list")

            } catch (e: Exception) {
                Log.e(tag, "Error: ${e.message}")
            }


        }


    }

    fun connect() {
        socket.connect()
        Log.d(tag, "Connecting to the server")
    }

    fun emit(value: Any, jsonObject: String) {
    socket.emit(value.toString(), jsonObject)
    }

}
