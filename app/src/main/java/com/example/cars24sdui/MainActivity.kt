package com.example.cars24sdui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.example.cars24sdui.actions.ActionHandler
import com.example.cars24sdui.model.Screen
import com.example.cars24sdui.parser.JsonLoader
import com.example.cars24sdui.renderer.ScreenRenderer
import com.example.cars24sdui.ui.theme.Cars24SDUITheme
import com.example.cars24sdui.ui.theme.CarsDimens
import com.example.cars24sdui.ui.theme.CarsPurple
import com.example.cars24sdui.ui.theme.PageInk

private val NavInactive = androidx.compose.ui.graphics.Color(0xFF9CA3AF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { Cars24SDUITheme { Cars24App() } }
    }
}

@Composable fun Cars24App() {
    val context = LocalContext.current
    var state by remember { mutableStateOf<Result<Screen>?>(null) }
    var showFilters by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) { state = JsonLoader(context).loadHome() }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = { CarsBottomNav() }
    ) { contentPadding ->
        Box(Modifier.fillMaxSize().padding(contentPadding)) {
            val result = state
            if (result == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                result.fold(onSuccess = { screen ->
                    if (screen.components.isEmpty()) {
                        Text("Nothing available")
                    } else if (showFilters) {
                        FilterScreen(onClose = { showFilters = false })
                    } else {
                        // Separate sticky header and scrollable content
                        val header = screen.components.find { it.type == "homeHeader" }
                        val otherComponents = screen.components.filter { it.type != "homeHeader" }

                        Column(Modifier.fillMaxSize()) {
                            // Header stays on top
                            header?.let {
                                StickyHomeHeader(it.props, scrollState, onAction = { action ->
                                    if (action == "filter") showFilters = true else ActionHandler(context).dispatch(action)
                                })
                            }
                            // Rest of the content scrolls
                            Column(Modifier.verticalScroll(scrollState)) {
                                ScreenRenderer(Screen(otherComponents), onAction = { action ->
                                    if (action == "view_all_used" || action == "filter") showFilters = true
                                    else ActionHandler(context).dispatch(action)
                                })
                            }
                        }
                    }
                }, onFailure = { Text("We couldn't load the home screen.") })
            }
        }
    }
}

