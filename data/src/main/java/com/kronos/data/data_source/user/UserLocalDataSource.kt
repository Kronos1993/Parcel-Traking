package com.kronos.data.data_source.user

import com.kronos.domain.model.user.UserModel


interface UserLocalDataSource {
    suspend fun saveUser(userModel: UserModel): UserModel
    suspend fun getUser(): UserModel
    suspend fun deleteUser()

}