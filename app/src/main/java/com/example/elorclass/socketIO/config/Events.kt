package com.example.elorclass.socketIO.config

enum class Events(val value: String) {
    ON_LOGIN("onLogin"),
    ON_GET_ALL("onGetAll"),
    ON_RESPONSE_LOGIN("onLoginAnswer"),
    ON_REGISTER("onRegister"),
    ON_RESPONSE_REGISTER("onRegisterAnswer"),
    TEST_EVENT("testEvent"),
    TEST_RESPONSE_EVENT("testEventAnswer"),
    ON_PASSWORD_CHANGE("onPasswordChange"),
    ON_RESPONSE_PASSWORD_CHANGE("onPasswordChangeAnswer"),
}