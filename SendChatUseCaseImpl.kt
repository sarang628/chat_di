package com.sarang.torang.di.chat_di

import com.sarang.torang.repository.ChatRepository
import com.sarang.torang.usecase.SendChatUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SendChatUseCaseImpl {
    @Singleton
    @Provides
    fun provideSendChatUseCase(
        chatRepository: ChatRepository,
    ): SendChatUseCase {
        return object : SendChatUseCase {
            override suspend fun invoke(roomId: Int, message: String) {
                chatRepository.addChat(roomId, message)
            }
        }
    }
}