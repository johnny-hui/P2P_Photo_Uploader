package com.example.p2pphotouploader.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.p2pphotouploader.R
import com.example.p2pphotouploader.ui.theme.P2PPhotoUploaderTheme
import kotlinx.coroutines.delay

/** Constants for delay timers (milliseconds) **/
private const val DELAY_TIMER_BUTTON: Long = 2000
private const val DELAY_TIMER_LOGO: Long = 500


@Composable
fun SplashScreen(modifier: Modifier = Modifier,
                 onGetStartedClicked: () -> Unit
) {
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DelayedLogo()
        DelayedButton(
            modifier.padding(100.dp).fillMaxWidth(),
            onClickHandler = onGetStartedClicked
        )
    }
}


@Composable
fun DelayedLogo(modifier: Modifier = Modifier) {
    var showLogo by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(DELAY_TIMER_LOGO) // Delay for 1/2 second
        showLogo = true
    }

    AnimatedVisibility(
        visible = showLogo,
        enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
        exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = stringResource(R.string.p2p_logo),
        )
    }
}


@Composable
fun DelayedButton(
    modifier: Modifier = Modifier,
    onClickHandler: () -> Unit = {}
) {
    var showButton by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(DELAY_TIMER_BUTTON) // Delay for 2 seconds
        showButton = true
    }

    AnimatedVisibility(
        visible = showButton,
        enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
        exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
    ) {
        Button(
            modifier = modifier.height(50.dp),
            colors = ButtonDefaults.buttonColors(Color.White),
            onClick = { onClickHandler() })
        {
            Text(
                text = stringResource(R.string.get_started),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    P2PPhotoUploaderTheme {
        SplashScreen(
            modifier = Modifier.background(color = Color.Black),
            onGetStartedClicked = {}
        )
    }
}