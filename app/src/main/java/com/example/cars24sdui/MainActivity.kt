package com.example.cars24sdui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cars24sdui.actions.ActionHandler
import com.example.cars24sdui.model.Screen
import com.example.cars24sdui.parser.JsonLoader
import com.example.cars24sdui.renderer.ScreenRenderer
import com.example.cars24sdui.ui.theme.Cars24SDUITheme
import com.example.cars24sdui.ui.theme.CarsDimens
import com.example.cars24sdui.ui.theme.CarsPurple
import com.example.cars24sdui.ui.theme.PageInk
import com.example.cars24sdui.ui.theme.MutedInk

private val NavInactive = Color(0xFF9CA3AF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { Cars24SDUITheme { Cars24App() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable fun Cars24App() {
    val context = LocalContext.current
    var state by remember { mutableStateOf<Result<Screen>?>(null) }
    var showFilters by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val sheetState = rememberModalBottomSheetState()

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
                                    when (action) {
                                        "filter" -> showFilters = true
                                        "open_showroom_sheet" -> showBottomSheet = true
                                        else -> ActionHandler(context).dispatch(action)
                                    }
                                })
                            }
                            // Rest of the content scrolls
                            Column(Modifier.verticalScroll(scrollState)) {
                                ScreenRenderer(Screen(otherComponents), onAction = { action ->
                                    when (action) {
                                        "view_all_used" -> showFilters = true
                                        "open_showroom_sheet" -> showBottomSheet = true
                                        else -> ActionHandler(context).dispatch(action)
                                    }
                                })
                            }
                        }
                    }
                }, onFailure = { Text("We couldn't load the home screen.") })
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState,
                    containerColor = White
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Showroom Details", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))
                        Text("Visit us to explore 50+ certified cars.", textAlign = TextAlign.Center)
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = { showBottomSheet = false },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = CarsPurple)
                        ) {
                            Text("Got it")
                        }
                        Spacer(Modifier.height(40.dp))
                    }
                }
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
                .padding(bottom = 12.dp)
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
                            Icons.Default.KeyboardArrowDown,
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
                                AnimatedContent(
                                    targetState = placeholders[placeholderIndex],
                                    transitionSpec = {
                                        (slideInVertically { height -> height } + fadeIn())
                                            .togetherWith(slideOutVertically { height -> -height } + fadeOut())
                                    },
                                    label = "PlaceholderAnimation"
                                ) { targetPlaceholder ->
                                    Text(
                                        targetPlaceholder,
                                        color = White.copy(.6f),
                                        fontSize = 14.sp,
                                        maxLines = 1
                                    )
                                }
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
                                            Icons.Default.GridView,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable private fun FilterScreen(onClose: () -> Unit) {
    BackHandler { onClose() }
    val filters = listOf("Filters", "Sort by", "Budget", "Make & model", "Model year", "Fuel", "Transmission")
    val placeholders = listOf("Search for \"Hyundai Creta\"", "Search for \"Maruti Swift\"", "Search for \"Tata Nexon\"")
    var placeholderIndex by remember { mutableIntStateOf(0) }
    
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(2500)
            placeholderIndex = (placeholderIndex + 1) % placeholders.size
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().background(Color(0xFFF7F8FA))) {
            // Sticky Header (Location + Search)
            Surface(color = CarsPurple, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.statusBarsPadding().padding(bottom = 16.dp)) {
                    Row(
                        Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = White.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp).clickable { onClose() }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.ArrowBack, null, tint = White, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text("New Delhi", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(6.dp))
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                null,
                                tint = White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Surface(
                            shape = CircleShape,
                            color = White.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.FavoriteBorder, null, tint = White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                    
                    // Search Box with Animation (Square-ish with 8dp radius)
                    Surface(
                        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = White
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                            Icon(Icons.Default.Search, null, tint = CarsPurple, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            AnimatedContent(
                                targetState = placeholders[placeholderIndex],
                                transitionSpec = {
                                    (slideInVertically { height -> height } + fadeIn())
                                        .togetherWith(slideOutVertically { height -> -height } + fadeOut())
                                },
                                label = "FilterPlaceholderAnimation"
                            ) { targetPlaceholder ->
                                Text(
                                    targetPlaceholder,
                                    color = PageInk.copy(0.6f),
                                    fontSize = 15.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // Horizontal Filter Chips (Sticky)
            Row(
                Modifier.fillMaxWidth().background(White).padding(vertical = 12.dp).horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEachIndexed { index, filter ->
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(0.5f)),
                        color = if (index < 2) CarsPurple.copy(0.05f) else White
                    ) {
                        Row(
                            Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (index == 0) {
                                Surface(color = CarsPurple, shape = CircleShape, modifier = Modifier.size(20.dp)) {
                                    Icon(Icons.Default.Tune, null, tint = White, modifier = Modifier.padding(4.dp))
                                }
                                Spacer(Modifier.width(6.dp))
                            }
                            if (index == 1) {
                                Surface(color = Color(0xFF5747FF), shape = CircleShape, modifier = Modifier.size(20.dp)) {
                                    Icon(Icons.Default.Sort, null, tint = White, modifier = Modifier.padding(4.dp))
                                }
                                Spacer(Modifier.width(6.dp))
                            }
                            Text(filter, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.KeyboardArrowDown, null, tint = PageInk.copy(0.6f), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Scrollable List
            Column(Modifier.verticalScroll(rememberScrollState())) {
                // Promo Banner (Scrolls away)
                Box(Modifier.fillMaxWidth().height(200.dp).background(CarsPurple)) {
                    AsyncImage(
                        model = "https://picsum.photos/seed/cars24banner/800/400",
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    Column(Modifier.padding(20.dp).align(Alignment.CenterStart)) {
                        Surface(color = Color.Black.copy(0.7f), shape = RoundedCornerShape(4.dp)) {
                            Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AllInclusive, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("LIFETIME WARRANTY", color = White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "India's first warranty that lasts\nas long as your car",
                            color = White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        Text("Know more →", color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Row(
                        Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(3) { i ->
                            Box(Modifier.size(if (i == 0) 6.dp else 4.dp).clip(CircleShape).background(if (i == 0) White else White.copy(0.5f)))
                        }
                    }
                }

                // Result Count
                Text(
                    "3266 Used cars available in New Delhi",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 13.sp,
                    color = MutedInk,
                    fontWeight = FontWeight.Medium
                )

                // Car List
                repeat(5) {
                    SearchResultCard()
                    Spacer(Modifier.height(12.dp))
                }
            }
        }

        // FAB
        Surface(
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp).size(56.dp),
            shape = CircleShape,
            color = Color(0xFF5747FF),
            shadowElevation = 6.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.AutoAwesome, null, tint = White, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@Composable
fun SearchResultCard() {
    Card(
        Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(Modifier.fillMaxWidth().height(180.dp).background(Color(0xFFF3F6FC))) {
                AsyncImage(
                    model = "https://picsum.photos/seed/car/800/400",
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                )
                Icon(
                    Icons.Outlined.FavoriteBorder,
                    null,
                    tint = MutedInk,
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).size(24.dp)
                )
            }
            
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Budget", color = MutedInk, fontSize = 12.sp, modifier = Modifier.background(Color(0xFFF1F5FF), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp))
                    Spacer(Modifier.weight(1f))
                    Surface(color = Color(0xFFF0ECFF), shape = RoundedCornerShape(10.dp)) {
                        Text("Cars24 Owned stock", color = CarsPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
                
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("2015 Hyundai Eon", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("MAGNA +", color = MutedInk, fontSize = 13.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("₹1.72 lakh", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("+other charges", color = MutedInk, fontSize = 10.sp)
                    }
                }
                
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("43,518 km  Petrol  Manual  DL8C", color = MutedInk, fontSize = 12.sp)
                    Spacer(Modifier.weight(1f))
                    Text("EMI ₹4,529/m* >", color = PageInk, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val badges = listOf("✓ Lifetime warranty", "🔄 30 days return", "📋 300+ quality")
                    badges.forEach { badge ->
                        Surface(color = Color(0xFFF7F8FA), shape = RoundedCornerShape(4.dp)) {
                            Text(badge, fontSize = 10.sp, color = PageInk.copy(0.7f), modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
                        }
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                androidx.compose.material3.HorizontalDivider(color = Color.LightGray.copy(0.3f))
                Row(Modifier.fillMaxWidth().height(48.dp)) {
                    Text("Free Test Drive", modifier = Modifier.weight(1f).fillMaxHeight().clickable {}.wrapContentSize(Alignment.Center), color = CarsPurple, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Box(Modifier.width(1.dp).fillMaxHeight().background(Color.LightGray.copy(0.3f)))
                    Text("View Details", modifier = Modifier.weight(1f).fillMaxHeight().clickable {}.wrapContentSize(Alignment.Center), color = CarsPurple, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
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
        containerColor = White,
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
                    indicatorColor = Color.Transparent
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
