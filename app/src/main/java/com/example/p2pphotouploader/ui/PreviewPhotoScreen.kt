package com.example.p2pphotouploader.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.p2pphotouploader.R

@ExperimentalMaterial3Api
@Composable
fun PreviewPhotoScreen(modifier: Modifier = Modifier,
                       canNavigateBack: Boolean,
                       navigateBackHandler: () -> Unit = {},
                       imageBitmap: Bitmap?,
) {
    if(imageBitmap != null) {
        Scaffold (
            modifier = modifier
                .background(Color.Black)
                .fillMaxSize(),
            topBar = {
                CustomTopAppBar(
                    modifier = modifier,
                    title = stringResource(R.string.preview_photo),
                    canNavigateBack = canNavigateBack,
                    navigateUp = navigateBackHandler
                )
            }
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color.Black),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = modifier
                        .background(Color.Black)
                        .fillMaxSize(),
                    bitmap = imageBitmap.asImageBitmap(),
                    contentDescription = stringResource(R.string.captured_img),
                    contentScale = ContentScale.Crop
                )
            }
        }
    } else {
        Scaffold (
            modifier = modifier
                .background(Color.Black)
                .fillMaxSize(),
            topBar = {
                CustomTopAppBar(
                    modifier = modifier,
                    title = stringResource(R.string.preview_photo),
                    canNavigateBack = canNavigateBack,
                    navigateUp = navigateBackHandler,
                )
            }
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .background(Color.Black)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = modifier,
                    text = stringResource(R.string.preview_photo_error_desc),
                    textAlign = TextAlign.Center,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Thin
                )
            }
        }
    }
}
