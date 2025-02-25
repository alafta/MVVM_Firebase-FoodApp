package com.example.mydicodingsubmission.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mydicodingsubmission.core.Response
import com.example.mydicodingsubmission.ui.ViewModel.LoginViewModel
import com.example.mydicodingsubmission.ui.navigation.NavGraph
import com.example.mydicodingsubmission.ui.screen.common.MyAlertDialog
import com.example.mydicodingsubmission.ui.screen.common.MyCircularProgress
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun LoginScreen(
    navHostController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val hostState = remember {
        SnackbarHostState()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = hostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Content(
            hostState = hostState,
            paddingValues = paddingValues,
            signInStateFlow = loginViewModel.loginFlow,
            resetPasswordStateFlow = loginViewModel.resetFlow,
            onRegisterNow = { navHostController.navigate(NavGraph.RegisterScreen.route) },
            onForgotPassword = { email -> loginViewModel.reset(email) },
            onLogin = { email, password -> loginViewModel.login(email, password) },
            loginSuccess = { navHostController.navigate(NavGraph.MainScreen.route) { popUpTo(0) } }
        )
    }
}

@Composable
fun Content(
    paddingValues: PaddingValues,
    signInStateFlow: MutableSharedFlow<Response<AuthResult>>,
    resetPasswordStateFlow: MutableSharedFlow<Response<Void?>>,
    onRegisterNow: () -> Unit,
    onForgotPassword: (String) -> Unit,
    onLogin: (String, String) -> Unit,
    loginSuccess: () -> Unit,
    hostState: SnackbarHostState
) {
    val emailText = remember {
        mutableStateOf("")
    }
    val passwordText = remember {
        mutableStateOf("")
    }
    var showForgotPasswordDialog by remember {
        mutableStateOf(false)
    }

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    if (showForgotPasswordDialog)
        MyAlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            onConfirmation = {
                if (emailText.value != "") {
                    onForgotPassword(emailText.value)
                    showForgotPasswordDialog = false
                } else {
                    scope.launch {
                        hostState.showSnackbar("Mohon masukkan alamat email anda.")
                    }
                }
            },
            title = "Lupa Kata Sandi?",
            text = "Kirim email pengaturan ulang kata sandi ke alamat email yang dimasukkan.",
            confirmButtonText = "Kirim",
            dismissButtonText = "Batal",
            cancelable = true
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Text(
            text = "Masuk",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 25.dp, end = 20.dp, top = 20.dp),
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Silahkan masuk untuk melanjutkan.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 25.dp, end = 20.dp, top = 5.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 20.dp, end = 20.dp, top = 40.dp),
            singleLine = true,
            value = emailText.value,
            onValueChange = { text -> emailText.value = text },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = { Icon(Icons.Filled.Email, "email") }
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(20.dp),
            singleLine = true,
            value = passwordText.value,
            onValueChange = { text -> passwordText.value = text },
            label = { Text("Kata Sandi") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = { Icon(Icons.Filled.Lock, "password") },
            trailingIcon = {
                IconButton(
                    onClick = {
                        passwordVisible = !passwordVisible
                    }
                ) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Sembunyikan" else "Tampilkan"
                    )
                }
            }
        )
        Button(
            onClick = {
                onLogin(emailText.value, passwordText.value)
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 20.dp, end = 20.dp, top = 20.dp),
            content = { Text(text = "Masuk") }
        )
        Text(
            color = MaterialTheme.colorScheme.primary,
            text = "Lupa Sandi?",
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .align(alignment = Alignment.CenterHorizontally)
                .padding(start = 20.dp, end = 20.dp, top = 10.dp)
                .clickable { showForgotPasswordDialog = true },
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = buildAnnotatedString {
                append("Tidak punya akun?")
                withStyle(style = SpanStyle(MaterialTheme.colorScheme.primary)) { append("Daftar Sekarang") }
            },
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .align(alignment = Alignment.CenterHorizontally)
                .padding(20.dp)
                .clickable { onRegisterNow() },
            style = MaterialTheme.typography.titleMedium
        )
    }
    LoginInState(
        flow = signInStateFlow,
        onSuccess = { loginSuccess() },
        onError = { scope.launch { hostState.showSnackbar("Alamat email dan sandi salah.") } }
    )
    ResetPasswordState(
        flow = resetPasswordStateFlow,
        onSuccess = { scope.launch { hostState.showSnackbar("Email berhasil terkirim, periksa kotak masuk Anda") } },
        onError = { scope.launch { hostState.showSnackbar("Ups! terjadi kesalahan, coba lagi.") } }
    )
}

@Composable
fun LoginInState(
    flow: MutableSharedFlow<Response<AuthResult>>,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    val isLoading = remember { mutableStateOf(false) }
    if (isLoading.value) MyCircularProgress()
    LaunchedEffect(Unit) {
        flow.collect {
            when (it) {
                is Response.Loading -> {
                    Timber.tag("Login state -> ").i("Loading")
                    isLoading.value = true
                }

                is Response.Error -> {
                    Timber.tag("Login state -> ").e(it.message)
                    isLoading.value = false
                    onError()
                }

                is Response.Success -> {
                    Timber.tag("Login state -> ").i("Success")
                    isLoading.value = false
                    onSuccess()
                }
            }
        }
    }
}

@Composable
fun ResetPasswordState(
    flow: MutableSharedFlow<Response<Void?>>,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    val isLoading = remember { mutableStateOf(false) }
    if (isLoading.value) MyCircularProgress()
    LaunchedEffect(Unit) {
        flow.collect {
            when (it) {
                is Response.Loading -> {
                    Timber.tag("Reset password state -> ").i("Loading")
                    isLoading.value = true
                }

                is Response.Error -> {
                    Timber.tag("Reset password state -> ").e(it.message)
                    isLoading.value = false
                    onError()
                }

                is Response.Success -> {
                    Timber.tag("Reset password state -> ").i("Success")
                    isLoading.value = false
                    onSuccess()
                }
            }
        }
    }
}