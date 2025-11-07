package com.sarang.torang.di.chat_di

import com.sarang.torang.repository.ChatRepository
import com.sarang.torang.usecase.GetUserOrCreateRoomByUserIdUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class GetUserOrCreateRoomByUserIdUseCaseImpl {
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
}