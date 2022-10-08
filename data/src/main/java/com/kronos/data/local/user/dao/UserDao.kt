package com.kronos.data.local.user.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kronos.data.local.user.entity.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(user: UserEntity)

    @Query("SELECT * FROM USER LIMIT 1")
    suspend fun getUser(): UserEntity

    @Query("DELETE FROM USER")
    suspend fun deleteUser()

}