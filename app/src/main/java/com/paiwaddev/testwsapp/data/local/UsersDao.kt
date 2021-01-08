package com.paiwaddev.testwsapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.paiwaddev.testwsapp.data.model.Users

@Dao
interface UsersDao {
    @Query("SELECT *FROM `user_table` WHERE UserID in (:userID)")
    fun userByID(userID: Long): LiveData<Users>
    @Query("SELECT *FROM `user_table` WHERE Username in (:user) AND Password in (:pass)")
    fun userLogin(user: String, pass: String): LiveData<Users>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: Users)
}