package com.example.elorclass.functionalities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RememberMeDB(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "user") val user: String,
    @ColumnInfo(name = "password") val password: String,
)
