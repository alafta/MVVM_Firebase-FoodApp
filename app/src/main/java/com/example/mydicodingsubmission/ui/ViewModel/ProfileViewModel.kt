package com.example.mydicodingsubmission.ui.ViewModel

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydicodingsubmission.auth.LogoutUseCase
import com.example.mydicodingsubmission.model.UserData
import com.example.mydicodingsubmission.repository.AuthenticationRepository
import com.example.mydicodingsubmission.repository.ProfileRepository
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor (
    private val profileRepository: ProfileRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _userData = mutableStateOf<UserData?>(null)
    val userData: State<UserData?> = _userData

    init {
        viewModelScope.launch {
            fetchUserData()
        }
    }

    private suspend fun fetchUserData() {
        val user = authenticationRepository.getUser()
        user?.let {
            profileRepository.getUserProfile(it.uid) { profile ->
                if (profile != null) {
                    _userData.value = profile
                } else {
                    val newUser = UserData(uid = it.uid, username = it.displayName ?: "Unknown", email = it.email ?: "No email")
                    _userData.value = newUser
                    updateUserProfile(newUser)
                }
            }
        }
    }

    fun updateUserProfile(userData: UserData) = viewModelScope.launch {
        profileRepository.updateUserProfile(userData) { success ->
            if (success) {
                _userData.value = userData
            }
        }
    }

    fun updateProfilePicture(uri: Uri) = viewModelScope.launch {
        val user = authenticationRepository.getUser()
        if (user != null) {
            val profilePictureUrl = profileRepository.updateProfilePicture(user.uid, uri)
            val updatedUserData = _userData.value?.copy(profilePictureUrl = profilePictureUrl)
            updatedUserData?.let {
                updateUserProfile(it)
            }
        }
    }
    fun logout() = viewModelScope.launch {
        logoutUseCase.invoke()
    }
}

//private fun fetchUserData() {
//    val user = authenticationRepository.getUser()
//    if (user != null) {
//        _userData.value = UserData(
//            username = user.displayName ?: "Unknown",
//            email = user.email ?: "No email"
//        )
//    }
//}

