package com.kronos.data.local.user.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "USER"/*,
    indices = [
        Index(value = ["ID"], unique = true),
        Index(value = ["TRACKING_NUMBER"], unique = true)
    ]*/
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID") val id: Int,
    @ColumnInfo(name = "NAME") val name: String,
    @ColumnInfo(name = "LASTNAME") val lastname: String,
    @ColumnInfo(name = "PHONE") val phone: String,
    @ColumnInfo(name = "EMAIL") val email: String,
    @ColumnInfo(name = "ADDRESS") val address: String,
)

