package com.paiwaddev.testwsapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.paiwaddev.testwsapp.data.model.Users
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Users::class),version = 1, exportSchema = false)
abstract class UsersDataBase : RoomDatabase(){
    abstract fun userDao(): UsersDao

    companion object{
        @Volatile
        private var INSTANCE: UsersDataBase? = null
        private val DATABASE_NAME = "user_database"
        fun getDatabase(context: Context,scope: CoroutineScope) =
            INSTANCE
                ?: synchronized(this){
                    INSTANCE ?: buildDatabase(context,scope)
                        .also { INSTANCE = it }
                }
        private fun buildDatabase(context: Context,scope: CoroutineScope): UsersDataBase {
            return Room.databaseBuilder(
                context,
                UsersDataBase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .addCallback(TransacDatabaseCallback(scope))
                .build()
        }

        private class TransacDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.userDao())
                    }
                }
            }
        }
       fun populateDatabase(usersDao: UsersDao) {
            //default data
        }
    }
}