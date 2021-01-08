package com.paiwaddev.testwsapp.utils

import android.app.Application
import com.paiwaddev.testwsapp.data.local.UsersDataBase
import com.paiwaddev.testwsapp.data.repository.UsersRespository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MyApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    val dataBase by lazy { UsersDataBase.getDatabase(this,applicationScope) }
    val repository by lazy { UsersRespository(dataBase.userDao()) }
}