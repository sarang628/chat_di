package com.sarang.torang.di.chat_di

import com.sarang.torang.repository.ChatRepository
import com.sarang.torang.usecase.LoadChatRoomUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class LoadChatRoomUseCaseImpl {
    @Singleton
    @Provides
    fun provideLoadChatRoomUseCase(chatRepository: ChatRepository): LoadChatRoomUseCase {
        return object : LoadChatRoomUseCase {
            override suspend fun invoke() {
                return chatRepository.refreshAllChatRooms()
            }
        }
    }
}