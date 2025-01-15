package com.example.elorclass.data

data class User(
    val name: String,
    val surname: String,
    val id: String,
    val adress: String,
    val firstTelephone: String,
    val secondTelephone: String,
    val studies: String,
    val password: String,
    val schoolyear: Int,
    val dual: Boolean,
    val registered: Boolean
)
