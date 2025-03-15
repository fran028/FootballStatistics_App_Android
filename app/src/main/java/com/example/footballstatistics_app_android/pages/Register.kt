package com.example.footballstatistics_app_android.pages

import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.Theme.yellow
import com.example.footballstatistics_app_android.components.ButtonObject
import com.example.footballstatistics_app_android.data.AppDatabase
import com.example.footballstatistics_app_android.data.User
import com.example.footballstatistics_app_android.data.UserRepository
import com.example.footballstatistics_app_android.viewmodel.UserViewModel
import com.example.footballstatistics_app_android.viewmodel.UserViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.isNotBlank

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(navController: NavController){
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var heightString by remember { mutableStateOf("") }
    var weightString by remember { mutableStateOf("") }

     val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis() // Set the initial date to today
    )
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var formattedDate by remember { mutableStateOf("") }

    val borderColor by remember { mutableStateOf(white) }

    val context = LocalContext.current
    val appDatabase = AppDatabase.getDatabase(context)
    val userRepository = UserRepository(appDatabase.userDao())
    val viewModelFactory = UserViewModelFactory(userRepository)
    val userViewModel: UserViewModel = viewModel(factory = viewModelFactory)
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier
        .fillMaxSize() ) {
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
            Spacer(modifier = Modifier.size(100.dp))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(100.dp)
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
                    unfocusedContainerColor = white,
                    focusedContainerColor = white,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    cursorColor = black
                )
            )
            Spacer(modifier = Modifier.size(20.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
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
            Spacer(modifier = Modifier.size(20.dp))
            TextField(
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = {
                    Text(
                        text = "Full Name",
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
            Row (
                modifier = Modifier.width(350.dp)
                    .height(50.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = formattedDate,
                    onValueChange = { formattedDate = it },
                    readOnly = true,
                    /*prefix = {
                        Text(
                            text = "Date",
                            fontFamily = RobotoCondensed,
                            style = TextStyle(
                                color = black,
                                fontSize = 18.sp,
                                fontFamily = RobotoCondensed
                            )
                        )
                    },*/
                    placeholder ={
                        Text(
                            text = "Date",
                            style = TextStyle(
                                color = black,
                                fontSize = 16.sp,
                                fontFamily = RobotoCondensed
                            )
                        )
                    },

                    modifier = Modifier
                        .width(260.dp)
                        .fillMaxHeight()
                        .padding(horizontal = 0.dp),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = TextStyle(
                        fontFamily = RobotoCondensed,
                        fontSize = 18.sp,
                        color = black,
                        textAlign = TextAlign.Right
                    ),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = white, // Background when not focused
                        focusedContainerColor = white, // Background when focused
                        unfocusedIndicatorColor = Color.Transparent, // Remove the underline when not focused
                        focusedIndicatorColor = Color.Transparent // Remove the underline when focused
                    )
                )
                Spacer(modifier = Modifier.size(10.dp))
                Button(
                    onClick = { showDatePickerDialog = true },
                    modifier = Modifier
                        .fillMaxSize().padding(0.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blue,
                        contentColor = black
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 2.dp
                    ),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.calendar),
                        contentDescription = "Calendar Icon",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            if (showDatePickerDialog) {
                DatePickerDialog(
                    onDismissRequest = { showDatePickerDialog = false },
                    confirmButton = {
                        Button(onClick = {
                            showDatePickerDialog = false
                            val selectedDateMillis = datePickerState.selectedDateMillis
                            if (selectedDateMillis != null) {
                                val date = Date(selectedDateMillis)
                                val formattedDatestring = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
                                formattedDate = formattedDatestring
                            }
                        }) {
                            Text("Select Date")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }
            Spacer(modifier = Modifier.size(20.dp))
            TextField(
                value = heightString,
                onValueChange = { heightString = it },
                /*prefix = {
                    Text(
                        text = "Heigth (optional)",
                        fontFamily = RobotoCondensed,
                        style = TextStyle(
                            color = black,
                            fontSize = 18.sp,
                            fontFamily = RobotoCondensed
                        )
                    )
                },
                suffix = {
                    Text(
                        text = "Cm",
                        fontFamily = RobotoCondensed,
                        style = TextStyle(
                            color = black,
                            fontSize = 16.sp,
                            fontFamily = RobotoCondensed
                        ),
                    )
                },*/
                placeholder ={
                    Text(
                        text = "Height (Cm)",
                        style = TextStyle(
                            color = black,
                            fontSize = 16.sp,
                            fontFamily = RobotoCondensed
                        )
                    )
                },
                modifier = Modifier
                    .width(350.dp)
                    .heightIn(min = 50.dp, max = 50.dp),
                shape = RoundedCornerShape(8.dp),
                textStyle = TextStyle(
                    fontFamily = RobotoCondensed,
                    fontSize = 18.sp,
                    color = black,
                    //textAlign = TextAlign.Right,
                ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = white, // Background when not focused
                    focusedContainerColor = white, // Background when focused
                    unfocusedIndicatorColor = Color.Transparent, // Remove the underline when not focused
                    focusedIndicatorColor = Color.Transparent // Remove the underline when focused
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )

            Spacer(modifier = Modifier.size(20.dp))
            TextField(
                value = weightString,
                onValueChange = { weightString = it },
                /*prefix = {
                    Text(
                        text = "Weight (optional)",
                        fontFamily = RobotoCondensed,
                        style = TextStyle(
                            color = black,
                            fontSize = 18.sp,
                            fontFamily = RobotoCondensed
                        )
                    )
                },
                suffix = {
                    Text(
                        text = "Kg",
                        fontFamily = RobotoCondensed,
                        style = TextStyle(
                            color = black,
                            fontSize = 16.sp,
                            fontFamily = RobotoCondensed
                        )
                    )
                },*/
                placeholder ={
                    Text(
                        text = "Weight (Kg)",
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
                    .padding(horizontal = 0.dp),
                shape = RoundedCornerShape(8.dp),
                textStyle = TextStyle(
                    fontFamily = RobotoCondensed,
                    fontSize = 18.sp,
                    color = black,
                    //textAlign = TextAlign.Right,
                ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = white, // Background when not focused
                    focusedContainerColor = white, // Background when focused
                    unfocusedIndicatorColor = Color.Transparent, // Remove the underline when not focused
                    focusedIndicatorColor = Color.Transparent // Remove the underline when focused
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )
            Spacer(modifier = Modifier.size(40.dp))
            ButtonObject(
                text = "SIGN UP",
                onClick = {

                    if (
                        username.isNotBlank()
                        && password.isNotBlank()
                        && fullName.isNotBlank()
                        && formattedDate.isNotBlank()) {

                        if(heightString.isBlank()){
                            heightString = "0"
                        }
                        if(weightString.isBlank()){
                            weightString = "0"
                        }

                        coroutineScope.launch {
                            val usernameExists = userViewModel.checkUsernameExists(username)
                            if (usernameExists) {
                                Toast.makeText(
                                    context,
                                    "Username already exists!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // Create a User object
                                val newUser = User(
                                    username = username,
                                    password = password,
                                    fullName = fullName,
                                    height = heightString.toInt(),
                                    weight = weightString.toInt(),
                                    date = formattedDate,
                                    id = "0"
                                )

                                // Insert the user into the database
                                userViewModel.insertUser(newUser)

                                // Show a success message
                                Toast.makeText(
                                    context,
                                    "User registered successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Optionally, navigate to another screen or clear the fields
                                // navController.navigate("some_other_screen")
                                username = ""
                                password = ""
                                fullName = ""
                                formattedDate = ""
                                heightString = ""
                                weightString = ""
                                navController.navigate(Screen.Login.route)
                            }
                        }
                    } else {
                        // Show an error message if any field is empty
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    }
                },
                bgcolor = blue,
                width = 360.dp,
                height = 60.dp,
                textcolor = white,
            )
            Spacer(modifier = Modifier.size(10.dp))
            ButtonObject(
                text = "BACK TO LOGIN",
                onClick = { navController.navigate(Screen.Login.route)  },
                bgcolor = yellow,
                width = 360.dp,
                height = 60.dp,
                textcolor = black,
            )
        }
    }
}