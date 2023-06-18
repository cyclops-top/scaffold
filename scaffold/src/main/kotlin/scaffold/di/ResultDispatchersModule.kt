package scaffold.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import scaffold.navigation.DefaultResultDispatchers
import scaffold.navigation.ResultDispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ResultDispatchersModule {

    @Singleton
    @Binds
    internal abstract fun resultDispatchers(repo: DefaultResultDispatchers): ResultDispatchers

}