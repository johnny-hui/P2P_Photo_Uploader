package com.example.p2pphotouploader.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.p2pphotouploader.R
import com.example.p2pphotouploader.ui.theme.P2PPhotoUploaderTheme


@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "",
        )
    }
}




@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    P2PPhotoUploaderTheme {
        SplashScreen(modifier = Modifier.background(color = Color.Black))
    }
}