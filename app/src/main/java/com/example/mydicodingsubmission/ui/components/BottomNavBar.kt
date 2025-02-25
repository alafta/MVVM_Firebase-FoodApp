package com.example.mydicodingsubmission.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mydicodingsubmission.R

@Composable
fun CustomBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onFabClick: () -> Unit
) {
    val items = listOf(
        BottomNavItem("Beranda", painterResource(id = R.drawable.hamburger), "home"),
        BottomNavItem("Favorit", painterResource(id = R.drawable.heart), ""),
        BottomNavItem("Profil", painterResource(id = R.drawable.user), "profile")
    )

    var isFabClicked by remember { mutableStateOf(false) }
    val fabScale by animateFloatAsState(
        targetValue = if (isFabClicked) 1.2f else 1f,
        animationSpec = tween(durationMillis = 200), label = ""
    )
    val fabElevation by animateDpAsState(
        targetValue = if (isFabClicked) 16.dp else 8.dp,
        animationSpec = tween(durationMillis = 200), label = ""
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .height(64.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    DynamicLabel(
                        item = item,
                        isSelected = currentRoute == item.route,
                        onClick = { onNavigate(item.route) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        FloatingActionButton(
            onClick = {
                isFabClicked = true
                onFabClick()
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .graphicsLayer (
                    scaleX = fabScale,
                    scaleY = fabScale
                )
                .padding(start = 8.dp)
                .padding(vertical = 8.dp)
                .size(64.dp)
                .shadow(12.dp, RoundedCornerShape(24.dp)),
            elevation = FloatingActionButtonDefaults.elevation(fabElevation)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.plus),
                contentDescription = "Add",
                modifier = Modifier
                    .size(24.dp),
                tint = Color.White
            )
        }

        LaunchedEffect(isFabClicked) {
            if (isFabClicked) {
                kotlinx.coroutines.delay(200)
                isFabClicked = false
            }
        }
    }
}

@Composable
fun DynamicLabel(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    val iconTint by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF2ECC71) else Color.Gray,
        animationSpec = tween(durationMillis = 300), label = ""
    )

    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .clickable(onClick = onClick)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSelected) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor),
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = item.icon,
                        contentDescription = item.label,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Icon(
                painter = item.icon,
                contentDescription = item.label,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: Painter,
    val route: String
)


@Preview(showBackground = true)
@Composable
fun CustomBottomNavBarPreview() {
    var currentRoute by remember { mutableStateOf("today_route") }

    CustomBottomNavBar(
        currentRoute = currentRoute,
        onNavigate = { selectedRoute ->
            currentRoute = selectedRoute
        },
        onFabClick = {}
    )
}
