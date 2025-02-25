package com.example.mydicodingsubmission.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mydicodingsubmission.R

data class BottomBarItemData(
    val icon: ImageVector,
    val selected: Boolean = false,
    val onClick: () -> Unit = {}
)

@Composable
fun BottomBarItem(
    itemData: BottomBarItemData,
    selectedColor: Color = MaterialTheme.colorScheme.onPrimary,
    nonSelectedColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
    iconSize: Dp = 24.dp
) {
    IconButton(onClick = { itemData.onClick() }) {
        Icon(
            imageVector = itemData.icon,
            contentDescription = null,
            tint = if (itemData.selected) selectedColor else nonSelectedColor,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Preview
@Composable
fun BottomBarItemPreview() {
    val mockOnClick = {}
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {
        Text("Selected", color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        BottomBarItem(
            itemData = BottomBarItemData(
                onClick = mockOnClick,
                icon = Icons.Default.AccountCircle,
                selected = true
            ),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Not Selected", color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        BottomBarItem(
            itemData = BottomBarItemData(
                onClick = mockOnClick,
                icon = Icons.Default.AccountCircle,
                selected = false
            ),
        )
    }
}