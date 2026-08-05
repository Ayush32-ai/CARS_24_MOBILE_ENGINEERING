package com.example.cars24sdui.renderer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cars24sdui.R
import com.example.cars24sdui.model.Component
import com.example.cars24sdui.model.Screen
import com.example.cars24sdui.ui.theme.CarsDimens
import com.example.cars24sdui.ui.theme.CarsPurple
import com.example.cars24sdui.ui.theme.PageInk
import com.example.cars24sdui.ui.theme.Danger

private val CarsBlue = Color(0xFF103D96)
private val Muted = Color(0xFF718096)
private val Success = Color(0xFF22C55E)

@Composable
fun ScreenRenderer(screen: Screen, onAction: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier) {
        screen.components.forEach { component -> ComponentRegistry.Render(component, onAction) }
    }
}

object ComponentRegistry {
    @Composable
    fun Render(component: Component, onAction: (String) -> Unit) = when (component.type) {
        "homeHeader" -> HomeHeader(component.props, onAction)
        "tileRail" -> TileRail(component.props, onAction)
        "loanRail" -> LoanRail(component.props, onAction)
        "serviceGrid" -> ServiceGrid(component.props, onAction)
        "carRail" -> CarRail(component.props, onAction)
        "vehicleGrid" -> VehicleGrid(component.props, onAction)
        "promoBanner" -> PromoBanner(component.props, onAction)
        "showroomRail" -> ShowroomRail(component.props, onAction)
        "trendingRail" -> TrendingRail(component.props, onAction)
        "matchBanner" -> MatchBanner(component.props, onAction)
        "brandFooter" -> BrandFooter(component.props)
        "spacer" -> Spacer(Modifier.height((component.props["height"] as? Double ?: 16.0).dp))
        "divider" -> androidx.compose.material3.HorizontalDivider(
            Modifier.padding(horizontal = 16.dp),
            thickness = 1.dp,
            color = Color.LightGray.copy(alpha = 0.5f)
        )
        else -> Unsupported(component.type)
    }
}

@Composable
private fun HomeHeader(props: Map<String, Any?>, onAction: (String) -> Unit) {
    var search by remember { mutableStateOf("") }
    Surface(color = CarsPurple) {
        Column(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = CarsDimens.headerPaddingTop, bottom = CarsDimens.headerPaddingBottom)
        ) {
            Row(
                Modifier
                    .padding(horizontal = CarsDimens.screenPaddingH)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "●  ${props.text("location")}⌄",
                    color = White,
                    fontSize = CarsDimens.headerLocationSize,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = CircleShape,
                    color = White.copy(alpha = .2f),
                    modifier = Modifier.size(CarsDimens.headerProfileSize)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("♙", color = White, fontSize = CarsDimens.headerProfileIcon)
                    }
                }
            }
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = CarsDimens.screenPaddingH, vertical = CarsDimens.searchPaddingV),
                shape = RoundedCornerShape(CarsDimens.searchRadius),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = White.copy(.12f),
                    unfocusedContainerColor = White.copy(.12f),
                    cursorColor = White,
                    focusedTextColor = White,
                    unfocusedTextColor = White
                ),
                leadingIcon = {
                    Text("⌕", color = White.copy(.6f), fontSize = CarsDimens.searchIconSize)
                },
                placeholder = {
                    AnimatedSearchPlaceholder(
                        placeholders = props.strings("placeholders").ifEmpty {
                            listOf(props.text("placeholder"))
                        }
                    )
                }
            )
            Row(
                Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = CarsDimens.screenPaddingH),
                horizontalArrangement = Arrangement.spacedBy(CarsDimens.tabGap)
            ) {
                props.maps("tabs").forEach { tab ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(CarsDimens.tabItemWidth)
                            .clickable { onAction(tab.text("title")) }
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (tab.text("title") == "All") White.copy(.2f) else White.copy(.12f),
                            modifier = Modifier.size(CarsDimens.tabCircleSize)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (tab.text("title") == "All") {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.GridView,
                                        contentDescription = null,
                                        tint = White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Text(tab.text("icon"), fontSize = CarsDimens.tabIconSize)
                                }
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            tab.text("title"),
                            color = White,
                            fontSize = CarsDimens.tabLabelSize,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimatedSearchPlaceholder(placeholders: List<String>) {
    val items = placeholders.filter { it.isNotBlank() }.ifEmpty { listOf("Search cars") }
    var index by remember(items) { mutableIntStateOf(0) }

    LaunchedEffect(items) {
        while (true) {
            delay(2500)
            index = (index + 1) % items.size
        }
    }

    Box(Modifier.height(CarsDimens.searchTextSize.value.dp + 4.dp)) {
        AnimatedContent(
            targetState = index,
            transitionSpec = {
                slideInVertically(
                    animationSpec = tween(350),
                    initialOffsetY = { fullHeight -> fullHeight }
                ) + fadeIn(tween(350)) togetherWith
                    slideOutVertically(
                        animationSpec = tween(350),
                        targetOffsetY = { fullHeight -> -fullHeight }
                    ) + fadeOut(tween(200))
            },
            label = "search_placeholder"
        ) { currentIndex ->
            Text(
                items[currentIndex],
                color = White.copy(.55f),
                fontSize = CarsDimens.searchTextSize
            )
        }
    }
}

@Composable
private fun TileRail(props: Map<String, Any?>, onAction: (String) -> Unit) {
    Section(props.text("title"), props.text("badge")) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(CarsDimens.railGap)) {
            items(props.maps("items")) { item -> BigTile(item, onAction) }
        }
    }
}

