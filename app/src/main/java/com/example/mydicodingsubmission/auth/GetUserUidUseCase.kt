package com.example.mydicodingsubmission.auth

import com.example.mydicodingsubmission.repository.AuthenticationRepository
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class GetUserUidUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(): FirebaseUser? =
        authenticationRepository.getUser()
}