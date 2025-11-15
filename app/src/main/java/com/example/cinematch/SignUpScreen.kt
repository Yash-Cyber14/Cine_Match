package com.example.cinematch

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    navcontroller: NavHostController,
    viewModel: AuthenticationViewModel
) {
    val signupState by viewModel.signupState

    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var name by rememberSaveable { mutableStateOf("")}

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.get_started),
            contentDescription = "Get Started",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(Modifier.fillMaxSize().background(Color(0x80000000))) {

            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(150.dp))
                Text(
                    "Sign Up",
                    fontSize = 50.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(Modifier.height(90.dp))

                Card(
                    Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF0E6)),
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
                                    contentDescription = null,
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
                            value = name,
                            onValueChange = {name = it},
                            shape = RoundedCornerShape(10.dp),
                            label = { Text("Name") },
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
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = Color.Black
                                )
                            },
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

                        Card(
                            modifier = Modifier
                                .width(250.dp)
                                .height(50.dp)
                                .clickable {
                                    scope.launch {
                                        viewModel.signUpWithEmailAndPassword(email, password)
                                        viewModel.savename(name , email = email)
                                    }
                                },
                            shape = RoundedCornerShape(15.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.get_started),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                Text(
                                    text = "Sign Up",
                                    color = Color.White,
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "If you already have an account ",
                                color = Color.Black,
                                fontSize = 12.sp
                            )
                            TextButton(onClick = {
                                navcontroller.navigate(Screens.SignIn.route)
                            }) {
                                Text("Sign In", color = Color.Black, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // ✅ Observe signup state outside UI
    LaunchedEffect(signupState) {
        when {
            signupState.confirm -> {
                Toast.makeText(context, "SignUp Successful", Toast.LENGTH_SHORT).show()
                navcontroller.navigate(Screens.Home.route) {
                    popUpTo(Screens.SignUp.route) { inclusive = true }
                }
            }

            signupState.errorMessage != null -> {
                Toast.makeText(context, signupState.errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
