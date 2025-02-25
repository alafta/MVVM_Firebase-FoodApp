package com.example.mydicodingsubmission.ui.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydicodingsubmission.auth.RegisterUseCase
import com.example.mydicodingsubmission.core.Response
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    private var _registerFlow = MutableSharedFlow<Response<AuthResult>>()
    val registerFlow = _registerFlow

    fun register(username: String, email: String, password: String) = viewModelScope.launch {
        registerUseCase.invoke(username, email, password).collect {
            _registerFlow.emit(it)
        }
    }
}