package com.example.elorclass.data

import java.sql.Timestamp

data class User(
    var id: Long?= null,
    var userTypes: UserTypes? = null,
    var name: String? = null,
    var lastNames: String?= null,
    var dni: String? = null,
    var address: String?= null,
    var photo: String?= null,
    var phone: String?= null,
    var phone2: String?= null,
    var email: String?= null,
    var emailVerifiedAt: Timestamp?= null,
    var studies: String?= null,
    var password: String?= null,
    var rememberToken: String?= null,
    var schoolYear: Int?= null,
    var dualStudies: Boolean?= null,
    var registered: Boolean?= null,
    var createdAt: Timestamp?= null,
    var updatedAt: Timestamp?= null
)