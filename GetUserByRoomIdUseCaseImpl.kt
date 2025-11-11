package com.sarang.torang.di.chat_di

import android.text.TextUtils
import com.sarang.torang.BuildConfig
import com.sarang.torang.data.ChatUser
import com.sarang.torang.repository.ChatRepository
import com.sarang.torang.repository.LoginRepository
import com.sarang.torang.usecase.GetUsersByRoomIdUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class GetUserByRoomIdUseCaseImpl {
    @Singleton
    @Provides
    fun provideGetUserUseCase(
        chatRepository: ChatRepository,
        loginRepository: LoginRepository,
    ): GetUsersByRoomIdUseCase {
        return object : GetUsersByRoomIdUseCase {
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
}