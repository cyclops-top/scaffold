package scaffold.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import scaffold.permission.PermissionDispatchers


@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface PermissionDispatchersEntryPoint {
    fun permissionDispatchers(): PermissionDispatchers
}


