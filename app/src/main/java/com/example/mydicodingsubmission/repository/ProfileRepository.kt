package com.example.mydicodingsubmission.repository

import android.net.Uri
import android.util.Log
import com.example.mydicodingsubmission.model.UserData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class ProfileRepository  @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    fun getUserProfile(uid: String, onComplete: (UserData?) -> Unit ) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userData = document.toObject(UserData::class.java)
                    onComplete(userData)
                } else {
                    onComplete(null)
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    fun updateUserProfile(userData: UserData, onComplete: (Boolean) -> Unit) {
        firestore.collection("users").document(userData.uid)
            .set(userData)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    suspend fun updateProfilePicture(uid: String, uri: Uri): String {
        return try {
            val storageRef = storage.reference.child("profile_pictures/$uid/${System.currentTimeMillis()}.jpg")
            val uploadTask = storageRef.putFile(uri).await() // Upload the file to Firebase Storage
            val downloadUrl = storageRef.downloadUrl.await() // Get the download URL
            downloadUrl.toString()
        } catch (e: Exception) {
            Timber.tag("ProfileRepository").e("Failed to upload image: %s", e.message)
            throw e
        }
    }
}