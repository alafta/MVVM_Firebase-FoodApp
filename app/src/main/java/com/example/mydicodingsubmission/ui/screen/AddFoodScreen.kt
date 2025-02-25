package com.example.mydicodingsubmission.ui.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.mydicodingsubmission.R
import com.example.mydicodingsubmission.model.FirebaseItems
import com.example.mydicodingsubmission.ui.ViewModel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(
    navHostController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel(),
    context: Context = LocalContext.current
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var foodName by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var additionalInfo by remember { mutableStateOf(TextFieldValue("")) }
    var isLoading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
    }

    val colors = MaterialTheme.colorScheme
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(colors.primary, colors.onPrimary)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tambah Daftar Makanan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onPrimary
                    )
                } ,
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navHostController.navigate("home") {
                                popUpTo(navHostController.graph.startDestinationId) {
                                    inclusive = true
                                }
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.angle_back),
                            contentDescription = "Back",
                            tint = colors.onPrimary
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(backgroundGradient)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(
                            colors.secondary,
                            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri == null) {
                        Text (
                            text = "Unggah Gambarmu",
                            textAlign = TextAlign.Center,
                            color = colors.onSecondary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        IconButton(
                            onClick = { launcher.launch("image/*") },
                            modifier = Modifier
                                .size(40.dp)
                                .align(Alignment.BottomEnd)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(colors.onPrimary, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.plus) ,
                                    contentDescription = "Add Icon",
                                    tint = colors.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(model = selectedImageUri),
                            contentDescription = "Selected Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = foodName,
                    onValueChange = { foodName = it },
                    label = { Text("Nama Makanan", fontSize = 12.sp, color = colors.onSurface) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedLabelColor = colors.tertiary,
                        unfocusedLabelColor = colors.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi", fontSize = 12.sp, color = colors.onSurface) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedLabelColor = colors.tertiary,
                        unfocusedLabelColor = colors.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = additionalInfo,
                    onValueChange = { additionalInfo = it },
                    label = { Text("Informasi Tambahan", fontSize = 12.sp, color = colors.onSurface) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(horizontal = 16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedLabelColor = colors.tertiary,
                        unfocusedLabelColor = colors.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (foodName.text.isNotEmpty() && description.text.isNotEmpty() && selectedImageUri != null) {
                            isLoading = true
                            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

                            if (currentUserId != null) {
                                mainViewModel.uploadImage(selectedImageUri!!, userId = currentUserId) { imageUrl ->
                                    if (imageUrl != null) {
                                        val newItem = FirebaseItems(
                                            title = foodName.text,
                                            description = description.text,
                                            imageUrl = imageUrl,
                                            addInfo = additionalInfo.text
                                        )
                                        mainViewModel.addItem(newItem) { success ->
                                            if (success) {
                                                navHostController.navigate("home") {
                                                    popUpTo("home") { inclusive = true }
                                                }
                                            } else {
                                                Toast.makeText(context, "Gagal menambahkan item.", Toast.LENGTH_SHORT).show()
                                            }
                                            isLoading = false
                                        }
                                    } else {
                                        Toast.makeText(context, "Gagal mengunggah gambar.", Toast.LENGTH_SHORT).show()
                                        isLoading = false
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Gagal mendapatkan User ID.", Toast.LENGTH_SHORT).show()
                                isLoading = false
                            }
                        } else {
                            Toast.makeText(context, "Harap isi semua kolom dan unggah gambar.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.tertiary,
                        contentColor = colors.onPrimary
                    ),
                    shape = RoundedCornerShape(25)
                ) {
                    Text("Tambahkan", fontSize = 16.sp)
                }

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = colors.onSecondary)
                }
            }
        },
        bottomBar = {},
    )
}


//@Preview(showBackground = true)
//@Composable
//fun PreviewAddFoodScreen() {
//    AddFoodScreen()
//}