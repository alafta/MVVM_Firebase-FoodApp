package com.example.mydicodingsubmission.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import com.example.mydicodingsubmission.R
import com.example.mydicodingsubmission.ui.ViewModel.ProfileViewModel
import com.example.mydicodingsubmission.ui.components.BottomBar
import com.example.mydicodingsubmission.ui.components.BottomBarItemData
import com.example.mydicodingsubmission.ui.components.CustomBottomNavBar
import com.example.mydicodingsubmission.ui.navigation.NavGraph
import com.example.mydicodingsubmission.ui.screen.common.MyCircularProgress

@Composable
fun AboutScreen(
    navHostController: NavHostController,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val userData by profileViewModel.userData
    var isEditing by remember { mutableStateOf(false) }
    var editedUsername by remember { mutableStateOf(userData?.username ?: "") }
    var showImagePicker by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploadingImage by remember { mutableStateOf(false) }

    val currentRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route?: "profile"


    if (showImagePicker) {
        ImagePickerDialog(
            isOpen = showImagePicker,
            onDismiss = { showImagePicker = false },
            onImagePicked = { uri ->
                selectedImageUri = uri
                showImagePicker = false
            }
        )
    }

    // Show progress indicator while updating profile picture
    if (isUploadingImage) {
        MyCircularProgress()
    }


    Scaffold(
        bottomBar = {
            CustomBottomNavBar(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navHostController.navigate(route) {
                        popUpTo(navHostController.graph.startDestinationId) { inclusive = false }
                    }
                },
                onFabClick = {
                    navHostController.navigate("addFood")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Profile Picture Section
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Display picked image if available
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri ?: userData?.profilePictureUrl ?: R.drawable.baseline_account_circle_24),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(240.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile Picture",
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = 10.dp, y = (-10).dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .padding(4.dp)
                        .clickable { showImagePicker = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Accept/Cancel Icons if a new image is selected
            if (selectedImageUri != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(onClick = {
                        isUploadingImage = true
                        profileViewModel.updateProfilePicture(selectedImageUri!!).invokeOnCompletion {
                            isUploadingImage = false
                            selectedImageUri = null
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Accept change")
                    }
                    IconButton(onClick = {
                        selectedImageUri = null
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel change")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Name Field
            ProfileField(
                label = "Nama Pengguna",
                value = if (isEditing) editedUsername else userData?.username ?: "Loading...",
                icon = Icons.Default.Edit,
                onClick = {
                    isEditing = true
                },
                isEditing = isEditing,
                onValueChange = { editedUsername = it },
                onSave = {
                    userData?.let { updatedData ->
                        profileViewModel.updateUserProfile(updatedData.copy(username = editedUsername))
                    }
                    isEditing = false
                },
                onCancel = {
                    editedUsername = userData?.username ?: ""
                    isEditing = false
                }
            )

            // Email Field
            ProfileField(
                label = "Email",
                value = userData?.email ?: "Loading...",
                icon = Icons.Default.Edit,
                onClick = { /* Handle email editing */ }
            )

//
//            // Subscription Field (Placeholder for subscription info)
//            ProfileField(
//                label = "Subscription",
//                value = "Free", // You can make this dynamic based on actual subscription data
//                icon = Icons.Default.Edit,
//                onClick = { /* Handle subscription editing */ }
//            )

            Spacer(modifier = Modifier.weight(1f))

            // Logout Button
            Button(
                onClick = {
                    profileViewModel.logout()
                    navHostController.navigate(NavGraph.LoginScreen.route) { popUpTo(0) }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = "Log out", color = Color.White)
            }
        }
    }
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isEditing: Boolean = false,
    onValueChange: (String) -> Unit = {},
    onSave: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            if (isEditing) {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    onSave?.invoke()
                }) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Save")
                }
                IconButton(onClick = {
                    onCancel?.invoke()
                }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel")
                }
            } else {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onClick) {
                    Icon(imageVector = icon, contentDescription = "$label edit icon")
                }
            }
        }
    }
}

@Composable
fun ImagePickerDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onImagePicked: (Uri?) -> Unit
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        onImagePicked(uri)
    }

    if (isOpen) {
        AlertDialog(
            onDismissRequest = onDismiss,
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Pick from Gallery button
                    Button(
                        onClick = {
                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Pick from Gallery", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {}
        )
    }
}

//    if (isOpen) {
//        AlertDialog(
//            onDismissRequest = onDismiss,
//            title = { Text("Pick an image") },
//            text = {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Button(
//                        onClick = {
//                            // Launch the picker with a request for images only
//                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
//                        },
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Text("Pick from Gallery")
//                    }
//                    Button(onClick = { onDismiss }
//                    ) {
//                       Text("Cancel")
//                    }
//                }
//            },
//            confirmButton = {
//                Button(onClick = onDismiss) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }
//}
