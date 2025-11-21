package com.example.cinematch

import android.widget.Toast
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ProfileScreen(navController: NavController, viewModel: AuthenticationViewModel) {
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    var actionCardState by rememberSaveable { mutableStateOf(false) }
    val friends by viewModel.friends

    LaunchedEffect(Unit) {
        delay(100)
        viewModel.loadFriends()
    }

    Box(Modifier.fillMaxSize()) {
        // 🌄 Background image
        Image(
            painter = painterResource(R.drawable.get_started),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 🖤 Semi-transparent overlay
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xCC000000))
        ) {
            // 🎨 Curved beige background shape
            Canvas(Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                val path = Path().apply {
                    moveTo(0f, height)
                    lineTo(0f, height / 3)
                    quadraticTo(width / 2, (height / 2.2).toFloat(), width, height / 3)
                    lineTo(width, height)
                    close()
                }
                drawPath(path = path, color = Color(0xFFFAF0E6), style = Fill)
            }

            // 🌟 Content Box
            Box(Modifier.fillMaxSize()) {
                // 🔙 Back button
                IconButton(
                    onClick = { navController.navigate(Screens.Home.route) },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .background(Color(0x66000000), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // 👇 Main centered content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(Modifier.height(150.dp))

                    // 🧑‍💼 Profile Title
                    Text(
                        text = "Profile",
                        style = TextStyle(
                            fontWeight = FontWeight.ExtraBold,
                            fontStyle = FontStyle.Italic
                        ),
                        color = Color.White,
                        fontSize = 56.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(40.dp))

                    // 👤 Profile Info Fields
                    GeneralInfo(viewModel)

                    Spacer(Modifier.height(30.dp))

                    // 🧩 Friend Zone Section
                    friendZone(viewModel, navController , friends)

                    Spacer(Modifier.height(40.dp))

                    // 🚪 Sign Out Button
                    Button(
                        onClick = {
                            viewModel.signOut()
                            navController.navigate(Screens.GettingStarted.route) {
                                popUpTo(0)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Text(
                            text = "Sign Out",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(Modifier.height(100.dp))
                }
            }
        }
    }
}



@Composable
fun GeneralInfo(viewModel: AuthenticationViewModel) {

    val instance = viewModel.instance
    val currentuser = instance.currentUser
    Column(Modifier.wrapContentHeight().fillMaxWidth(0.9f)) {
        OutlinedTextField(
            value = currentuser?.email ?: "",
            onValueChange = {},
            shape = RoundedCornerShape(10.dp),
            readOnly = true,
            label = { Text("Email") },
            leadingIcon = {
                Icon(
                    Icons.Default.Email,
                    contentDescription = "Email",
                    tint = Color.Black
                )
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = currentuser?.uid ?: "",
            onValueChange = {},
            shape = RoundedCornerShape(10.dp),
            readOnly = true,
            label = { Text("UserId") },
            leadingIcon = {
                Icon(
                    Icons.Default.AddLink,
                    contentDescription = "Email",
                    tint = Color.Black
                )
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = currentuser?.displayName ?: "",
            onValueChange = {},
            shape = RoundedCornerShape(10.dp),
            readOnly = true,
            label = { Text("Display Name") },
            leadingIcon = {
                Icon(
                    Icons.Default.Adjust,
                    contentDescription = "Name",
                    tint = Color.Black
                )
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )


    }

}

@Composable
fun friendZone(
    viewModel: AuthenticationViewModel,
    navController: NavController,
    friends: List<String>
) {
    val context = LocalContext.current
    var friendEmail by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadFriends()
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 10.dp)) {
        Spacer(Modifier.height(15.dp))
        Text(
            "Friends",
            style = TextStyle(fontStyle = FontStyle.Normal, fontWeight = FontWeight.Bold, fontSize = 20.sp),
            color = Color.Black
        )

        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = friendEmail,
            onValueChange = { friendEmail = it },
            shape = RoundedCornerShape(10.dp),
            label = { Text("Enter Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.Black) },
            trailingIcon = {
                IconButton(onClick = {
                    scope.launch {
                        val uid = viewModel.findinguserByEmail(friendEmail, context)
                        if (uid != null) {
                            navController.navigate("${Screens.ActionCardforfriends.route}/$uid")
                        } else {
                            Toast.makeText(context, "Email not registered", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Add Friend")
                }
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(Modifier.height(10.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(friends) { friend ->
                friendcard(friend)
            }
        }
    }
}



@Composable
fun friendcard(friend: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F8FF)),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(friend, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp))
            IconButton(onClick = { /* TODO: delete friend */ }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Friend", tint = Color.Black)
            }
        }
    }
}




