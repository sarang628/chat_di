package com.sarang.torang.di.chat_di

import com.sarang.torang.BuildConfig
import com.sarang.torang.compose.chatroom.ChatRoomUiState
import com.sarang.torang.data.ChatRoom
import com.sarang.torang.data.ChatUser
import com.sarang.torang.data.User
import com.sarang.torang.repository.ChatRepository
import com.sarang.torang.repository.LoginRepository
import com.sarang.torang.usecase.GetChatRoomUseCase
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
class GetChatRoomUseCaseImpl {
    @Singleton
    @Provides
    fun provideGetChatRoomUseCase(
        chatRepository: ChatRepository,
        loginRepository: LoginRepository,
    ): GetChatRoomUseCase {
        return object : GetChatRoomUseCase {
            val userNameFlow = loginRepository.loginUser
            val uiStateFlow = chatRepository.getAllChatRoomsFlow().map {
                chatRooms -> chatRooms.map { chatRoom ->
                    chatRoom.uiState
                }
            }

            val uiStateWithoutMe = uiStateFlow.combine(userNameFlow) { list, userName ->
                list.map { uiState ->
                    uiState.copy(
                        list = uiState.list.filter {
                            it.nickName != userName?.chatUser?.nickName
                        }
                    )
                }
            }


            override fun invoke(): Flow<List<ChatRoomUiState>> {
                return uiStateWithoutMe
            }
        }
    }
}

val ChatRoom.uiState : ChatRoomUiState get() = ChatRoomUiState(
        id = roomId,
        list = chatParticipants.map { it.chatUser },
        seenTime = createDate
    )

val User.chatUser : ChatUser get() = ChatUser(
    nickName = userName,
    profileUrl = BuildConfig.PROFILE_IMAGE_SERVER_URL + profilePicUrl,
    id = userName
)