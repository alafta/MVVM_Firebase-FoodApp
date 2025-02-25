package com.example.mydicodingsubmission.ui.ViewModel

import android.net.Uri
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydicodingsubmission.model.FirebaseItems
import com.example.mydicodingsubmission.repository.FirestoreRepository
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {
    private val _items = MutableStateFlow<List<FirebaseItems>>(emptyList())
    val items: StateFlow<List<FirebaseItems>> = _items.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    private val _favoriteStates = mutableStateMapOf<String, Boolean>()

    val filteredItems: StateFlow<List<FirebaseItems>> = combine(_items, _searchQuery) { items, query ->
        if (query.isBlank()) {
            items
        } else {
            items.filter { it.title.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    init {
        fetchUpdatedData()
    }

    fun fetchItems() {
        viewModelScope.launch {
            try {
                firestoreRepository.fetchFoodListForUser { fetchedItems ->
                    _items.value = fetchedItems
                    fetchUpdatedData() // Ensuring filteredItems updates
                }
            } catch (e: Exception) {
                Timber.e(e, "Error fetching items")
            }
        }
    }


    fun fetchUpdatedData() {
        viewModelScope.launch {
            val newList = firestoreRepository.getAllItems()
            _items.value = newList
        }
    }


    fun addItem(item: FirebaseItems, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            firestoreRepository.addItem(item) { success ->
                if (success) {
                    fetchItems()
                    onComplete(true)
                } else {
                    Timber.e("Gagal menambahkan item")
                    onComplete(false)
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun uploadImage(imageUri: Uri, userId: String, onComplete: (String?) -> Unit) {
        viewModelScope.launch {
            firestoreRepository.uploadImage(imageUri, userId, onComplete)
        }
    }

    fun uploadNewImage(imageUri: Uri, onImageUploaded: (String?) -> Unit) {
        _isUploading.value = true

        firestoreRepository.uploadNewImage(imageUri) { imageUrl ->
            _isUploading.value = false
            if (imageUrl != null) {
                onImageUploaded(imageUrl)
            } else {
                // Handle failure
                Timber.e("Image upload failed")
                onImageUploaded(null)
            }
        }
    }

    fun updateItem(item: FirebaseItems) {
        viewModelScope.launch {
            firestoreRepository.updateItem(item) { success ->
                if (success) {
                    fetchItems()
                } else {
                    Timber.e("Failed to update item")
                }
            }
        }
    }

    fun deleteItem(documentId: String) {
        viewModelScope.launch {
            val success = firestoreRepository.deleteItem(documentId)
            if (success) {
                fetchItems()
            } else {
                Timber.e("Failed to delete item")
            }
        }
    }

    fun toggleFavorite(item: FirebaseItems) {
        val currentState = _favoriteStates[item.title] ?: false
        _favoriteStates[item.title] = !currentState
    }

    fun isFavorite(item: FirebaseItems): Boolean {
        return _favoriteStates[item.title] ?: false
    }
}

