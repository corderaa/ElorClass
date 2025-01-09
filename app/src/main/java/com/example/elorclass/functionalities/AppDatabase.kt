package com.example.elorclass.functionalities

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RememberMeDB::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rememberMeDao(): RememberMeDao
}