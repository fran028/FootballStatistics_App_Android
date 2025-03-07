package com.example.footballstatistics_app_android

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.footballstatistics_app_android.pages.CalendarPage
import com.example.footballstatistics_app_android.pages.HomePage
import com.example.footballstatistics_app_android.pages.MatchPage
import com.example.footballstatistics_app_android.pages.ProfilePage
import com.example.footballstatistics_app_android.ui.theme.FootballStatistics_App_AndroidTheme
import com.example.footballstatistics_app_android.Theme.green
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.Theme.yellow
import com.example.footballstatistics_app_android.Theme.blue
import com.example.footballstatistics_app_android.pages.LoginPage
import com.example.footballstatistics_app_android.pages.RegisterPage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val hasNews: Boolean,
    val indicatorColor: Color,
    val route: String,
)

class MainActivity : ComponentActivity() {
    private var isBluetoothConnected = mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        //checkBluetoothPermissions()
        //DataTransferService.startService(this, ::onBluetoothConnected)

        setContent {
            FootballStatistics_App_AndroidTheme {
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = !isSystemInDarkTheme()

                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons
                    )
                    systemUiController.setNavigationBarColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons
                    )
                }
                MainScreen()
                BluetoothStatusIcon(isBluetoothConnected.value)
            }
        }
    }


    private fun checkBluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                1
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Bluetooth permissions granted")
            } else {
                Log.w("MainActivity", "Bluetooth permissions denied")
            }
        }
    }

    private fun onBluetoothConnected(isConnected: Boolean) {
        isBluetoothConnected.value = isConnected
    }
}

@Composable
fun BluetoothStatusIcon(isConnected: Boolean) {
    Row(
        modifier = Modifier
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isConnected) Icons.Filled.Bluetooth else Icons.Filled.BluetoothDisabled,
            contentDescription = "Bluetooth Status",
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Text(text = if (isConnected) "Bluetooth Connected" else "Bluetooth Disconnected")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(){
    val navController = rememberNavController()

    val items = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = R.drawable.home,
            unselectedIcon = R.drawable.home,
            indicatorColor = yellow,
            hasNews = false,
            route = Screen.Home.route
        ),
        BottomNavigationItem(
            title = "Calendar",
            selectedIcon = R.drawable.calendar,
            unselectedIcon = R.drawable.calendar,
            indicatorColor = blue,
            hasNews = false,
            route = Screen.Calendar.route
        ),
        BottomNavigationItem(
            title = "Profile",
            selectedIcon = R.drawable.shirt,
            unselectedIcon = R.drawable.shirt,
            indicatorColor = green,
            hasNews = false,
            route = Screen.Profile.route
        )
    )



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            if (currentDestination?.route != Screen.Login.route && currentDestination?.route != Screen.Register.route) {
                NavigationBar(containerColor = white) {
                    items.forEachIndexed { _, item ->
                        NavigationBarItem(
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (item.hasNews) {
                                            Badge()
                                        }
                                    }
                                ) {
                                    Icon(
                                        painter = if (currentDestination?.hierarchy?.any { it.route == item.route } == true) {
                                            painterResource(id = item.selectedIcon)
                                        } else painterResource(id = item.unselectedIcon),
                                        contentDescription = item.title,
                                        modifier = Modifier.size(25.dp)
                                    )
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent, // Remove the default indicator
                                selectedIconColor = Color.Black,
                                unselectedIconColor = Color.Black
                            ),
                            modifier = Modifier
                                .padding(horizontal = 15.dp)
                                .drawBehind {
                                    if (currentDestination?.hierarchy?.any { it.route == item.route } == true) {
                                        drawRoundRect(
                                            color = item.indicatorColor,
                                            cornerRadius = CornerRadius(
                                                8.dp.toPx()
                                            ),
                                            size = Size(
                                                //width = this.size.width,
                                                width = 50.dp.toPx(),
                                                height = 50.dp.toPx()
                                            ),
                                            topLeft = Offset(
                                                x = (this.size.width - 50.dp.toPx()) / 2,
                                                y = (this.size.height - 50.dp.toPx()) / 2
                                            )
                                        )
                                    }
                                }
                        )
                    }

                }

            }
        },
        contentWindowInsets = WindowInsets(0,0,0,0)
    )
    { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            composable(Screen.Login.route) {
                LoginPage(
                    navController = navController
                )
            }
            composable(Screen.Register.route) {
                RegisterPage(
                    navController = navController
                )
            }
            composable(Screen.Home.route) {
                HomePage(
                    navController = navController,
                    modifier = Modifier
                )
            }
            composable(Screen.Match.route) {
                MatchPage(
                    navController = navController,
                    modifier = Modifier,
                    match_id = 0.toString()
                )
            }
            composable(Screen.Calendar.route) {
                CalendarPage(
                    navController = navController,
                    modifier = Modifier
                )
            }
            composable(
                Screen.Profile.route,
                /*arguments = listOf(
                    navArgument("userId") {
                        type = NavType.StringType
                    }
                )*/
            ){
                    /*entry ->
                val userId = entry.arguments?.getString("userId") ?: ""*/
                ProfilePage(
                    navController = navController,
                    modifier = Modifier
                )
            }
        }
    }
}
