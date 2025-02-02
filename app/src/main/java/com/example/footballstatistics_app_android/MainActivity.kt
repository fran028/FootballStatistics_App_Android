package com.example.footballstatistics_app_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.footballstatistics_app_android.pages.CalendarPage
import com.example.footballstatistics_app_android.pages.HomePage
import com.example.footballstatistics_app_android.pages.MatchPage
import com.example.footballstatistics_app_android.pages.ProfilePage
import com.example.footballstatistics_app_android.pages.SettingPage
import com.example.footballstatistics_app_android.ui.theme.FootballStatistics_App_AndroidTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean,
    val indicatorColor: Color
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FootballStatistics_App_AndroidTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "Home") {
                    composable("Home") {
                        HomePage()
                    }
                    composable("Setting") {
                        SettingPage()
                    }
                    composable("Calendar") {
                        CalendarPage()
                    }
                    composable("Profile") {
                        ProfilePage()
                    }
                    composable("Match") {
                        MatchPage()
                    }
                }
                val items = listOf(
                    BottomNavigationItem(
                        title = "Home",
                        selectedIcon = Icons.Filled.Home,
                        unselectedIcon = Icons.Outlined.Home,
                        indicatorColor = Color(0xfffdcb60),
                        hasNews = false
                    ),
                    BottomNavigationItem(
                        title = "Calendar",
                        selectedIcon = Icons.Filled.DateRange,
                        unselectedIcon = Icons.Outlined.DateRange,
                        indicatorColor = Color(0xff7da8e8),
                        hasNews = false
                    ),
                    BottomNavigationItem(
                        title = "Profile",
                        selectedIcon = Icons.Filled.Person,
                        unselectedIcon = Icons.Outlined.Person,
                        indicatorColor = Color(0xff59834a),
                        hasNews = false
                    ),
                    BottomNavigationItem(
                        title = "Setting",
                        selectedIcon = Icons.Filled.Settings,
                        unselectedIcon = Icons.Outlined.Settings,
                        indicatorColor = Color(0xfff94545),
                        hasNews = false
                    )
                )

                var selectedItemIndex by rememberSaveable {
                    mutableStateOf(0)
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            items.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    selected = selectedItemIndex == index,
                                    onClick = {
                                        selectedItemIndex = index
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
                                                imageVector = if (index == selectedItemIndex) {
                                                    item.selectedIcon
                                                } else item.unselectedIcon,
                                                contentDescription = item.title
                                            )
                                        }
                                    },
                                    colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                                        indicatorColor = item.indicatorColor, // Color of the selected indicator
                                    ),
                                    modifier = Modifier.padding(horizontal = 28.dp)
                                )
                            }

                        }
                    }
                ) { innerPadding ->
                    ContentScreen(modifier = Modifier.padding(innerPadding), selectedItemIndex)
                }
            }
        }
    }
}

@Composable
fun ContentScreen(modifier: Modifier = Modifier, selectedIndex : Int) {
    when(selectedIndex){
        0 -> HomePage()
        1 -> CalendarPage()
        2 -> ProfilePage()
        3 -> SettingPage()
        4 -> MatchPage()
    }
}

@Serializable
object Home

@Serializable
object Calendar

@Serializable
object Profile

@Serializable
object Setting

@Serializable
data class match(
    val matchid: Int,
)

