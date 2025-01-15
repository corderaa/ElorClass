package com.example.elorclass.functionalities

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RememberMeDao {
    @Query("SELECT * FROM remembermedb")
    fun getAll(): List<RememberMeDB>
    @Insert
    fun insertAll(vararg users: RememberMeDB)
    @Delete
    fun delete(user: RememberMeDB)
}