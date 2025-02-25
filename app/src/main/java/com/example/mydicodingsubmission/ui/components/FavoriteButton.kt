package com.example.mydicodingsubmission.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mydicodingsubmission.R

@Composable
fun FavoriteButton(
    isLiked: Boolean,
    onLikeToggled: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter,
    likedColor: Color,
    unlikedColor: Color,
    contentDescription: String = "Favorite button"
) {
    IconButton(
        onClick = onLikeToggled,
        modifier = modifier
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            tint = if (isLiked) likedColor else unlikedColor
        )
    }
}

@Composable
fun FavoriteButtonDemo() {
    var isLiked by remember { mutableStateOf(false) }

    FavoriteButton(
        isLiked = isLiked,
        onLikeToggled = { isLiked = !isLiked },
        icon = painterResource(id = R.drawable.heart),
        likedColor = Color.Red,
        unlikedColor = Color.Gray,
        modifier = Modifier.size(24.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun FavoriteButtonPreview() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        // Unliked state
        FavoriteButton(
            isLiked = false,
            onLikeToggled = {},
            icon = painterResource(id = R.drawable.heart),
            likedColor = Color.Red,
            unlikedColor = Color.Gray,
            modifier = Modifier.size(24.dp)
        )

        // Liked state
        FavoriteButton(
            isLiked = true,
            onLikeToggled = {},
            icon = painterResource(id = R.drawable.heart),
            likedColor = Color.Red,
            unlikedColor = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
}



