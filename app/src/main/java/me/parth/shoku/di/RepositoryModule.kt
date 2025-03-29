package me.parth.shoku.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import me.parth.shoku.data.repository.FoodRepositoryImpl
import me.parth.shoku.domain.repository.FoodRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton // Binds the implementation to the interface
    abstract fun bindFoodRepository(impl: FoodRepositoryImpl): FoodRepository
} 