@Composable
private fun BigTile(item: Map<String, Any?>, onAction: (String) -> Unit) {
    val tileColor = item.color("color", CarsBlue)
    Card(
        Modifier
            .width(148.dp) // Adjusted width for better fit
            .height(134.dp) // Taller cards to match screenshot
            .clickable { onAction(item.text("title")) },
        shape = RoundedCornerShape(24.dp), // Very rounded corners
        colors = CardDefaults.cardColors(containerColor = tileColor)
    ) {
        Box(Modifier.fillMaxSize()) {
            // Graphic at bottom right
            NetworkPlaceholder(
                item.text("title"),
                Modifier
                    .size(96.dp)
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 2.dp, end = 2.dp),
                contentScale = ContentScale.Fit
            )
            // Text at top left
            Text(
                item.text("title"),
                color = White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                lineHeight = 19.sp,
                modifier = Modifier
                    .padding(14.dp)
                    .width(100.dp)
            )
        }
    }
}

@Composable
private fun LoanRail(props: Map<String, Any?>, onAction: (String) -> Unit) {
    Section(props.text("title")) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(props.maps("items")) { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(94.dp)
                        .clickable { onAction(item.text("title")) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp) // Large dome
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5FF))
                            .border(1.dp, Color(0xFFD4E2FF), CircleShape),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        NetworkPlaceholder(
                            item.text("title"),
                            Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        item.text("title"),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        lineHeight = 15.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = PageInk
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceGrid(props: Map<String, Any?>, onAction: (String) -> Unit) {
    Section(props.text("title")) {
        val services = props.maps("items")
        Column(verticalArrangement = Arrangement.spacedBy(CarsDimens.gridGap)) {
            services.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(CarsDimens.gridGap)) {
                    row.forEach { item -> ServiceTile(item, onAction, Modifier.weight(1f)) }
                    repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun ServiceTile(item: Map<String, Any?>, onAction: (String) -> Unit, modifier: Modifier) {
    Card(
        modifier
            .height(CarsDimens.serviceTileHeight)
            .clickable { onAction(item.text("title")) },
        shape = RoundedCornerShape(CarsDimens.serviceTileRadius),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAF3)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE1CCA2))
    ) {
        Box(Modifier.fillMaxSize()) {
            Text(
                item.text("title"),
                Modifier
                    .padding(CarsDimens.servicePadding)
                    .align(Alignment.TopStart),
                fontSize = CarsDimens.serviceTitleSize,
                lineHeight = CarsDimens.serviceTitleLine,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Box(
                Modifier
                    .size(CarsDimens.serviceIconSize)
                    .align(Alignment.BottomEnd)
                    .padding(end = 2.dp, bottom = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                NetworkPlaceholder(
                    item.text("title"),
                    Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}

@Composable
private fun CarRail(props: Map<String, Any?>, onAction: (String) -> Unit) {
    Section(
        props.text("title"),
        "View all",
        onBadgeClick = { onAction(props.text("viewAllAction")) }
    ) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(CarsDimens.carCardGap)) {
            items(props.maps("items")) { car -> UsedCar(car, onAction) }
        }
    }
}

@Composable
private fun UsedCar(car: Map<String, Any?>, onAction: (String) -> Unit) {
    var liked by remember { mutableStateOf(false) }
    Card(
        Modifier
            .width(CarsDimens.carCardWidth)
            .clickable { onAction(car.text("name")) },
        shape = RoundedCornerShape(CarsDimens.carCardRadius),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(CarsDimens.carImageHeight)
                .background(car.color("color", Color(0xFFF0F4FA)))
        ) {
            Image(
                painter = painterResource(carDrawable(car.text("image"))),
                contentDescription = car.text("name"),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
            Text(
                if (liked) "♥" else "♡",
                fontSize = CarsDimens.carHeartSize,
                color = if (liked) Danger else Muted,
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.TopEnd)
                    .clickable { liked = !liked }
            )
        }
        Column(Modifier.padding(CarsDimens.carCardPadding)) {
            Surface(shape = RoundedCornerShape(10.dp), color = Color(0xFFF0ECFF)) {
                Text(
                    "Cars24 Owned stock",
                    Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    color = CarsPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = CarsDimens.carBadgeSize
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(car.text("name"), fontWeight = FontWeight.Bold, fontSize = CarsDimens.carNameSize)
            Text(car.text("variant"), color = Muted, fontSize = CarsDimens.carVariantSize)
            Spacer(Modifier.height(6.dp))
            Text(car.text("specs"), color = Muted, fontSize = CarsDimens.carSpecsSize)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(car.text("price"), fontSize = CarsDimens.carPriceSize, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(8.dp))
                Text("EMI ${car.text("emi")}", color = Muted, fontSize = CarsDimens.carEmiSize)
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("❖ Zero Worry Max", color = CarsPurple, fontSize = CarsDimens.carFooterSize, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(12.dp))
                Text("✓ Lifetime warranty", color = Muted, fontSize = CarsDimens.carFooterSize)
            }
        }
    }
}

@Composable
private fun VehicleGrid(props: Map<String, Any?>, onAction: (String) -> Unit) {
    Surface(color = CarsPurple) {
        Column(
            Modifier.padding(
                horizontal = CarsDimens.screenPaddingH,
                vertical = CarsDimens.vehicleSectionPaddingV
            )
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    props.text("title"),
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = CarsDimens.vehicleTitleSize
                )
                Text(
                    props.text("actionText"),
                    color = White,
                    fontWeight = FontWeight.Medium,
                    fontSize = CarsDimens.vehicleAddSize,
                    modifier = Modifier.clickable { onAction("add_vehicle") }
                )
            }
            Spacer(Modifier.height(14.dp))
            val items = props.maps("items")
            Column(verticalArrangement = Arrangement.spacedBy(CarsDimens.gridGap)) {
                items.chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(CarsDimens.gridGap)) {
                        row.forEach { item ->
                            Card(
                                Modifier
                                    .height(CarsDimens.vehicleTileHeight)
                                    .weight(1f)
                                    .clickable { onAction(item.text("title")) },
                                shape = RoundedCornerShape(CarsDimens.vehicleTileRadius),
                                colors = CardDefaults.cardColors(containerColor = White)
                            ) {
                                Box(Modifier.fillMaxSize()) {
                                    Text(
                                        item.text("title"),
                                        fontSize = CarsDimens.vehicleTileTitleSize,
                                        lineHeight = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .padding(CarsDimens.vehicleTilePadding)
                                            .align(Alignment.TopStart)
                                    )
                                    Text(
                                        item.text("icon"),
                                        fontSize = CarsDimens.vehicleTileIconSize,
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(end = 4.dp, bottom = 2.dp)
                                    )
                                }
                            }
                        }
                        repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun PromoBanner(props: Map<String, Any?>, onAction: (String) -> Unit) {
    Card(
        Modifier
            .padding(CarsDimens.screenPaddingH)
            .fillMaxWidth()
            .height(CarsDimens.promoHeight)
            .clickable { onAction(props.text("action")) },
        shape = RoundedCornerShape(CarsDimens.promoRadius),
        colors = CardDefaults.cardColors(containerColor = props.color("color", CarsPurple))
    ) {
        Box(Modifier.fillMaxSize()) {
            NetworkPlaceholder(
                props.text("title"),
                Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(Modifier.fillMaxSize().background(props.color("color", CarsPurple).copy(alpha = 0.6f)))
            Column(Modifier.padding(CarsDimens.promoPadding).width(200.dp)) {
                Text(
                    props.text("eyebrow"),
                    color = Color(0xFFFFFF80),
                    fontWeight = FontWeight.Bold,
                    fontSize = CarsDimens.promoEyebrowSize
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    props.text("title"),
                    color = White,
                    fontSize = CarsDimens.promoTitleSize,
                    fontWeight = FontWeight.Bold,
                    lineHeight = CarsDimens.promoTitleLine
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    props.text("subtitle"),
                    color = White.copy(.8f),
                    fontSize = CarsDimens.promoSubtitleSize,
                    lineHeight = CarsDimens.promoSubtitleLine
                )
                Spacer(Modifier.height(14.dp))
                Surface(shape = RoundedCornerShape(50), color = White) {
                    Text(
                        props.text("action"),
                        Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        fontSize = CarsDimens.promoCtaSize,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ShowroomRail(props: Map<String, Any?>, onAction: (String) -> Unit) {
    Section(props.text("title")) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(CarsDimens.carCardGap)) {
            items(props.maps("items")) { shop ->
                Card(
                    Modifier
                        .width(CarsDimens.showroomCardWidth)
                        .clickable { onAction(shop.text("name")) },
                    shape = RoundedCornerShape(CarsDimens.showroomRadius),
                    colors = CardDefaults.cardColors(containerColor = White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Box(Modifier.fillMaxWidth().height(CarsDimens.showroomImageHeight)) {
                        Image(
                            painter = painterResource(R.drawable.showroom_placeholder),
                            contentDescription = shop.text("name"),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Surface(
                            shape = RoundedCornerShape(topEnd = 16.dp),
                            color = White,
                            modifier = Modifier.align(Alignment.BottomStart)
                        ) {
                            Text(
                                shop.text("cars"),
                                Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = CarsDimens.showroomCarsSize
                            )
                        }
                    }
                    Column(Modifier.padding(CarsDimens.showroomPadding)) {
                        Text(
                            shop.text("name"),
                            fontWeight = FontWeight.Bold,
                            fontSize = CarsDimens.showroomNameSize
                        )
                        Text(
                            shop.text("address"),
                            color = Muted,
                            fontSize = CarsDimens.showroomAddressSize
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            shop.text("distance"),
                            color = CarsPurple,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = CarsDimens.showroomMetaSize
                        )
                        Spacer(Modifier.height(4.dp))
                        Row {
                            Text(shop.text("status"), color = Danger, fontWeight = FontWeight.Bold, fontSize = CarsDimens.showroomMetaSize)
                            Spacer(Modifier.width(4.dp))
                            Text(shop.text("timing"), color = Muted, fontSize = CarsDimens.showroomMetaSize)
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { onAction("call") },
                                modifier = Modifier.weight(1f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CarsPurple),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("📞 Call us now", fontSize = 12.sp, color = CarsPurple)
                            }
                            Button(
                                onClick = { onAction("view_showroom") },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = CarsPurple),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("View showroom", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrendingRail(props: Map<String, Any?>, onAction: (String) -> Unit) {
    Section(props.text("title"), "View all") {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(CarsDimens.carCardGap)) {
            items(props.maps("items")) { car ->
                Card(
                    Modifier.width(180.dp).clickable { onAction(car.text("name")) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F8FA))
                ) {
                    Box(Modifier.fillMaxWidth().height(140.dp)) {
                        Text(
                            car.text("rank"),
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.LightGray.copy(alpha = 0.3f),
                            modifier = Modifier.align(Alignment.CenterStart).padding(start = 10.dp)
                        )
                        NetworkPlaceholder(car.text("name"), Modifier.fillMaxSize().padding(10.dp), contentScale = ContentScale.Fit)
                    }
                    Column(Modifier.padding(12.dp)) {
                        Text(car.text("name"), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(car.text("brand"), color = Muted, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun MatchBanner(props: Map<String, Any?>, onAction: (String) -> Unit) {
    Card(
        Modifier.padding(16.dp).fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Surface(color = Color(0xFF5747FF), shape = RoundedCornerShape(4.dp)) {
                    Text("Recommended", color = White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
                Spacer(Modifier.height(8.dp))
                Text(props.text("title"), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(props.text("subtitle"), color = Muted, fontSize = 13.sp)
                Spacer(Modifier.height(16.dp))
                Text(
                    props.text("action"),
                    color = CarsPurple,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onAction("match") }
                )
            }
            NetworkPlaceholder("person", Modifier.size(100.dp).clip(CircleShape))
        }
    }
}

@Composable
private fun NetworkPlaceholder(seed: String, modifier: Modifier, contentScale: ContentScale = ContentScale.Crop) {
    AsyncImage(
        model = "https://picsum.photos/seed/${seed.filter { it.isLetterOrDigit() }}/700/420",
        contentDescription = null,
        contentScale = contentScale,
        placeholder = painterResource(R.drawable.car_hatchback_placeholder),
        error = painterResource(R.drawable.car_hatchback_placeholder),
        modifier = modifier
    )
}

@Composable
private fun BrandFooter(props: Map<String, Any?>) {
    val configuration = LocalConfiguration.current
    val footerHeight = 450.dp // Fixed height for footer to match screenshot

    Surface(
        color = CarsPurple,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .heightIn(min = footerHeight)
                .padding(horizontal = 24.dp, vertical = 60.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                props.text("title").lowercase(),
                color = White,
                fontSize = 58.sp, // Very big text
                fontWeight = FontWeight.Bold,
                lineHeight = 62.sp,
                letterSpacing = (-1).sp
            )
            Spacer(Modifier.height(32.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Made with ",
                    color = White.copy(alpha = 0.8f),
                    fontSize = 22.sp
                )
                Text("❤️", fontSize = 20.sp)
                Text(
                    " in Gurugram",
                    color = White.copy(alpha = 0.8f),
                    fontSize = 22.sp
                )
            }
        }
    }
}

@Composable
private fun Unsupported(type: String) {
    Card(Modifier.padding(CarsDimens.screenPaddingH).fillMaxWidth()) {
        Text("⚠ Unsupported component: $type", Modifier.padding(16.dp))
    }
}

@Composable
private fun Section(
    title: String,
    badge: String = "",
    onBadgeClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Column(Modifier.padding(horizontal = CarsDimens.screenPaddingH, vertical = CarsDimens.sectionPaddingV)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                fontSize = CarsDimens.sectionTitleSize,
                fontWeight = FontWeight.Bold,
                color = PageInk
            )
            if (badge.isNotBlank()) {
                if (badge == "View all") {
                    Spacer(Modifier.weight(1f))
                } else {
                    Spacer(Modifier.width(12.dp))
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (badge == "View all") Color.Transparent else Danger,
                    modifier = Modifier.clickable(onClick = onBadgeClick)
                ) {
                    Text(
                        badge,
                        Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        color = if (badge == "View all") CarsPurple else White,
                        fontWeight = if (badge == "View all") FontWeight.Medium else FontWeight.Bold,
                        fontSize = if (badge == "View all") CarsDimens.viewAllSize else CarsDimens.sectionBadgeSize
                    )
                }
            }
        }
        Spacer(Modifier.height(CarsDimens.sectionTitleGap))
        content()
    }
}

private fun carDrawable(imageKey: String): Int = when (imageKey) {
    "swift" -> R.drawable.car_swift_placeholder
    else -> R.drawable.car_hatchback_placeholder
}

private fun Map<String, Any?>.text(key: String) = this[key] as? String ?: ""
private fun Map<String, Any?>.strings(key: String) =
    (this[key] as? List<*>)?.mapNotNull { it as? String }.orEmpty()
private fun Map<String, Any?>.maps(key: String) = (this[key] as? List<*>)?.mapNotNull { it as? Map<String, Any?> }.orEmpty()
private fun Map<String, Any?>.color(key: String, fallback: Color) =
    (this[key] as? String)?.let { runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrNull() } ?: fallback
