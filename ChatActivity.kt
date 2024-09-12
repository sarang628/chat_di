package com.sarang.torang.di.chat_di

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sarang.torang.compose.chat.ChatScreen
import com.sarang.torang.di.image.provideTorangAsyncImage

class ChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatScreen(onBack = {
                finish()
            }, image = provideTorangAsyncImage())
        }
    }
}


