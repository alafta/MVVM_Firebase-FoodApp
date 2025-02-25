package com.example.mydicodingsubmission.ui.screen


import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.mydicodingsubmission.model.FirebaseItems
import com.example.mydicodingsubmission.ui.ViewModel.MainViewModel
import com.example.mydicodingsubmission.ui.components.CustomBottomNavBar
import com.example.mydicodingsubmission.ui.components.CustomCardView
import com.example.mydicodingsubmission.ui.screen.common.MyCircularProgress
import com.example.mydicodingsubmission.ui.theme.MyDicodingSubmissionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navHostController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    MyDicodingSubmissionTheme {
        val items by mainViewModel.items.collectAsState()
        val searchQuery by mainViewModel.searchQuery.collectAsState()

        var isGridView by remember { mutableStateOf(true) }

        val filteredItems = items.filter { it.title.contains(searchQuery, ignoreCase = true) }
        val selectedIndex = remember { mutableIntStateOf(0) }

        val (editingItem, setEditingItem) = remember { mutableStateOf<FirebaseItems?>(null) }

        var showDeleteDialog by remember { mutableStateOf(false) }
        var selectedItem by remember { mutableStateOf<FirebaseItems?>(null) }


            LaunchedEffect(Unit) {
                mainViewModel.fetchUpdatedData()
            }

        val currentRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route?: "home"

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Daftar Makanan",
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(Color.Transparent)

                )

            },
            content = { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    // Search and Toggle Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SearchBar(
                            searchQuery = searchQuery,
                            onSearchQueryChange = mainViewModel::onSearchQueryChange,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = { isGridView = !isGridView },
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                imageVector = if (isGridView) Icons.AutoMirrored.Filled.ViewList else Icons.Default.GridView,
                                contentDescription = "Toggle View",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Dynamic Layout (Grid or List)
                    if (isGridView) {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 150.dp),
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredItems) { item ->
                                CustomCardView(
                                    item = item,
                                    onClick = {
                                        navHostController.currentBackStackEntry?.savedStateHandle?.set("firebaseItem", item)
                                        navHostController.navigate("detail")
                                    },
                                    onDeleteClick = {
                                        mainViewModel.deleteItem(item.documentId)
                                    },
                                    onFavoriteClick = {
                                        mainViewModel.toggleFavorite(item)
                                    },
                                    layoutType = "grid" // 🟢 Pass "grid" here
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredItems) { item ->
                                CustomCardView(
                                    item = item,
                                    onClick = {
                                        navHostController.currentBackStackEntry?.savedStateHandle?.set("firebaseItem", item)
                                        navHostController.navigate("detail")
                                    },
                                    onDeleteClick = {
                                        mainViewModel.deleteItem(item.documentId)
                                    },
                                    onFavoriteClick = {
                                        mainViewModel.toggleFavorite(item)
                                    },
                                    layoutType = "list" // 🔵 Pass "list" here
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {
                Box(
                    modifier = Modifier.background(Color.Transparent)
                ) {
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
            }
        )


        if (showDeleteDialog) {
            ConfirmActionDialog(
                title = "Hapus Item",
                message = "Apakah anda yakin ingin menghapus makanan ini dari daftar?",
                onConfirm = {
                    selectedItem?.let { mainViewModel.deleteItem(it.documentId) }
                },
                onDismiss = { showDeleteDialog = false }
            )
        }

        if (editingItem != null) {
            EditItemDialog(
                item = editingItem,
                onDismiss = { setEditingItem(null) },
                onSaveClick = { updatedItem, newImageUri ->
                    if (newImageUri != null) {
                        mainViewModel.uploadNewImage(newImageUri) { imageUrl ->
                            if (imageUrl != null) {
                                val itemWithNewImage = updatedItem.copy(imageUrl = imageUrl)
                                mainViewModel.updateItem(itemWithNewImage)
                            } else {

                            }
                            setEditingItem(null)
                        }
                    } else {
                        mainViewModel.updateItem(updatedItem)
                        setEditingItem(null)
                    }
                }
            )
        }
    }
}



@Composable
fun EditItemDialog(
    item: FirebaseItems,
    onDismiss: () -> Unit,
    onSaveClick: (FirebaseItems, Uri?) -> Unit
) {
    val scrollState = rememberScrollState()
    var title by remember { mutableStateOf(item.title) }
    var description by remember { mutableStateOf(item.description) }
    var addInfo by remember { mutableStateOf(item.addInfo ?: "") }
    var imageUrl by remember { mutableStateOf(item.imageUrl ?: "") }

    var pickedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            pickedImageUri = uri
        }
    }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                Text("Edit Item", style = MaterialTheme.typography.bodySmall)

                // Display the current or new image
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = pickedImageUri ?: imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile Picture",
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = 10.dp, y = (-10).dp)
                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                            .padding(4.dp)
                            .clickable { imagePickerLauncher.launch("image/*") }
                    )
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Nama Makanan") },
                    colors = TextFieldDefaults.colors(
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi") },
                    colors = TextFieldDefaults.colors(
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                OutlinedTextField(
                    value = addInfo,
                    onValueChange = { addInfo = it },
                    label = { Text("Informasi Tambahan") },
                    colors = TextFieldDefaults.colors(
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                        showConfirmationDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Simpan")
                    }
                }
            }
        }
    }

    if (showConfirmationDialog) {
        ConfirmActionDialog(
            title = "Edit Item",
            message = "Apakah anda yakin ingin menyimpan perubahan?",
            onConfirm = {
                val updatedItem = item.copy(title = title, description = description, addInfo = addInfo)
                onSaveClick(updatedItem, pickedImageUri)
                showConfirmationDialog = false
                onDismiss()
            },
            onDismiss = { showConfirmationDialog = false }
        )
    }
}



@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier


) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        label = { Text("Pencarian")},
        placeholder = { Text("Cari makanan...") },
        modifier = modifier
            .fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
    )
}

@Composable
fun ConfirmActionDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
    ) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .animateContentSize()
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Cancel icon
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Confirm icon
                        IconButton(onClick = {
                            onConfirm()
                            onDismiss()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Confirm",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                }
            }
        }
    }
}


