package scaffold.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import scaffold.permission.DefaultPermissionDispatchersHost
import scaffold.permission.PermissionDispatchers
import scaffold.permission.PermissionDispatchersHost
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class PermissionDispatchersModule {

    @Singleton
    @Binds
    internal abstract fun permissionDispatchers(repo: DefaultPermissionDispatchersHost): PermissionDispatchers

    @Singleton
    @Binds
    internal abstract fun permissionDispatchersHost(repo: DefaultPermissionDispatchersHost): PermissionDispatchersHost
}



