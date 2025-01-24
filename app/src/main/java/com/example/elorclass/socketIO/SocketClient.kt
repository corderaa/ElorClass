package com.example.elorclass.socketIO

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
    val gson = Gson()

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

        socket.on("onLoginAnswer") { args ->
            try {
                val response = JSONObject(args[0] as String);
                Log.d(tag, "res: $response")
                activity.runOnUiThread {
                    if (!response.has("code") || !response.get("code").equals(500)) {
                        val user: User = gson.fromJson(response.toString(), User::class.java)
                        Log.d(tag, "res:");
                        activity.loginSuccess(user)

                        Log.d(tag, "Answer to Login: $user")

                    } else {
                        activity.loginFailed(response.getString("msg"))
                    }
                }
            } catch (e: Exception) {
                Log.e(tag, "Error parsing response: ${e.message}")
            }
        }

        socket.on("onPasswordChangeAnswer"){ args ->

            try {
                val response = JSONObject(args[0] as String);
                Log.d(tag, "res: $response")
                activity.runOnUiThread {
                    if (!response.has("code") || !response.get("code").equals(500)) {
                        val user: User = gson.fromJson(response.toString(), User::class.java)
                        Log.d(tag, "res:");
                        activity.loginSuccess(user)

                        Log.d(tag, "Answer to Login: $user")

                    } else {
                        activity.loginFailed(response.getString("msg"))
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
