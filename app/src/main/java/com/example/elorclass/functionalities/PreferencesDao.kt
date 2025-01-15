package com.example.elorclass.functionalities

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PreferencesDao {
    @Query("SELECT * FROM preferencesdb")
    fun getAll(): List<PreferencesDB>
    @Insert
    fun insertAll(vararg preferences: PreferencesDB)
    @Delete
    fun delete(preference: PreferencesDB)
    @Query("UPDATE preferencesdb SET language = :language WHERE userLogin = :userLogin")
    fun changeLanguage(language:String, userLogin:String)
    @Query("UPDATE preferencesdb SET theme = :theme WHERE userLogin = :userLogin")
    fun changeTheme(theme:String, userLogin:String)
    @Query("SELECT * FROM preferencesdb WHERE userLogin = :userLogin LIMIT 1")
    fun getPreferenceByLogin(userLogin: String): PreferencesDB?
}