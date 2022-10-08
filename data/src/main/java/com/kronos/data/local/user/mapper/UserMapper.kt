package com.kronos.data.local.user.mapper

import com.kronos.data.local.user.entity.UserEntity
import com.kronos.domain.model.user.UserModel


internal fun UserEntity.toDomain(): UserModel =
    UserModel(
        name = name,
        lastname = lastname,
        phone = phone,
        email = email,
        address = address,
    )

internal fun UserModel.toEntity(): UserEntity =
    UserEntity(
        id = 1,
        name = name,
        lastname = lastname,
        phone = phone,
        email = email,
        address = address,
    )

