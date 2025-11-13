package com.sarang.torang.di.chat_di

import com.sarang.torang.core.database.dao.chat.ChatRoomDao
import com.sarang.torang.repository.ChatRepository
import com.sarang.torang.usecase.DeleteChatRoomUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DeleteChatRoomUseCaseImpl {
    @Singleton
    @Provides
    fun provideDeleteChatRoomUseCase(
        chatRepository: ChatRepository
    ): DeleteChatRoomUseCase {
        return object : DeleteChatRoomUseCase {
            override suspend fun invoke(roomId: Int) {
                chatRepository.deleteChatRoom(roomId = roomId)
            }
        }
    }
}