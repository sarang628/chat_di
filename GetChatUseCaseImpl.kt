package com.sarang.torang.di.chat_di

import com.sarang.torang.BuildConfig
import com.sarang.torang.core.database.dao.LoggedInUserDao
import com.sarang.torang.data.Chat
import com.sarang.torang.data.ChatMessage
import com.sarang.torang.repository.ChatRepository
import com.sarang.torang.usecase.GetChatsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class GetChatUseCaseImpl {
    @Singleton
    @Provides
    fun provideGetChatUseCase(
        chatRepository: ChatRepository,
        loggedInUserDao: LoggedInUserDao,
    ): GetChatsUseCase {
        return object : GetChatsUseCase {
            override fun invoke(roomId: Int): Flow<List<Chat>> {
                return chatRepository.getChatsFlow(roomId)
                    .combine(loggedInUserDao.getLoggedInUserFlow()) { list, loggedInUser ->
                        list.map { chatEntity ->
                            chatEntity.toChat(chatEntity.userId == loggedInUser?.userId)
                        }
                    }
            }
        }
    }

}



private fun ChatMessage.toChat(isMe: Boolean): Chat {
    return Chat(
        userId = this.userId,
        message = this.message,
        createDate = this.createDate,
        profileUrl = BuildConfig.PROFILE_IMAGE_SERVER_URL + this.user.profilePicUrl,
        userName = this.user.userName,
        isMe = isMe,
        isSending = this.sending
    )
}