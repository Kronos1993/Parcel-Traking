package com.kronos.domain.repository.user

import com.kronos.domain.model.user.UserModel


interface UserLocalRepository {
    suspend fun saveUser(userModel: UserModel): UserModel

    suspend fun getUser(): UserModel

    suspend fun deleteUser()
}
