package com.example.mydicodingsubmission.auth

import com.example.mydicodingsubmission.repository.AuthenticationRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(username: String, email: String, password: String) =
        authenticationRepository.register(username, email, password)
}