package com.example.elorclass.data

import java.sql.Timestamp

data class User(
    val id: Long?= null,
    val userTypes: UserTypes? = null,
    var name: String? = null,
    var lastNames: String?= null,
    var dni: String? = null,
    var address: String?= null,
    val photo: String?= null,
    var phone: String?= null,
    var phone2: String?= null,
    val email: String?= null,
    val emailVerifiedAt: Timestamp?= null,
    val studies: String?= null,
    var password: String?= null,
    val rememberToken: String?= null,
    val schoolyear: Int?= null,
    val dualStudies: Boolean?= null,
    var registered: Boolean?= null,
    val createdAt: Timestamp?= null,
    val updatedAt: Timestamp?= null
)