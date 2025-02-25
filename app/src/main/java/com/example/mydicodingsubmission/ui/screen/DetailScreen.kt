package com.example.mydicodingsubmission.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.mydicodingsubmission.R
import com.example.mydicodingsubmission.model.FirebaseItems
import com.example.mydicodingsubmission.ui.ViewModel.DetailViewModel
import com.example.mydicodingsubmission.ui.components.FavoriteButton
import timber.log.Timber

@Composable
fun DetailScreen(
    navHostController: NavHostController,
    detailViewModel: DetailViewModel = hiltViewModel()
) {
    var item by remember { mutableStateOf(navHostController.previousBackStackEntry?.savedStateHandle?.get<FirebaseItems>("firebaseItem")) }
    val isUploading by detailViewModel.isUploading.collectAsState()

    var isEditing by remember { mutableStateOf(false) }
    var updatedTitle by remember { mutableStateOf(item?.title.orEmpty()) }
    var updatedDescription by remember { mutableStateOf(item?.description.orEmpty()) }
    var updatedAddInfo by remember { mutableStateOf(item?.addInfo.orEmpty()) }
    var updatedImageUrl by remember { mutableStateOf(item?.imageUrl.orEmpty()) }
    var uploadProgress by remember { mutableStateOf(0) }

    var pickedImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            pickedImageUri = uri
            detailViewModel.uploadNewImage(
                imageUri = uri,
                onImageUploaded = { imageUrl ->
                    if (imageUrl != null) {
                        updatedImageUrl = imageUrl
                    } else {
                        Timber.e("Image upload failed")
                    }
                },
                onProgress = { progress ->
                    uploadProgress = progress // Update progress value
                }
            )
        }
    }

    item?.let {
        Scaffold(
            floatingActionButton = {
                if (isEditing) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                isEditing = false
                                updatedTitle = it.title
                                updatedDescription = it.description
                                updatedAddInfo = it.addInfo
                                updatedImageUrl = it.imageUrl
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val updatedItem = it.copy(
                                    title = updatedTitle,
                                    description = updatedDescription,
                                    addInfo = updatedAddInfo,
                                    imageUrl = updatedImageUrl
                                )
                                detailViewModel.updateItem(updatedItem) { success ->
                                    if (success) {
                                        item = updatedItem // Update the local item
                                        isEditing = false
                                        navHostController.previousBackStackEntry
                                            ?.savedStateHandle
                                            ?.set("shouldRefresh", true)// Exit editing mode
                                    } else {
                                        Timber.e("Failed to update item")
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Save")
                        }
                    }
                } else {
                    FloatingActionButton(
                        onClick = { isEditing = true },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                ) {
                    // Display the current or newly picked image
                    AsyncImage(
                        model = pickedImageUri ?: it.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Show LinearProgressIndicator during upload
                    if (isUploading) {
                        LinearProgressIndicator(
                            progress = {
                                uploadProgress / 100f // Convert percentage to 0-1 scale
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter)
                                .padding(16.dp),
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }

                    if (isEditing) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomEnd)
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = { imagePickerLauncher.launch("image/*") },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                                    .size(48.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.pencil),
                                    contentDescription = "Edit Image",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Back and Favorite Buttons (hidden when editing)
                    if (!isEditing) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            // Back Button
                            IconButton(
                                onClick = {
                                    navHostController.previousBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("shouldRefresh", true)
                                    navHostController.navigateUp()
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.Gray)
                                    .size(40.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.angle_back), // Replace with your back drawable
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }

                            // Favorite Button
                            var isFavorite by remember { mutableStateOf(false) }
                            FavoriteButton(
                                isLiked = isFavorite,
                                onLikeToggled = { isFavorite = !isFavorite },
                                icon = painterResource(id = R.drawable.heart),
                                likedColor = Color.Red,
                                unlikedColor = Color.White,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.Gray)
                                    .size(40.dp)
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                ) {
                    if (isEditing) {
                        OutlinedTextField(
                            value = updatedTitle,
                            onValueChange = { updatedTitle = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = it.title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isEditing) {
                        OutlinedTextField(
                            value = updatedDescription,
                            onValueChange = { updatedDescription = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = it.description,
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    thickness = 1.dp,
                    color = Color.Gray
                )

                if (isEditing) {
                    OutlinedTextField(
                        value = updatedAddInfo,
                        onValueChange = { updatedAddInfo = it },
                        label = { Text("Additional Info") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                } else {
                    Text(
                        text = it.addInfo,
                        textAlign = TextAlign.Justify,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    } ?: run {
        Timber.e("Item not found in savedStateHandle")
    }
}
