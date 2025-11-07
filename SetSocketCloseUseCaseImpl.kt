package com.sarang.torang.di.chat_di

import com.sarang.torang.repository.ChatRepository
import com.sarang.torang.usecase.SetSocketCloseUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SetSocketCloseUseCaseImpl {
    @Singleton
    @Provides
    fun provideSetSocketCloseUseCase(
        chatRepository: ChatRepository,
    ): SetSocketCloseUseCase {
        return object : SetSocketCloseUseCase {
            override fun invoke(roomId: Int) {
                chatRepository.unSubscribe(roomId)
            }
        }
    }
}