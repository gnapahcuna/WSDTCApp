package com.paiwaddev.testwsapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class Users (
    @ColumnInfo(name = "Username") val username: String,
    @ColumnInfo(name = "Password") val password: String,
    @ColumnInfo(name = "Firstname") val firstname: String,
    @ColumnInfo(name = "Lastname") val lastname: String,
    @ColumnInfo(name = "CardId") val cardId: String,
    @ColumnInfo(name = "Phone") val phone: String?,
    @ColumnInfo(name = "Image") val image: ByteArray?,
    @PrimaryKey(autoGenerate = true) var UserID: Long = 0,
)