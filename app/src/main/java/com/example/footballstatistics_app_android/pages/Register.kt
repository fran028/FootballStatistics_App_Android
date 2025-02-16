package com.example.footballstatistics_app_android.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.footballstatistics_app_android.R
import com.example.footballstatistics_app_android.Theme.LeagueGothic
import com.example.footballstatistics_app_android.Theme.RobotoCondensed
import com.example.footballstatistics_app_android.Theme.black
import com.example.footballstatistics_app_android.Theme.blue
import com.example.footballstatistics_app_android.Theme.green
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.Theme.yellow
import com.example.footballstatistics_app_android.components.ButtonObject

@Composable
fun RegisterPage(navController: NavController, updateSelectedItemIndex: (Int) -> Unit){
    var username by remember { mutableStateOf("Username") }
    var password by remember { mutableStateOf("Password") }
    var email by remember { mutableStateOf("Email") }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.register_img), // Replace with your image
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize() ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.size(150.dp))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(124.dp)
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = "FOOTBALL STATISTICS",
                style = TextStyle(
                    fontFamily = LeagueGothic,
                    fontSize = 60.sp,
                    color = white
                )
            )
            Spacer(modifier = Modifier.size(30.dp))
            TextField(
                value = username,
                onValueChange = { username = it },
                //label = { Text("Username") },
                modifier = Modifier
                    .width(350.dp)
                    .height(50.dp)
                    .padding(horizontal = 0.dp),
                shape = RoundedCornerShape(8.dp),
                textStyle = TextStyle(
                    fontFamily = RobotoCondensed,
                    fontSize = 18.sp,
                    color = black
                )
            )
            Spacer(modifier = Modifier.size(20.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                //label = { Text("Password") },
                modifier = Modifier
                    .width(350.dp)
                    .height(50.dp)
                    .padding(horizontal = 0.dp),
                shape = RoundedCornerShape(8.dp),
                textStyle = TextStyle(
                    fontFamily = RobotoCondensed,
                    fontSize = 18.sp,
                    color = black
                )

            )
            Spacer(modifier = Modifier.size(20.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                //label = { Text("Password") },
                modifier = Modifier
                    .width(350.dp)
                    .height(50.dp)
                    .padding(horizontal = 0.dp),
                shape = RoundedCornerShape(8.dp),
                textStyle = TextStyle(
                    fontFamily = RobotoCondensed,
                    fontSize = 18.sp,
                    color = black
                )

            )
            Spacer(modifier = Modifier.size(40.dp))
            ButtonObject(
                text = "REGISTER",
                onClick = { updateSelectedItemIndex(5) },
                bgcolor = blue,
                width = 360.dp,
                height = 60.dp,
                textcolor = white,
            )
            Spacer(modifier = Modifier.size(10.dp))
            ButtonObject(
                text = "BACK TO LOGIN",
                onClick = { updateSelectedItemIndex(5) },
                bgcolor = yellow,
                width = 360.dp,
                height = 60.dp,
                textcolor = black,
            )
        }
    }
}