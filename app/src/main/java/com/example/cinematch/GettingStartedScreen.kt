package com.example.cinematch

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Path
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun GettingStartedScreen(navcontroller: NavHostController) {



    Box(Modifier.fillMaxSize()) {
        Image(painter = painterResource(R.drawable.get_started) , contentDescription = "Get Started",
            modifier = Modifier.fillMaxSize() , contentScale = ContentScale.Crop)

        Box(Modifier.fillMaxSize().background(Color(0x80000000))) {

            Canvas(
                modifier = Modifier.fillMaxSize(),
                contentDescription = "TODO()"
            ) {
                val width :Float = size.width
                val height :Float = size.height
                val path1 = Path().apply {
                    moveTo(0f, height)
                    lineTo(0f, (height/1.9).toFloat())
                    quadraticTo(width/4 , (height/1.75).toFloat(), (width/2.2).toFloat(),height/2)
//                    lineTo((width/2.2).toFloat() , height)
                    quadraticTo((width/1.475).toFloat(), (height/2.5).toFloat(), width, ((height/2.7).toFloat()))
                    lineTo(width,height)
                    close()

                }

                drawPath(
                    path =path1,
                    color = Color(0xFFFAF0E6),
                    style = Fill,

                )
            }

            Box(Modifier.fillMaxSize()) {

                Column(Modifier.fillMaxSize().padding(15.dp)) {

                    Spacer(Modifier.height(200.dp))
                    Text(
                        "Welcome", style = TextStyle(
                            fontWeight = FontWeight.ExtraBold,
                            fontStyle = FontStyle.Italic
                        ), color = Color.White, fontSize = 70.sp
                    )
                    Spacer(Modifier.height(5.dp))
                    Text("Create an account and enjoy", fontSize = 15.sp, color = Color.White)
//                    Spacer(Modifier.height(2.dp))
                    Text("a seamless experience ", fontSize = 15.sp, color = Color.White)
                }
                Box(Modifier.fillMaxSize()) {

                    Column(Modifier.fillMaxSize() , horizontalAlignment = Alignment.CenterHorizontally) {

                        Spacer(Modifier.height(480.dp))
                        Card(Modifier.width(250.dp).height(50.dp)
                            .clickable{
                                navcontroller.navigate(Screens.SignUp.route)
                            },
                            shape = RoundedCornerShape(15.dp)
                            ) {
                            Box(contentAlignment = Alignment.Center) { // Use contentAlignment to center everything inside

                                // Layer 1: The Background Image
                                Image(
                                    painter = painterResource(id = R.drawable.get_started),
                                    contentDescription = "Background Image",
                                    modifier = Modifier.fillMaxSize(), // Image fills the Card
                                    contentScale = ContentScale.Crop    // Crop ensures it fills without distortion
                                )

                                // Layer 2: The Text, drawn on top of the Image
                                Text(
                                    text = "Sign Up",
                                    color = Color.White,
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }



                    }
                }
            }



        }
    }

}