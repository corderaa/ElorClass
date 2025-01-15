package com.example.elorclass.functionalities

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RememberMeDB::class, PreferencesDB::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rememberMeDao(): RememberMeDao
    abstract fun preferencesDao(): PreferencesDao
}