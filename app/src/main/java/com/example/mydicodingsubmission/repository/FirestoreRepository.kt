package com.example.mydicodingsubmission.repository

import android.net.Uri
import com.example.mydicodingsubmission.model.FirebaseItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    private val firestore = FirebaseFirestore.getInstance()
    private val storageReference = FirebaseStorage.getInstance().reference

    suspend fun fetchFoodListForUser(onResult: (List<FirebaseItems>) -> Unit) {
        try {
            val user = authenticationRepository.getUser()
            user?.let {
                val userFoodListRef = firestore.collection("users")
                    .document(user.uid)
                    .collection("userFoodList")

                // Check if userFoodList collection exists for the user
                userFoodListRef.get()
                    .addOnSuccessListener { userFoodListSnapshot ->
                        if (userFoodListSnapshot.isEmpty) {
                            // Your existing logic for fetching global list
                        } else {
                            val userItemsList = userFoodListSnapshot.mapNotNull { document ->
                                val item = document.toObject(FirebaseItems::class.java)
                                item.copy(documentId = document.id)  // Set document ID
                            }
                            onResult(userItemsList)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Timber.e(exception, "Error getting user food list")
                        onResult(emptyList())
                    }
            } ?: run {
                Timber.e("User not found")
                onResult(emptyList())
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching user food list")
            onResult(emptyList())
        }
    }

    suspend fun getAllItems(): List<FirebaseItems> {
        return try {
            val user = authenticationRepository.getUser()
            if (user != null) {
                val userFoodListRef = firestore.collection("users")
                    .document(user.uid)
                    .collection("userFoodList")
                    .get()
                    .await() // Convert to suspend function

                val itemList = userFoodListRef.mapNotNull { document ->
                    val item = document.toObject(FirebaseItems::class.java)
                    item.copy(documentId = document.id)  // Set document ID
                }
                itemList
            } else {
                Timber.e("User not logged in")
                emptyList()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching user food list")
            emptyList()
        }
    }


    private fun fetchFromGlobalFoodList(onResult: (List<FirebaseItems>) -> Unit) {
        firestore.collection("foodList")
            .get()
            .addOnSuccessListener { result ->
                val globalFoodList = result.mapNotNull { document ->
                    document.toObject(FirebaseItems::class.java)
                }
                onResult(globalFoodList) // Return global food list
            }
            .addOnFailureListener { exception ->
                Timber.e(exception, "Error getting items from global FoodList")
                onResult(emptyList()) // Return an empty list on failure
            }
    }

    suspend fun addItem(item: FirebaseItems, onComplete: (Boolean) -> Unit) {
        val user = authenticationRepository.getUser()
        if (user != null ) {
            try {
                val userFoodListRef = firestore.collection("users")
                    .document(user.uid)
                    .collection("userFoodList")

                userFoodListRef.add(item)
                    .addOnSuccessListener {
                        Timber.d("Item berhasil ditambahkan")
                        onComplete(true)
                    }
                    .addOnFailureListener {
                        Timber.d("Item gagal ditambahkan")
                        onComplete(false)
                    }
            } catch (e: Exception) {
                Timber.e(e, "Error adding item to Firestore")
            }
        } else {
            Timber.e("User not found, unable to add item")
            onComplete(false)
        }

    }

    suspend fun updateItem(item: FirebaseItems, callback: (Boolean) -> Unit) {
        val user = authenticationRepository.getUser()
        if (user != null) {
            val documentRef = firestore.collection("users")
                .document(user.uid)
                .collection("userFoodList")
                .document(item.documentId)  // Use the document ID

            documentRef.update(
                mapOf(
                    "title" to item.title,
                    "description" to item.description,
                    "addInfo" to item.addInfo,
                    "imageUrl" to item.imageUrl
                )
            ).addOnSuccessListener {
                callback(true)
            }.addOnFailureListener {
                callback(false)
            }
        }
    }

    fun uploadNewImage(
        imageUri: Uri,
        onProgress: (Int) -> Unit = {}, // Progress callback
        onImageUploaded: (String?) -> Unit
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("usersFoodList/$userId/${UUID.randomUUID()}.jpg")

            val uploadTask = imageRef.putFile(imageUri)

            // Track upload progress
            uploadTask.addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
                onProgress(progress) // Send progress updates to the caller
            }

            // Handle success
            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    onImageUploaded(downloadUrl.toString()) // Return the URL
                }
            }

            // Handle failure
            uploadTask.addOnFailureListener { exception ->
                Timber.e(exception, "Failed to upload image")
                onImageUploaded(null) // Return null on failure
            }
        } else {
            Timber.e("User is not logged in")
            onImageUploaded(null) // Handle the case where the user is not logged in
        }
    }




    fun uploadImage(imageUri: Uri, userId: String, onComplete: (String?) -> Unit) {
        val userFolderRef = storageReference.child("usersFoodList/$userId/${imageUri.lastPathSegment}")
        val uploadTask = userFolderRef.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            userFolderRef.downloadUrl.addOnSuccessListener { uri ->
                onComplete(uri.toString()) // Return the download URL on success
            }.addOnFailureListener { exception ->
                Timber.e(exception, "Failed to get download URL")
                onComplete(null)
            }
        }.addOnFailureListener { exception ->
            Timber.e(exception, "Failed to upload image")
            onComplete(null)
        }
    }

    suspend fun deleteItem(documentId: String): Boolean {
        val user = authenticationRepository.getUser()
        return if (user != null) {
            try {
                firestore.collection("users")
                    .document(user.uid)
                    .collection("userFoodList")
                    .document(documentId)
                    .delete()
                    .await()
                Timber.d("Item successfully deleted")
                true
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete item")
                false
            }
        } else {
            Timber.e("User not logged in")
            false
        }
    }

}



//suspend fun getDataFromFirestore():FirebaseItems {
//    val db = FirebaseFirestore.getInstance()
//
//    try {
//        db.collection("")
//    }
//}



//    suspend fun getItems(onResult: (List<FirebaseItems>) -> Unit) {
//        try {
//            val user = authenticationRepository.getUser()
//            user?.let {
//                val userFoodListRef = firestore.collection("users")
//                    .document(it.uid)
//                    .collection("userFoodList")
//
//                userFoodListRef.get()
//                    .addOnSuccessListener { result ->
//                        val itemsList = result.mapNotNull { document ->
//                            document.toObject(FirebaseItems::class.java)
//                        }
//                        onResult(itemsList)
//                    }
//                    .addOnFailureListener { exception ->
//                        Timber.e(exception, "Error getting items from userFoodList")
//                        onResult(emptyList())
//                    }
//            } ?: run {
//                Timber.e("User not found")
//                onResult(emptyList())
//            }
//        } catch (e: Exception) {
//            Timber.e(e, "Error fetching items from Firestore")
//            onResult(emptyList())
//        }
//    }
