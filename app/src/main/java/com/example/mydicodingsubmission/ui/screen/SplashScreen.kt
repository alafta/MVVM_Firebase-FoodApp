package com.example.mydicodingsubmission.ui.screen

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.mydicodingsubmission.R
import com.example.mydicodingsubmission.ui.navigation.AuthenticationNavigationViewModel
import com.example.mydicodingsubmission.ui.navigation.NavGraph
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navHostController: NavHostController,
    authenticationNavigationViewModel: AuthenticationNavigationViewModel = hiltViewModel()
) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnimation = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 3000
        )
    )
    LaunchedEffect(key1 = authenticationNavigationViewModel.isLoggedInState.value) {
        startAnimation = true
        delay(4000)
        navHostController.popBackStack()

        if (authenticationNavigationViewModel.isLoggedInState.value) {
            navHostController.navigate(NavGraph.LoginScreen.route)
        } else {
            navHostController.navigate(NavGraph.MainScreen.route)
        }
    }
    SplashView(alpha = alphaAnimation.value)
}

@Composable
fun SplashView(alpha: Float) {
    val isDarkTheme = isSystemInDarkTheme()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Image(
            painter = painterResource(
                id = if (isDarkTheme) R.drawable.logo_dark else R.drawable.logo_light
            ),
            contentDescription = "splash",
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.Center)
                .alpha(alpha),
        )
    }
}


//@Composable
//fun SplashView(alpha: Float) {
//    val isDarkTheme = isSystemInDarkTheme()
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//    ) {
//        Image(
//            painter = painterResource(
//                id = if (isDarkTheme) R.drawable.background_dark else R.drawable.background_light
//            ),
//            contentDescription = null,
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop
//        )
//
//        Image(
//            painter = painterResource(
//                id = if (isDarkTheme) R.drawable.logo_dark else R.drawable.logo_light
//            ),
//            contentDescription = "splash",
//            modifier = Modifier
//                .size(500.dp)
//                .align(Alignment.Center)
//                .alpha(alpha),
//        )
//    }
//}

@Composable
@Preview
fun SplashPrev() {
    SplashView(alpha = 1f)
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES)
fun SplashBackground() {
    SplashView(alpha = 1f)
}
