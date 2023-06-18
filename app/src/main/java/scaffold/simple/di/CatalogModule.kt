package scaffold.simple.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import scaffold.simple.Catalog
import scaffold.simple.R
import javax.inject.Singleton

/** @author justin on 2023/6/17 */
@Module
@InstallIn(SingletonComponent::class)
object CatalogModule {
    @Provides
    @Singleton
    fun catalog(items: Set<Catalog.Item.Destination>): Catalog {
        return Catalog(items)
    }


    @Singleton
    @Provides
    @IntoSet
    fun pickers(): Catalog.Item.Destination {
        return Catalog.Item.Destination("", "Pickers", R.id.pickersFragment)
    }
}