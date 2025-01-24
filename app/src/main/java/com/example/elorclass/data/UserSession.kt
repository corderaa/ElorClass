package com.example.elorclass.data

object UserSession {

    private var user: User? = null

    fun setUserSession(
        user: User
    ) {
        this.user = user
    }

    fun fetchUser(): User? = user

    fun clearSession() {
        user = null
    }
}
