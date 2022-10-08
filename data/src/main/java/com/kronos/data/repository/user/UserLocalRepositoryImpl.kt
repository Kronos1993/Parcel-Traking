package com.kronos.data.repository.user

import com.kronos.data.data_source.user.UserLocalDataSource
import com.kronos.domain.model.user.UserModel
import com.kronos.domain.repository.user.UserLocalRepository
import javax.inject.Inject

class UserLocalRepositoryImpl @Inject constructor(
    private val userLocalDataSource: UserLocalDataSource
) : UserLocalRepository {
    override suspend fun saveUser(userModel: UserModel): UserModel {
        return userLocalDataSource.saveUser(userModel)
    }

    override suspend fun getUser(): UserModel {
        return userLocalDataSource.getUser()
    }

    override suspend fun deleteUser() {
        userLocalDataSource.deleteUser()
    }

}