package scaffold.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import scaffold.navigation.ResultDispatchers

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface ResultDispatchersEntryPoint {
    fun resultDispatchers(): ResultDispatchers
}