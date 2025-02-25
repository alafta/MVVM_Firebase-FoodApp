package com.example.mydicodingsubmission.ui.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mydicodingsubmission.auth.LoginUseCase
import com.example.mydicodingsubmission.auth.ResetPasswordUseCase
import com.example.mydicodingsubmission.core.Response
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val resetUseCase: ResetPasswordUseCase
) : ViewModel() {
    private val _loginFlow = MutableSharedFlow<Response<AuthResult>>()
    val loginFlow = _loginFlow

    private val _resetFlow = MutableSharedFlow<Response<Void?>>()
    val resetFlow = _resetFlow

    fun login(email: String, password: String) = viewModelScope.launch {
        loginUseCase.invoke(email, password).collect {response ->
            _loginFlow.emit(response)
        }
    }

    fun reset(email: String) = viewModelScope.launch {
        resetUseCase.invoke(email).collect { response ->
            _resetFlow.emit(response)
        }
    }
}