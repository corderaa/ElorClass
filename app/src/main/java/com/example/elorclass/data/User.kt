package com.example.elorclass.data

import java.sql.Timestamp

data class User(
    val id: Long?= null,
    val userTypes: UserTypes? = null,
    val name: String? = null,
    val lastNames: String?= null,
    var dni: String? = null,
    val address: String?= null,
    val photo: String?= null,
    val phone: String?= null,
    val phone2: String?= null,
    val email: String?= null,
    val emailVerifiedAt: Timestamp?= null,
    val studies: String?= null,
    var password: String?= null,
    val rememberToken: String?= null,
    val schoolyear: Int?= null,
    val dual: Boolean?= null,
    val registered: Boolean?= null,
    val createdAt: Timestamp?= null,
    val updatedAt: Timestamp?= null
)