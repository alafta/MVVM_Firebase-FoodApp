package com.example.mydicodingsubmission.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.mydicodingsubmission.R
import com.example.mydicodingsubmission.model.FirebaseItems
import com.example.mydicodingsubmission.ui.screen.common.MyCircularProgress

@Composable
fun CustomCardView(
    item: FirebaseItems,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDeleteClick: () -> Unit,
    layoutType: String = "grid"
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        if (layoutType == "grid") {
            Column(modifier = Modifier.padding(12.dp)) {
                ImageContent(item)
                Spacer(modifier = Modifier.height(8.dp))
                TextContent(item, onDeleteClick, layoutType)
            }
        } else {
            Row(
                modifier = Modifier.padding(8.dp),
            ) {
                ImageContent(item, Modifier.size(100.dp))
                Spacer(modifier = Modifier.width(8.dp))
                TextContent(item, onDeleteClick, layoutType)
            }
        }
    }
}

@Composable
fun ImageContent(item: FirebaseItems, modifier: Modifier = Modifier.fillMaxWidth().aspectRatio(1.5f)) {
    Box(modifier = modifier) {
        val painter = rememberAsyncImagePainter(
            model = item.imageUrl ?: R.drawable.ayam_goreng,
            error = painterResource(id = R.drawable.ayam_goreng),
            placeholder = painterResource(id = R.drawable.ayam_goreng)
        )

        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        if (painter.state is AsyncImagePainter.State.Loading) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                MyCircularProgress()
            }
        }

        val isLiked = remember { mutableStateOf(false) }

        FavoriteButton(
            isLiked = isLiked.value,
            onLikeToggled = { isLiked.value = !isLiked.value },
            icon = painterResource(id = R.drawable.heart),
            likedColor = Color.Red,
            unlikedColor = Color.White,
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Gray)
                .padding(8.dp)
                .align(Alignment.TopStart) // ✅ Now it works correctly!
        )
    }
}


@Composable
fun TextContent(item: FirebaseItems, onDeleteClick: () -> Unit, layoutType: String) {
    if (layoutType == "list") {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f) // Ensures text takes available space
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = { onDeleteClick() }) {
                Icon(
                    painter = painterResource(id = R.drawable.trash),
                    contentDescription = "Delete Item",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = { onDeleteClick() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.trash),
                        contentDescription = "Delete Item",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}



