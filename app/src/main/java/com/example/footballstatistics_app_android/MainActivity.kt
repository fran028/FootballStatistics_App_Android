package com.example.footballstatistics_app_android

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.footballstatistics_app_android.pages.CalendarPage
import com.example.footballstatistics_app_android.pages.HomePage
import com.example.footballstatistics_app_android.pages.LoginPage
import com.example.footballstatistics_app_android.pages.MatchPage
import com.example.footballstatistics_app_android.pages.ProfilePage
import com.example.footballstatistics_app_android.pages.RegisterPage
import com.example.footballstatistics_app_android.ui.theme.FootballStatistics_App_AndroidTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.example.footballstatistics_app_android.Theme.green
import com.example.footballstatistics_app_android.Theme.white
import com.example.footballstatistics_app_android.Theme.yellow
import com.example.footballstatistics_app_android.Theme.blue
import com.example.footballstatistics_app_android.Theme.gray
import android.content.Context
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.navigation.NavType
import androidx.navigation.navArgument
import java.util.HashSet
import kotlin.io.path.name

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val hasNews: Boolean,
    val indicatorColor: Color,
    val route: String,
)

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private val requiredPermissions = arrayOf(
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.FOREGROUND_SERVICE
    )

    private var isBluetoothConnected = mutableStateOf(false)
    lateinit var container: AppContainer
    private val dataTransferReceiver = DataTransferReceiver {
        Log.d("DataTransferReceiver", "Data transfer complete")
        runOnUiThread { recreate() }
    }
    /*private val bluetoothConnectionReceiver = BluetoothConnectionReceiver { isConnected ->
        isBluetoothConnected.value = isConnected
    }*/
    /*private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your app.
                Log.d(TAG, "BLUETOOTH_CONNECT permission granted")
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied.
                Log.w(TAG, "BLUETOOTH_CONNECT permission denied")
            }
        }*/

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate called")
        super.onCreate(savedInstanceState)

        container = (applicationContext as FootballStatisticsApplication).container

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        Log.d(TAG, "bluetooth permission checked")
        checkBluetoothPermissions()
        Log.d(TAG, "service started")
        val serviceIntent = Intent(this, DataListenerService::class.java)
        startService(serviceIntent)
        //onBluetoothConnected(isSmartwatchConnected(this))
        //registerReceiver(dataTransferReceiver, IntentFilter(Constants.DATA_TRANSFER_COMPLETE))
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(dataTransferReceiver, IntentFilter(Constants.DATA_TRANSFER_COMPLETE), Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(dataTransferReceiver, IntentFilter(Constants.DATA_TRANSFER_COMPLETE))
        }
        val bluetoothFilter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }
        //registerReceiver(bluetoothConnectionReceiver, bluetoothFilter)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(bluetoothConnectionReceiver, bluetoothFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(bluetoothConnectionReceiver, bluetoothFilter)
        }*/
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
                var isBluetoothConnectedState by remember { isBluetoothConnected }
                MainScreen(
                    isBluetoothConnected = isBluetoothConnectedState,
                    onBluetoothConnectedChange = { isConnected ->
                        isBluetoothConnectedState = isConnected
                    }
                )

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(dataTransferReceiver)
        //unregisterReceiver(bluetoothConnectionReceiver)
    }

    override fun onResume() {
        super.onResume()
//        val bluetoothFilter = IntentFilter().apply {
//            addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED)
//            addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED)
//        }
//        registerReceiver(bluetoothConnectionReceiver, bluetoothFilter)
    }

    override fun onPause() {
        super.onPause()
//        unregisterReceiver(bluetoothConnectionReceiver)
//        unregisterReceiver(dataTransferReceiver)
    }

    private fun checkBluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permissions granted")
            } else {
                Log.w(TAG, "Permissions denied")
            }
        }
    }

    private fun onBluetoothConnected(isConnected: Boolean) {
        Log.d(TAG, "onBluetoothConnected: $isConnected")
        isBluetoothConnected.value = isConnected
    }

    /*fun getConnectedBluetoothDevices(context: Context): Set<BluetoothDevice> {
        Log.d(TAG, "getConnectedBluetoothDevices called")
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.e("BluetoothUtils", "Bluetooth is not supported on this device")
            return emptySet()
        }
        if (!bluetoothAdapter.isEnabled) {
            Log.w("BluetoothUtils", "Bluetooth is not enabled")
            return emptySet()
        }

        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
            ?: return emptySet<BluetoothDevice>().also {
                Log.e("BluetoothUtils", "Failed to get BluetoothManager")
            }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.w("BluetoothUtils", "BLUETOOTH_CONNECT permission not granted")
            return emptySet()
        }

        val connectedDevices = mutableSetOf<BluetoothDevice>()
        val profiles = intArrayOf(
            BluetoothProfile.GATT,
            BluetoothProfile.HEADSET,
            BluetoothProfile.A2DP,
        )

        for (profile in profiles) {
            try {
                connectedDevices.addAll(bluetoothManager.getConnectedDevices(profile))
            } catch (e: SecurityException) {
                Log.e("BluetoothUtils", "No devices found", e)
            }
        }
        return connectedDevices
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun isSmartwatchConnected(context: Context): Boolean {
        val connectedDevices = getConnectedBluetoothDevices(context)
        for (device in connectedDevices) {
            if (isProbablyASmartwatch(device)) {
                return true
            }
        }
        return false
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun isProbablyASmartwatch(device: BluetoothDevice): Boolean {
        val deviceName = device.name?.lowercase() ?: return false

        return deviceName.contains("watch") ||
                deviceName.contains("band") ||
                deviceName.contains("smart")
    }*/
}

@Composable
fun BluetoothStatusIcon(isConnected: Boolean, modifier: Modifier) {
    var color = white
    var border = gray
    if (!isConnected) {
        color = blue
        border = white
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .border(1.dp, border, CircleShape)
                .clip(CircleShape)
                .background(color)
                .padding(2.dp)
        ) {
            Icon(
                imageVector = if (isConnected) Icons.Filled.Bluetooth else Icons.Filled.BluetoothDisabled,
                contentDescription = "Bluetooth Status",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(isBluetoothConnected: Boolean, onBluetoothConnectedChange: (Boolean) -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
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
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {

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
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
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
                composable(
                    route = Screen.Match.route,
                    arguments = listOf(
                        navArgument("match_id") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val matchid = backStackEntry.arguments?.getInt("match_id") ?: 0
                    MatchPage(
                        navController = navController,
                        modifier = Modifier,
                        match_id = matchid
                    )
                }
                composable(Screen.Calendar.route) {
                    CalendarPage(
                        navController = navController,
                        modifier = Modifier
                    )
                }
                composable(Screen.Profile.route) {
                    ProfilePage(
                        navController = navController,
                        modifier = Modifier
                    )
                }
            }
        }
        if (currentDestination?.route != Screen.Login.route && currentDestination?.route != Screen.Register.route && isBluetoothConnected) {
            BluetoothStatusIcon(
                isConnected = isBluetoothConnected, modifier = Modifier
                    .align(Alignment.BottomCenter) // Position it TopEnd, TopStart, BottomEnd, BottomStart, etc.
                    .offset(x = 0.dp, y = -100.dp) // Fine-tune the offset
                    .padding(16.dp)
            )
        }
    }
}
