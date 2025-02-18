package com.example.footballstatistics_app_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.footballstatistics_app_android.pages.CalendarPage
import com.example.footballstatistics_app_android.pages.HomePage
import com.example.footballstatistics_app_android.pages.MatchPage
import com.example.footballstatistics_app_android.pages.ProfilePage
import com.example.footballstatistics_app_android.pages.SettingPage
import com.example.footballstatistics_app_android.ui.theme.FootballStatistics_App_AndroidTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import com.example.footballstatistics_app_android.Screen
import com.example.footballstatistics_app_android.Theme.blue
import com.example.footballstatistics_app_android.Theme.green
import com.example.footballstatistics_app_android.Theme.red
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.Theme.yellow
import com.example.footballstatistics_app_android.components.ViewTitle
import com.example.footballstatistics_app_android.pages.LoginPage
import com.example.footballstatistics_app_android.pages.RegisterPage
import com.google.accompanist.systemuicontroller.rememberSystemUiController

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val indicatorColor: Color,
    val route: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
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
            }
        }
    }
}

@Composable
fun MainScreen(){
    val navController = rememberNavController()
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(5)
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    // Function to update selectedItemIndex
    val updateSelectedItemIndex: (Int) -> Unit = { newIndex ->
        selectedItemIndex = newIndex
    }

    val items = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            indicatorColor = yellow,
            hasNews = false,
            route = Screen.Home.route
        ),
        BottomNavigationItem(
            title = "Calendar",
            selectedIcon = Icons.Filled.DateRange,
            unselectedIcon = Icons.Outlined.DateRange,
            indicatorColor = blue,
            hasNews = false,
            route = Screen.Calendar.route
        ),
        BottomNavigationItem(
            title = "Profile",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            indicatorColor = green,
            hasNews = false,
            route = Screen.Profile.route
        ),
        BottomNavigationItem(
            title = "Setting",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            indicatorColor = red,
            hasNews = false,
            route = Screen.Setting.route
        )
    )



    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            if (currentDestination?.route != Screen.Login.route && currentDestination?.route != Screen.Register.route) {
                NavigationBar(containerColor = white) {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            //selected = selectedItemIndex == index,
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
                                        imageVector = if (currentDestination?.hierarchy?.any { it.route == item.route } == true) {
                                            item.selectedIcon
                                        } else item.unselectedIcon,
                                        contentDescription = item.title
                                    )
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent, // Remove the default indicator
                            ),
                            modifier = Modifier
                                .padding(horizontal = 28.dp)
                                .drawBehind {
                                    if (currentDestination?.hierarchy?.any { it.route == item.route } == true) {
                                        drawRoundRect(
                                            color = item.indicatorColor,
                                            cornerRadius = CornerRadius(
                                                8.dp.toPx()
                                            ),
                                            size = Size(
                                                width = this.size.width,
                                                height = 60.dp.toPx()
                                            ),
                                            topLeft = Offset(
                                                x = 0f,
                                                y = (this.size.height - 60.dp.toPx()) / 2
                                            )
                                        )
                                    }
                                }
                        )
                    }

                }

            }
        }
    )
    { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier
                .fillMaxSize(),
        ) {
            composable(Screen.Login.route) {
                LoginPage(
                    navController = navController,
                    updateSelectedItemIndex = updateSelectedItemIndex
                )
            }
            composable(Screen.Register.route) {
                RegisterPage(
                    navController = navController,
                    updateSelectedItemIndex = updateSelectedItemIndex
                )
            }
            composable(Screen.Home.route) {
                HomePage(
                    navController = navController,
                    updateSelectedItemIndex = updateSelectedItemIndex,
                    modifier = Modifier
                )
            }
            composable(Screen.Match.route) {
                MatchPage(
                    navController = navController,
                    updateSelectedItemIndex = updateSelectedItemIndex,
                    modifier = Modifier
                )
            }
            composable(Screen.Setting.route) {
                SettingPage(
                    navController = navController,
                    updateSelectedItemIndex = updateSelectedItemIndex,
                    modifier = Modifier
                )
            }
            composable(Screen.Calendar.route) {
                CalendarPage(
                    navController = navController,
                    updateSelectedItemIndex = updateSelectedItemIndex,
                    modifier = Modifier
                )
            }
            composable(Screen.Profile.route) {
                ProfilePage(
                    navController = navController,
                    updateSelectedItemIndex = updateSelectedItemIndex,
                    modifier = Modifier
                )
            }
        }
        //ContentScreen(modifier = Modifier.padding(innerPadding), selectedItemIndex, updateSelectedItemIndex = updateSelectedItemIndex, navController)
    }
}


//@Composable
//fun ContentScreen(modifier: Modifier = Modifier, selectedIndex : Int, updateSelectedItemIndex: (Int) -> Unit, navController: NavController) {
//    when(selectedIndex){
//        0 -> HomePage(navController = navController, updateSelectedItemIndex = updateSelectedItemIndex)
//        1 -> CalendarPage(navController = navController, updateSelectedItemIndex = updateSelectedItemIndex)
//        2 -> ProfilePage(navController = navController, updateSelectedItemIndex = updateSelectedItemIndex)
//        3 -> SettingPage(navController = navController, updateSelectedItemIndex = updateSelectedItemIndex)
//        4 -> MatchPage(navController = navController, updateSelectedItemIndex = updateSelectedItemIndex)
//        5 -> LoginPage(navController= navController, updateSelectedItemIndex = updateSelectedItemIndex)
//        6 -> RegisterPage(navController= navController, updateSelectedItemIndex = updateSelectedItemIndex)
//    }
//}

//@Serializable
//object Home
//
//@Serializable
//object Calendar
//
//@Serializable
//object Profile
//
//@Serializable
//object Setting
//
//@Serializable
//data class match(
//    val matchid: Int,
//)

