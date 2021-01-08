package com.paiwaddev.testwsapp.data.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.paiwaddev.testwsapp.data.local.UsersDao
import com.paiwaddev.testwsapp.data.model.Users
class UsersRespository(private val usersDao: UsersDao) {

    fun userLogin(user: String, pass: String): LiveData<Users> = usersDao.userLogin(user, pass)
    fun userByID(userID: Long): LiveData<Users> = usersDao.userByID(userID)
    @WorkerThread
    suspend fun insert(user: Users) {
        usersDao.insert(user)
    }
}