@Composable
fun StickyHomeHeader(props: Map<String, Any?>, scrollState: androidx.compose.foundation.ScrollState, onAction: (String) -> Unit) {
    // The tabs will disappear as we scroll
    val alpha = (1f - (scrollState.value / 200f)).coerceIn(0f, 1f)

    Surface(color = CarsPurple) {
        Column(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(bottom = 12.dp) // Added bottom padding for the whole container
        ) {
            // Location and Profile (Always visible)
            Row(
                Modifier
                    .padding(horizontal = CarsDimens.screenPaddingH, vertical = 8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = White,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.LocationOn,
                                null,
                                tint = CarsPurple,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            props["location"] as? String ?: "",
                            color = White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            androidx.compose.material.icons.Icons.Default.KeyboardArrowDown,
                            null,
                            tint = White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Surface(
                    shape = CircleShape,
                    color = White,
                    modifier = Modifier.size(40.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CarsPurple)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, tint = CarsPurple, modifier = Modifier.size(24.dp))
                    }
                }
            }

            // Search Bar (Always visible)
            var search by remember { mutableStateOf("") }
            val placeholders = listOf("Search Baleno", "Search Swift", "Search Alto")
            var placeholderIndex by remember { mutableIntStateOf(0) }
            
            LaunchedEffect(Unit) {
                while (true) {
                    kotlinx.coroutines.delay(2000)
                    placeholderIndex = (placeholderIndex + 1) % placeholders.size
                }
            }

            // Using Box + BasicTextField for total control over "thin" height and centering
            androidx.compose.foundation.text.BasicTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = CarsDimens.screenPaddingH, vertical = 6.dp)
                    .height(44.dp)
                    .background(White.copy(.12f), RoundedCornerShape(8.dp))
                    .border(1.dp, White.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = White, fontSize = 14.sp),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(White),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, null, tint = White.copy(.7f), modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (search.isEmpty()) {
                                Text(
                                    placeholders[placeholderIndex],
                                    color = White.copy(.6f),
                                    fontSize = 14.sp,
                                    maxLines = 1
                                )
                            }
                            innerTextField()
                        }
                    }
                }
            )

            // Tabs (Fades out icons, keeps text sticky)
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(if (alpha > 0.1f) 100.dp else 44.dp) // Taller row for big tabs
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = CarsDimens.screenPaddingH),
                horizontalArrangement = Arrangement.spacedBy(16.dp), // More space between tabs
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tabs = (props["tabs"] as? List<*>)?.mapNotNull { it as? Map<String, Any?> }.orEmpty()
                tabs.forEach { tab ->
                    val isSelected = tab["title"] == "All"
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(if (alpha > 0.1f) 80.dp else 100.dp) // Wider tabs
                            .clickable { onAction(tab["title"] as? String ?: "") }
                    ) {
                        if (alpha > 0.4f) {
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) White else White.copy(.15f),
                                modifier = Modifier
                                    .graphicsLayer(alpha = (alpha - 0.4f) * 2.5f)
                                    .size(56.dp) // Big circles
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (tab["title"] == "All") {
                                        Icon(
                                            androidx.compose.material.icons.Icons.Default.GridView,
                                            null,
                                            tint = CarsPurple,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    } else {
                                        Text(tab["icon"] as? String ?: "", fontSize = 26.sp)
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                        Text(
                            tab["title"] as? String ?: "",
                            color = White,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                        if (isSelected) {
                            Spacer(Modifier.height(4.dp))
                            Box(
                                Modifier
                                    .width(24.dp)
                                    .height(2.dp)
                                    .background(White)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable private fun FilterScreen(onClose: () -> Unit) {
    val filters = listOf("SUV", "Hatchback", "Sedan", "Automatic", "Petrol", "Under ₹5 lakh")
    var selected by remember { mutableStateOf(setOf<String>()) }
    Column(Modifier.fillMaxSize().padding(22.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Filters", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Close", color = CarsPurple, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onClose))
        }
        Text("Find the car that fits your needs", fontSize = 15.sp, color = NavInactive)
        filters.forEach { filter ->
            OutlinedButton(
                onClick = { selected = if (filter in selected) selected - filter else selected + filter },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (filter in selected) CarsPurple else PageInk,
                    containerColor = if (filter in selected) CarsPurple.copy(alpha = 0.1f) else androidx.compose.ui.graphics.Color.Transparent
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (filter in selected) CarsPurple else NavInactive.copy(alpha = 0.5f))
            ) {
                Text(if (filter in selected) "✓  $filter" else filter)
            }
        }
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CarsPurple),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Show ${if (selected.isEmpty()) "all" else selected.size} cars", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable private fun CarsBottomNav() {
    var selected by remember { mutableIntStateOf(0) }
    val destinations = listOf(
        NavDestination("Home", Icons.Outlined.Home, Icons.Filled.Home),
        NavDestination("Activity", Icons.Outlined.Description, Icons.Outlined.Description),
        NavDestination("My Garage", Icons.Outlined.DirectionsCar, Icons.Outlined.DirectionsCar),
        NavDestination("Showrooms", Icons.Outlined.Storefront, Icons.Outlined.Storefront),
        NavDestination("Explore", Icons.Outlined.Explore, Icons.Outlined.Explore)
    )

    NavigationBar(
        containerColor = androidx.compose.ui.graphics.Color.White,
        contentColor = NavInactive
    ) {
        destinations.forEachIndexed { index, destination ->
            val isSelected = selected == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { selected = index },
                icon = {
                    Icon(
                        imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = destination.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        destination.label,
                        fontSize = CarsDimens.bottomNavLabelSize,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CarsPurple,
                    selectedTextColor = CarsPurple,
                    unselectedIconColor = NavInactive,
                    unselectedTextColor = NavInactive,
                    indicatorColor = androidx.compose.ui.graphics.Color.Transparent
                )
            )
        }
    }
}

private data class NavDestination(
    val label: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)

@Preview(showBackground = true) @Composable fun Cars24Preview() { Cars24SDUITheme { Cars24App() } }
