package com.sarang.torang.di.chat_di

import android.text.TextUtils
import com.sarang.torang.BuildConfig
import com.sarang.torang.compose.chatroom.ChatRoomUiState
import com.sarang.torang.data.Chat
import com.sarang.torang.data.ChatUser
import com.sarang.torang.data.dao.ChatDao
import com.sarang.torang.data.dao.LoggedInUserDao
import com.sarang.torang.data.entity.ChatEntityWithUser
import com.sarang.torang.data.entity.ChatRoomWithParticipantsEntity
import com.sarang.torang.repository.ChatRepository
import com.sarang.torang.usecase.GetChatRoomUseCase
import com.sarang.torang.usecase.GetChatUseCase
import com.sarang.torang.usecase.GetUserByRoomIdUseCase
import com.sarang.torang.usecase.GetUserOrCreateRoomByUserIdUseCase
import com.sarang.torang.usecase.IsSignInUseCase
import com.sarang.torang.usecase.LoadChatRoomUseCase
import com.sarang.torang.usecase.LoadChatUseCase
import com.sarang.torang.usecase.SendChatUseCase
import com.sarang.torang.usecase.SetSocketCloseUseCase
import com.sarang.torang.usecase.SetSocketListenerUseCase
import com.sarang.torang.usecase.SubScribeRoomUseCase
import com.sarang.torang.usecase.WebSocketListener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import okhttp3.Response
import okhttp3.WebSocket
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
                        if (loggedInUser == null) {
                            mutableListOf()
                        } else {
                            list.map { chatRoomEntity ->
                                ChatRoomUiState(
                                    chatRoomEntity.chatRoomEntity.roomId,
                                    list = chatRoomEntity.participantsWithUsers
                                        .filter {
                                            !TextUtils.equals(
                                                loggedInUser.userName,
                                                it.userName
                                            )
                                        }
                                        .map {
                                            ChatUser(
                                                nickName = it.userName,
                                                profileUrl = BuildConfig.PROFILE_IMAGE_SERVER_URL + it.profilePicUrl,
                                                id = it.userName
                                            )
                                        },
                                    seenTime = "25 min ago",
                                )
                            }
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
    fun provideGetChatUseCase(
        chatRepository: ChatRepository,
        loggedInUserDao: LoggedInUserDao,
    ): GetChatUseCase {
        return object : GetChatUseCase {
            override fun invoke(roomId: Int): Flow<List<Chat>> {
                return chatRepository.getContents(roomId)
                    .combine(loggedInUserDao.getLoggedInUser()) { list, loggedInUser ->
                        list.map { chatEntity ->
                            chatEntity.toChat(chatEntity.userEntity.userId == loggedInUser?.userId)
                        }
                    }
            }
        }
    }

    private fun ChatEntityWithUser.toChat(isMe: Boolean): Chat {
        return Chat(
            userId = this.chatEntity.userId,
            message = this.chatEntity.message,
            createDate = this.chatEntity.createDate,
            profileUrl = BuildConfig.PROFILE_IMAGE_SERVER_URL + this.userEntity.profilePicUrl,
            userName = this.userEntity.userName,
            isMe = isMe,
            isSending = this.chatEntity.sending
        )
    }

    @Singleton
    @Provides
    fun provideGetUserUseCase(
        chatDao: ChatDao,
        loggedInUserDao: LoggedInUserDao,
    ): GetUserByRoomIdUseCase {
        return object : GetUserByRoomIdUseCase {
            override fun invoke(roomId: Int): Flow<List<ChatUser>?> {
                return loggedInUserDao.getLoggedInUser()
                    .combine(chatDao.getParticipantsWithUsersFlow(roomId)) { loggedInUser, list ->
                        list?.filter {
                            !TextUtils.equals(
                                loggedInUser?.userName,
                                it.userEntity.userName
                            )
                        }
                            ?.map {
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

    @Singleton
    @Provides
    fun provideGetUserOrCreateRoomByUserIdUseCase(
        chatDao: ChatDao,
        loggedInUserDao: LoggedInUserDao,
        chatRepository: ChatRepository,
    ): GetUserOrCreateRoomByUserIdUseCase {
        return object : GetUserOrCreateRoomByUserIdUseCase {
            override suspend fun invoke(userId: Int): Int {

                //로컬 DB에 1:1 채팅방 있는지 확인
                var chatUser: ChatRoomWithParticipantsEntity? = chatDao.getChatRoomByUserId(userId)

                //없다면 서버에 채팅방 생성 요청
                if (chatUser == null) {
                    chatRepository.getUserOrCreateRoomByUserId(userId)
                    chatUser = chatDao.getChatRoomByUserId(userId)
                }

                return chatUser?.chatRoomEntity?.roomId ?: throw Exception("채팅방 생성에 실패하였습니다.")
            }
        }
    }

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

    @Singleton
    @Provides
    fun provideLoadChatUseCase(
        chatRepository: ChatRepository,
    ): LoadChatUseCase {
        return object : LoadChatUseCase {
            override suspend fun invoke(roomId: Int) {
                chatRepository.loadContents(roomId)
            }
        }
    }

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

    @Singleton
    @Provides
    fun provideSetSocketListenerUseCase(
        chatRepository: ChatRepository,
    ): SetSocketListenerUseCase {
        return object : SetSocketListenerUseCase {
            override fun invoke(webSocketListener: WebSocketListener) {

                chatRepository.setListener(object : okhttp3.WebSocketListener() {
                    override fun onOpen(webSocket: WebSocket, response: Response) {
                        super.onOpen(webSocket, response)
                        webSocketListener.onOpen()
                    }

                    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                        super.onClosed(webSocket, code, reason)
                        webSocketListener.onClosed()
                    }
                })

                chatRepository.connectSocket()
            }
        }
    }

    @Singleton
    @Provides
    fun provideSetSocketCloseUseCase(
        chatRepository: ChatRepository,
    ): SetSocketCloseUseCase {
        return object : SetSocketCloseUseCase {
            override fun invoke() {
                chatRepository.closeConnection()
            }
        }
    }

    @Singleton
    @Provides
    fun provideSubScribeRoomUseCase(
        chatRepository: ChatRepository,
    ): SubScribeRoomUseCase {
        return object : SubScribeRoomUseCase {
            override suspend fun invoke(roomId: Int) {
                chatRepository.openChatRoom(roomId).collect {

                }
            }
        }
    }
}