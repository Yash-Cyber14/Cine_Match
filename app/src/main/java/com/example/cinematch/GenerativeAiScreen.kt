package com.example.cinematch

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


@Composable
fun GenerativeAiScreen(
    aiViewModel: ChatViewModel,
    navController: NavHostController,
    mainViewModel: MainViewModel // if your bottombar needs it
) {
    Box(modifier = Modifier.fillMaxSize()) {

        // 🖼 Background image
        Image(
            painter = painterResource(R.drawable.ai_background),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 🌑 Dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xCC000000))
        )

        // 🧠 Main Chat Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // ================== CHAT MESSAGES ==================
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                items(aiViewModel.messages) { message ->
                    ChatBubble(message)
                }
            }

            if (aiViewModel.isLoading) {
                Text(
                    "AI is typing...",
                    color = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ================== INPUT BAR ==================
            var userInput by remember { mutableStateOf("") }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                TextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(55.dp),
                    placeholder = {
                        Text("Type your message...", color = Color.White.copy(alpha = 0.7f))
                    },
                    colors = TextFieldDefaults.colors(
                        cursorColor = Color(0xFF008B8B),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White.copy(alpha = 0.8f),
                        focusedPlaceholderColor = Color(0xFF008B8B),
                        unfocusedPlaceholderColor = Color(0xFF008B8B).copy(alpha = 0.7f),
                        focusedContainerColor = Color(0x33008B8B),
                        unfocusedContainerColor = Color(0x33008B8B),
                        focusedIndicatorColor = Color(0xFF008B8B),
                        unfocusedIndicatorColor = Color(0xFF008B8B),
                        disabledTextColor = Color.White.copy(alpha = 0.5f),
                        disabledIndicatorColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = false
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (userInput.isNotBlank()) {
                            aiViewModel.sendUserMessage(userInput)
                            userInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008B8B)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(55.dp)
                ) {
                    Text("Send", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ================== BOTTOM BAR ==================
            bottombar(navController, mainViewModel)
        }
    }
}


@Composable
fun ChatBubble(message: Message) {
    val isUser = message.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (isUser) Color(0xFF00796B) else Color(0xFF004D40),
                    RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(text = message.content, color = Color.White)
        }
    }
}
