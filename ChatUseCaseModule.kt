package com.sarang.torang.di.chat_di

import android.text.TextUtils
import com.sarang.torang.BuildConfig
import com.sarang.torang.compose.chatroom.ChatRoomUiState
import com.sarang.torang.core.database.dao.LoggedInUserDao
import com.sarang.torang.data.Chat
import com.sarang.torang.data.ChatMessage
import com.sarang.torang.data.ChatRoom
import com.sarang.torang.data.ChatUser
import com.sarang.torang.repository.ChatRepository
import com.sarang.torang.repository.LoginRepository
import com.sarang.torang.usecase.GetChatRoomUseCase
import com.sarang.torang.usecase.GetChatUseCase
import com.sarang.torang.usecase.GetUserByRoomIdUseCase
import com.sarang.torang.usecase.GetUserOrCreateRoomByUserIdUseCase
import com.sarang.torang.usecase.IsSignInUseCase
import com.sarang.torang.usecase.LoadChatRoomUseCase
import com.sarang.torang.usecase.LoadChatUseCase
import com.sarang.torang.usecase.SendChatUseCase
import com.sarang.torang.usecase.SetSocketCloseUseCase
import com.sarang.torang.usecase.SubScribeRoomUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class ChatUseCaseModule {
    @Singleton
    @Provides
    fun provideGetChatRoomUseCase(
        chatRepository: ChatRepository,
        loginRepository: LoginRepository,
    ): GetChatRoomUseCase {
        return object : GetChatRoomUseCase {
            val isLoginFlow = loginRepository.isLogin
            val userNameFlow = loginRepository.getUserName()
            val chatRooms = chatRepository.getAllChatRoomsFlow()

            override fun invoke(): Flow<List<ChatRoomUiState>> {
                return combine(isLoginFlow, userNameFlow, chatRooms) { isLogin, userName, list ->
                        if (!isLogin) {
                            mutableListOf()
                        } else {
                            list.map { chatRoomEntity ->
                                ChatRoomUiState(
                                    chatRoomEntity.roomId,
                                    list = chatRoomEntity.chatParticipants
                                        .filter { !TextUtils.equals(userName, it.userName) }
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
                return chatRepository.refreshAllChatRooms()
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
                return chatRepository.getChatsFlow(roomId)
                    .combine(loggedInUserDao.getLoggedInUser()) { list, loggedInUser ->
                        list.map { chatEntity ->
                            chatEntity.toChat(chatEntity.userId == loggedInUser?.userId)
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

    @Singleton
    @Provides
    fun provideGetUserUseCase(
        chatRepository: ChatRepository,
        loginRepository: LoginRepository,
    ): GetUserByRoomIdUseCase {
        return object : GetUserByRoomIdUseCase {
            override fun invoke(roomId: Int): Flow<List<ChatUser>?> {
                val loginUser = loginRepository.loginUser
                val rooms = chatRepository.getAllChatRoomsFlow()
                return combine(loginUser, rooms) { loginUser, rooms ->
                    rooms.first { it.roomId == roomId }.chatParticipants.filter {
                        !TextUtils.equals(
                            loginUser?.userName,
                            it.userName
                        )
                    }.map {
                        ChatUser(
                            nickName = it.userName,
                            id = it.userId.toString(),
                            profileUrl = BuildConfig.PROFILE_IMAGE_SERVER_URL + it.profilePicUrl.toString()
                        )
                    }
                }
            }
        }
        }

    @Singleton
    @Provides
    fun provideGetUserOrCreateRoomByUserIdUseCase(
        chatRepository: ChatRepository,
    ): GetUserOrCreateRoomByUserIdUseCase {
        return object : GetUserOrCreateRoomByUserIdUseCase {
            override suspend fun invoke(userId: Int): Int {

                //로컬 DB에 1:1 채팅방 있는지 확인
                /*var chatUser: ChatRoomWithParticipantsEntity? = chatRepository.getChatRoomByUserId(userId)

                //없다면 서버에 채팅방 생성 요청
                if (chatUser == null) {
                    chatRepository.getUserOrCreateRoomByUserId(userId)
                    chatUser = chatDao.getChatRoomByUserId(userId)
                }

                return chatUser?.chatRoomEntity?.roomId ?: throw Exception("채팅방 생성에 실패하였습니다.")*/
                return 0
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
                //chatRepository.loadContents(roomId)
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
    fun provideSetSocketCloseUseCase(
        chatRepository: ChatRepository,
    ): SetSocketCloseUseCase {
        return object : SetSocketCloseUseCase {
            override fun invoke(roomId: Int) {
                chatRepository.unSubscribe(roomId)
            }
        }
    }

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