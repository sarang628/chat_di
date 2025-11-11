package com.sarang.torang.di.chat_di

import com.sarang.torang.repository.ChatRepository
import com.sarang.torang.usecase.LoadChatUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class LoadChatUseCaseImpl {
    @Singleton
    @Provides
    fun provideLoadChatUseCase(
        chatRepository: ChatRepository,
    ): LoadChatUseCase {
        return object : LoadChatUseCase {
            override suspend fun invoke(roomId: Int) {
                chatRepository.loadChats(roomId)
            }
        }
    }
}