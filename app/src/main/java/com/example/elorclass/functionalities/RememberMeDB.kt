package com.example.elorclass.functionalities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["userLogin"], unique = true)]
)
data class RememberMeDB(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "userLogin") val userLogin: String,
    @ColumnInfo(name = "password") val password: String,
)
