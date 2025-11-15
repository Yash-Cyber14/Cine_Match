package com.example.cinematch


import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults



@Composable
fun SignInScreen(
    navcontroller: NavHostController,
    viewModel: AuthenticationViewModel
) {
    val signinState by viewModel.signinState // observe state properly
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        // Background
        Image(
            painter = painterResource(R.drawable.get_started),
            contentDescription = "Get Started",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Semi-transparent overlay
        Box(Modifier.fillMaxSize().background(Color(0x80000000))) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(150.dp))

                Text(
                    "Sign In",
                    fontSize = 50.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Spacer(Modifier.height(90.dp))

                Card(
                    Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF0E6))
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(60.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },

                            shape = RoundedCornerShape(10.dp),
                            label = { Text("Enter Email") },
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

                        Spacer(Modifier.height(15.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            shape = RoundedCornerShape(10.dp),
                            label = { Text("Enter Password") },
                            visualTransformation = PasswordVisualTransformation(),
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

                        Spacer(Modifier.height(60.dp))

                        // Sign In Button
                        Card(
                            modifier = Modifier
                                .width(250.dp)
                                .height(50.dp)
                                .clickable(enabled = !signinState.loading) {
                                    if (email.isNotBlank() && password.isNotBlank()) {
                                        scope.launch {
                                            viewModel.signInWithEmailAndPassword(email, password)
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Please enter email and password",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                            shape = RoundedCornerShape(15.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.get_started),
                                    contentDescription = "Background Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                if (signinState.loading) {
                                    CircularProgressIndicator(color = Color.White)
                                } else {
                                    Text(
                                        text = "Sign In",
                                        color = Color.White,
                                        fontSize = 25.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "If you don't have an account ",
                                color = Color.Black,
                                fontSize = 12.sp
                            )
                            TextButton(onClick = {
                                navcontroller.navigate(Screens.SignUp.route)
                            }) {
                                Text("Sign Up", color = Color.Black, fontSize = 12.sp)
                            }
                        }

                        // Error or success handling
                        signinState.errorMessage?.let {
                            Text("Error: $it", color = Color.Red, fontSize = 14.sp)
                        }

                        if (signinState.confirm) {
                            // ✅ Navigate after success
                            LaunchedEffect(signinState.confirm) {
                                navcontroller.navigate(Screens.Home.route) {
                                    popUpTo(Screens.SignIn.route) { inclusive = true }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
