package com.example.footballstatistics_app_android.pages


import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.footballstatistics_app_android.R
import com.example.footballstatistics_app_android.Screen
import com.example.footballstatistics_app_android.Theme.LeagueGothic
import com.example.footballstatistics_app_android.Theme.RobotoCondensed
import com.example.footballstatistics_app_android.Theme.black
import com.example.footballstatistics_app_android.Theme.blue
import com.example.footballstatistics_app_android.Theme.green
import com.example.footballstatistics_app_android.Theme.red
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.components.ButtonObject
import com.example.footballstatistics_app_android.data.AppDatabase
import com.example.footballstatistics_app_android.data.User
import com.example.footballstatistics_app_android.data.UserRepository
import com.example.footballstatistics_app_android.viewmodel.LoginResult
import com.example.footballstatistics_app_android.viewmodel.LoginViewModel
import com.example.footballstatistics_app_android.viewmodel.LoginViewModelFactory
import com.example.footballstatistics_app_android.viewmodel.UserViewModel
import com.example.footballstatistics_app_android.viewmodel.UserViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun LoginPage(navController: NavController) {

    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val userDao = database.userDao()
    val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(userDao))


    val userRepository = UserRepository(database.userDao())
    val viewModelFactory = UserViewModelFactory(userRepository)
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory)
    val coroutineScope = rememberCoroutineScope()

    val loginResult by loginViewModel.loginResult.collectAsState()

    var borderColor by remember { mutableStateOf(white) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.login_img), // Replace with your image
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.size(125.dp))
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
                placeholder = {
                    Text(
                        text = "Username",
                        style = TextStyle(
                            color = black,
                            fontSize = 16.sp,
                            fontFamily = RobotoCondensed
                        )
                    )
                },
                modifier = Modifier
                    .width(350.dp)
                    .height(50.dp)
                    .padding(horizontal = 0.dp)
                    .border(2.dp, borderColor, RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                textStyle = TextStyle(
                    fontFamily = RobotoCondensed,
                    fontSize = 18.sp,
                    color = black
                ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = white, // Background when not focused
                    focusedContainerColor = white, // Background when focused
                    unfocusedIndicatorColor = Color.Transparent, // Remove the underline when not focused
                    focusedIndicatorColor = Color.Transparent // Remove the underline when focused
                )
            )
            Spacer(modifier = Modifier.size(20.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                //label = { Text("Password") },
                placeholder = {
                    Text(
                        text = "Password",
                        style = TextStyle(
                            color = black,
                            fontSize = 16.sp,
                            fontFamily = RobotoCondensed
                        )
                    )
                },
                modifier = Modifier
                    .width(350.dp)
                    .height(50.dp)
                    .padding(horizontal = 0.dp)
                    .border(2.dp, borderColor, RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                textStyle = TextStyle(
                    fontFamily = RobotoCondensed,
                    fontSize = 18.sp,
                    color = black
                ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = white, // Background when not focused
                    focusedContainerColor = white, // Background when focused
                    unfocusedIndicatorColor = Color.Transparent, // Remove the underline when not focused
                    focusedIndicatorColor = Color.Transparent // Remove the underline when focused
                )


            )
            if(isError){
                Text(
                    text = "Invalid username or password",
                    color = Color.Red,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            Spacer(modifier = Modifier.size(40.dp))
            ButtonObject(
                text = "LOGIN",
                onClick = {
                            coroutineScope.launch {
                                loginViewModel.login(username, password)
                            }
                          },
                bgcolor = green,
                width = 360.dp,
                height = 60.dp,
                textcolor = white,
            )
            Spacer(modifier = Modifier.size(10.dp))
            ButtonObject(
                text = "REGISTER",
                onClick = {
                    navController.navigate(Screen.Register.route)
                },
                bgcolor = blue,
                width = 360.dp,
                height = 60.dp,
                textcolor = white,
            )
        }
        when (loginResult) {
            is LoginResult.Initial -> {}
            is LoginResult.Success -> {
                borderColor = white
                isError = false
                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        val user: User? = userViewModel.getUserByUsername(username)
                        if (user != null) {
                            userViewModel.updateLoginStatus(user.id)
                        }
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
            }

            is LoginResult.Error -> {
               // Toast.makeText(context, loginResult.message, Toast.LENGTH_SHORT).show()
                borderColor = red
                isError = true
            }
        }
    }
}