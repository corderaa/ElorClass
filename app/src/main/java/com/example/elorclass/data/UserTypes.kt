package com.example.elorclass.data

import java.sql.Timestamp

data class UserTypes(
    val id: Long? = null,
    val role: String? = null,
    val description: String? = null,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

