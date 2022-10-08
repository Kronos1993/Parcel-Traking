package com.kronos.data.local.user

import com.kronos.data.data_source.user.UserLocalDataSource
import com.kronos.data.di.ApplicationDatabaseFactory
import com.kronos.data.local.LocalDatabaseFactory
import com.kronos.data.local.database.ApplicationDatabase
import com.kronos.data.local.parcel.mapper.toDomain
import com.kronos.data.local.parcel.mapper.toEntity
import com.kronos.data.local.user.mapper.toDomain
import com.kronos.data.local.user.mapper.toEntity
import com.kronos.domain.model.parcel.ParcelModel
import com.kronos.domain.model.user.UserModel
import javax.inject.Inject

class UserLocalDatasourceImpl @Inject constructor(
    @ApplicationDatabaseFactory private val databaseFactory: LocalDatabaseFactory,
) : UserLocalDataSource {

    override suspend fun saveUser(user: UserModel): UserModel {
        var result = UserModel()

        try {
            val userEntity = user.toEntity()

            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            internalDb.userDao().insertOrUpdate(userEntity)

            result = internalDb.userDao()
                .getUser().let {
                    it.toDomain()
                }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return result
    }

    override suspend fun getUser(): UserModel {
        var result = UserModel()

        try {
            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            result = internalDb.userDao().getUser().toDomain()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return result
    }

    override suspend fun deleteUser() {
        try {
            val internalDb = databaseFactory.loadLocalDatabase() as ApplicationDatabase
            internalDb.userDao().deleteUser()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


}
