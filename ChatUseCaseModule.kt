package com.sarang.torang.di.chat_di

import android.text.TextUtils
import com.sarang.torang.BuildConfig
import com.sarang.torang.compose.chatroom.ChatRoomUiState
import com.sarang.torang.compose.chatroom.ChatUiState
import com.sarang.torang.data.ChatUser
import com.sarang.torang.data.dao.ChatDao
import com.sarang.torang.data.dao.LoggedInUserDao
import com.sarang.torang.repository.ChatRepository
import com.sarang.torang.usecase.GetChatRoomUseCase
import com.sarang.torang.usecase.GetChatUseCase
import com.sarang.torang.usecase.GetUserByRoomIdUseCase
import com.sarang.torang.usecase.LoadChatRoomUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ChatUseCaseModule {
    @Singleton
    @Provides
    fun provideGetChatRoomUseCase(
        chatRepository: ChatRepository,
        loggedInUserDao: LoggedInUserDao,
    ): GetChatRoomUseCase {
        return object : GetChatRoomUseCase {
            override fun invoke(): Flow<List<ChatRoomUiState>> {
                return loggedInUserDao.getLoggedInUser()
                    .combine(chatRepository.getChatRoomsWithParticipantsAndUsers()) { loggedInUser, list ->
                        list.map { chatRoomEntity ->
                            ChatRoomUiState(
                                chatRoomEntity.chatRoomEntity.roomId,
                                list = chatRoomEntity.participantsWithUsers
                                    .filter {
                                        !TextUtils.equals(
                                            loggedInUser?.userName,
                                            it.userEntity.userName
                                        )
                                    }
                                    .map {
                                        ChatUser(
                                            nickName = it.userEntity.userName,
                                            profileUrl = BuildConfig.PROFILE_IMAGE_SERVER_URL + it.userEntity.profilePicUrl,
                                            id = it.userEntity.userName
                                        )
                                    },
                                seenTime = "25 min ago",
                            )
                        }
                    }
            }
        }
    }

    @Singleton
    @Provides
    fun provideLoadChatRoomUseCase(chatRepository: ChatRepository): LoadChatRoomUseCase {
        return object : LoadChatRoomUseCase {
            override suspend fun invoke() {
                return chatRepository.loadChatRoom()
            }
        }
    }

    @Singleton
    @Provides
    fun provideGetChatUseCase(chatRepository: ChatRepository): GetChatUseCase {
        return object : GetChatUseCase {
            override fun invoke(roomId: Int): Flow<List<ChatUiState>> {
                return chatRepository.getContents(roomId).map { list ->
                    list.map { chatEntity ->
                        ChatUiState.Success()
                    }
                }
            }
        }
    }

    @Singleton
    @Provides
    fun provideGetUserUseCase(
        chatDao: ChatDao,
        loggedInUserDao: LoggedInUserDao,
    ): GetUserByRoomIdUseCase {
        return object : GetUserByRoomIdUseCase {
            override fun invoke(roomId: Int): Flow<List<ChatUser>> {
                return loggedInUserDao.getLoggedInUser()
                    .combine(chatDao.getParticipantsWithUsers(roomId)) { loggedInUser, list ->
                        list
                            .filter {
                                !TextUtils.equals(
                                    loggedInUser?.userName,
                                    it.userEntity.userName
                                )
                            }
                            .map {
                                ChatUser(
                                    nickName = it.userEntity.userName,
                                    profileUrl = BuildConfig.PROFILE_IMAGE_SERVER_URL + it.userEntity.profilePicUrl,
                                    id = it.userEntity.userName
                                )
                            }
                    }
            }
        }
    }
}