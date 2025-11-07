package com.sarang.torang.di.chat_di

import com.sarang.torang.core.database.dao.LoggedInUserDao
import com.sarang.torang.usecase.IsSignInUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class IsSignInUseCaseImpl {
    @Singleton
    @Provides
    fun provideIsSignInUseCase(
        loggedInUserDao: LoggedInUserDao,
    ): IsSignInUseCase {
        return object : IsSignInUseCase {
            override fun invoke(): Flow<Boolean> {
                return loggedInUserDao.isLogin().map { it > 0 }
            }
        }
    }
}