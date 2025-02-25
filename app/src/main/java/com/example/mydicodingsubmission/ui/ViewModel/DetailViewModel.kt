package com.example.mydicodingsubmission.ui.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydicodingsubmission.model.FirebaseItems
import com.example.mydicodingsubmission.repository.FirestoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _item = MutableStateFlow<FirebaseItems?>(null)
    val item: StateFlow<FirebaseItems?> = _item

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    private val _currentItem = MutableStateFlow<FirebaseItems?>(null)
    val currentItem: StateFlow<FirebaseItems?> = _currentItem

    fun setItem(item: FirebaseItems) {
        _item.value = item
    }

    fun uploadNewImage(
        imageUri: Uri,
        onImageUploaded: (String?) -> Unit,
        onProgress: (Int) -> Unit
    ) {
        _isUploading.value = true

        firestoreRepository.uploadNewImage(
            imageUri,
            onProgress = { progress ->
                onProgress(progress) // Update progress in the UI
            }
        ) { imageUrl ->
            _isUploading.value = false
            if (imageUrl != null) {
                onImageUploaded(imageUrl)
            } else {
                Timber.e("Image upload failed")
                onImageUploaded(null)
            }
        }
    }

    fun updateItem(item: FirebaseItems, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            firestoreRepository.updateItem(item) { success ->
                if (success) {
                    _currentItem.value = item
                    onComplete(true)
                } else {
                    Timber.e("Failed to update item")
                    onComplete(false)
                }
            }
        }
    }
}