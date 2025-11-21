package com.example.cinematch

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionCardforFriends(
    viewModel: AuthenticationViewModel,
    navController: NavController,
    friendUid: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(friendUid) {
        viewModel.findinguserByUid(friendUid, context)
    }

    val frienduser = viewModel.foundfriend.value

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
    ) {
        Image(
            painter = painterResource(R.drawable.get_started),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (frienduser == null) {
                CircularProgressIndicator(color = Color.White)
            } else {
                AnimatedVisibility(visible = true, enter = fadeIn(animationSpec = tween(600))) {
                    Card(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(0.9f),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.profile_placeholder),
                                contentDescription = "Profile",
                                modifier = Modifier.size(90.dp).clip(CircleShape)
                            )

                            Spacer(Modifier.height(12.dp))

                            frienduser.username?.let {
                                Text(it, style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                ))
                            }

                            Spacer(Modifier.height(8.dp))
                            Text("📧  ${frienduser.email}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                            Text("🆔  BIO : ${frienduser.bio}", style = MaterialTheme.typography.bodyMedium)

                            Spacer(Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                OutlinedButton(
                                    onClick = { navController.popBackStack() },
                                    shape = RoundedCornerShape(12.dp)
                                ) { Text("Cancel") }

                                Button(
                                    onClick = {
                                        scope.launch {
                                            viewModel.Makingfriend(friendUid, context)
                                            viewModel.loadCommonMoviesWithFriends()
                                            delay(100)
                                            navController.popBackStack()
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) { Text("Confirm") }
                            }
                        }
                    }
                }
            }
        }
    }
}

