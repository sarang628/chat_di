package com.sarang.torang.di.chat_di

import com.sarang.torang.repository.ChatRepository
import com.sarang.torang.usecase.SubScribeRoomUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SubScribeRoomUseCaseImpl {
    @Singleton
    @Provides
    fun provideSubScribeRoomUseCase(
        chatRepository: ChatRepository,
    ): SubScribeRoomUseCase {
        return object : SubScribeRoomUseCase {
            override suspend fun invoke(
                roomId: Int,
                coroutineScope: CoroutineScope,
            ): Flow<HashMap<String, String>> {
                chatRepository.subscribe(roomId)
                return chatRepository.event(coroutineScope).map {
                    java.util.HashMap<String, String>().apply {
                        put("command", it.command ?: "")
                        put("payload", it.payload ?: "")
                    }
                }
            }
        }
    }
}