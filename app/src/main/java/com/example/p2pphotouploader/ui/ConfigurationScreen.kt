package com.example.p2pphotouploader.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.p2pphotouploader.R
import com.example.p2pphotouploader.utility.SUCCESS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import kotlin.reflect.KSuspendFunction0

@ExperimentalMaterial3Api
@Composable
fun ConfigurationScreen(
    modifier: Modifier = Modifier,
    inputIP: String,
    inputPort: String,
    canNavigateBack: Boolean,
    navigateBackHandler: () -> Unit = {},
    isIPValid: Boolean,
    isIPWrong: Boolean,
    isPortWrong: Boolean,
    isPortValid: Boolean,
    onIPAddressFieldChanged: (String) -> Unit,
    onPortAddressFieldChanged: (String) -> Unit,
    onKeyboardDoneIP: () -> Unit,
    onKeyboardDonePort: () -> Unit,
    onPreviewPhotoClick: () -> Unit,
    onUploadClick: KSuspendFunction0<String>,
    isTransferring: Boolean,
    showUploadError: Boolean,
    showTransferErrorMsg: Boolean,
    onCloseDialog: () -> Unit,
    onSuccess: () -> Unit,
) {
    val context = LocalContext.current.applicationContext
    var result by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                modifier = modifier,
                title = stringResource(R.string.configuration_title_bar),
                canNavigateBack = canNavigateBack,
                navigateUp = navigateBackHandler,
                isTransferring = isTransferring
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TextInstructions(
                modifier = Modifier,
                headerText = stringResource(R.string.enter_target_information),
                descriptionText = stringResource(R.string.enter_target_info_desc)
            )
            IPAndPortTextFields(
                inputIP = inputIP,
                inputPort = inputPort,
                isIPWrong = isIPWrong,
                isIPValid = isIPValid,
                isPortWrong = isPortWrong,
                isPortValid = isPortValid,
                onIPAddressFieldChanged = { onIPAddressFieldChanged(it) },
                onPortAddressFieldChanged = { onPortAddressFieldChanged(it) },
                onKeyboardDoneIP = onKeyboardDoneIP,
                onKeyboardDonePort = onKeyboardDonePort,
                isEnabled = isTransferring
            )
            TextInstructions(
                modifier = Modifier,
                headerText = stringResource(R.string.ensure_face_header),
                descriptionText = stringResource(R.string.ensure_face_desc)
            )
            CustomButton(
                modifier = Modifier,
                text = stringResource(R.string.preview_photo),
                onClickHandler = onPreviewPhotoClick,
                isTransferring = isTransferring,
            )
            TextInstructions(
                modifier = Modifier,
                headerText = stringResource(R.string.upload_photo_header),
                descriptionText = stringResource(R.string.upload_photo_desc)
            )
            CustomButton(
                modifier = Modifier,
                color = colorResource(id = R.color.dandelion_yellow),
                text = stringResource(R.string.upload),
                isTransferring = isTransferring,
                onClickHandler = {
                    CoroutineScope(Dispatchers.Main).launch {
                        result = onUploadClick()

                        if(result == SUCCESS) {
                            makeToast(
                                context = context,
                                description = context.getString(R.string.transfer_success_toast_msg)
                            )
                            onSuccess()
                        }
                    }
                },
            )
        }
        if(showUploadError) {
            ConfigurationUploadErrorDialog(
                title = stringResource(R.string.upload_error),
                description = {
                    if(showTransferErrorMsg) 
                        Text(text = stringResource(R.string.upload_error_msg))
                    else 
                        Text(text = stringResource(R.string.upload_field_missing_desc)) 
                  },
                closeDialog = onCloseDialog
            )
        }
    }
}


/**
 * A composable that writes instructions for the user per step
 */
@Composable
fun TextInstructions(modifier: Modifier = Modifier,
                     headerText: String,
                     descriptionText: String
) {
    val robotoFontFamily = FontFamily(
        Font(R.font.roboto, FontWeight.Normal),
        Font(R.font.roboto_bold, FontWeight.Bold),
        Font(R.font.roboto_thin, FontWeight.Thin)
    )
    Column(
        modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = headerText,
            fontWeight = FontWeight.Bold,
            fontFamily = robotoFontFamily,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text = descriptionText,
            fontWeight = FontWeight.Normal,
            fontFamily = robotoFontFamily,
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            lineHeight = 19.sp
        )
    }
}


/**
 * A composable that handles the updating and verification
 * of IP address and port number TextFields.
 */
@Composable
fun IPAndPortTextFields(modifier: Modifier = Modifier,
                        inputIP: String,
                        inputPort: String,
                        isIPWrong: Boolean,
                        isIPValid: Boolean,
                        isPortWrong: Boolean,
                        isPortValid: Boolean,
                        onIPAddressFieldChanged: (String) -> Unit,
                        onPortAddressFieldChanged: (String) -> Unit,
                        onKeyboardDoneIP: () -> Unit,
                        onKeyboardDonePort: () -> Unit,
                        isEnabled: Boolean,
) {
    val robotoFontFamily = FontFamily(
        Font(R.font.roboto, FontWeight.Normal),
        Font(R.font.roboto_bold, FontWeight.Bold),
        Font(R.font.roboto_thin, FontWeight.Thin)
    )

    val focusManager = LocalFocusManager.current

    Column(
        modifier
            .padding(horizontal = 20.dp)
            .padding(bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
            ),
            value = inputIP,
            singleLine = true,
            enabled = !isEnabled, // disable when isTransferring
            shape = MaterialTheme.shapes.large,
            onValueChange = onIPAddressFieldChanged,  // fun updateIPAddress(inputIP: String)
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onKeyboardDoneIP()
                    focusManager.clearFocus()
                }
            ),
            isError = isIPWrong,
            label = {
                if(isIPWrong) {
                    Text(
                        text = stringResource(R.string.invalid_format),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Thin,
                        fontSize = 18.sp
                    )
                } else if(isIPValid) {
                    Text(
                        text = stringResource(R.string.valid_ip),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Thin,
                        fontSize = 18.sp,
                        color = Color.Green
                    )
                } else {
                    Text(
                        text = stringResource(R.string.enter_target_ip),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Thin,
                        fontSize = 18.sp
                    )
                }
            }
        )
        Spacer(modifier = Modifier.size(20.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
            ),
            value = inputPort,
            shape = MaterialTheme.shapes.large,
            singleLine = true,
            enabled = !isEnabled, // disable when isTransferring
            onValueChange = onPortAddressFieldChanged,  // fun updatePort(inputIP: String)
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onKeyboardDonePort()
                    focusManager.clearFocus()
                }
            ),
            isError = isPortWrong,
            label = {
                if(isPortWrong) {
                    Text(
                        text = stringResource(id = R.string.invalid_format),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Thin,
                        fontSize = 18.sp
                    )
                } else if(isPortValid) {
                    Text(
                        text = stringResource(R.string.valid_port),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Thin,
                        fontSize = 18.sp,
                        color = Color.Green
                    )
                } else {
                    Text(
                        text = stringResource(R.string.enter_target_port),
                        fontFamily = robotoFontFamily,
                        fontWeight = FontWeight.Thin,
                        fontSize = 18.sp
                    )
                }
            },
        )
    }
}


/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@ExperimentalMaterial3Api
@Composable
fun CustomTopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit = {},
    isTransferring: Boolean = false
) {
    TopAppBar(
        modifier = modifier,
        colors = topAppBarColors(containerColor = Color.Black),
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            if(canNavigateBack) {
                IconButton(
                    onClick = navigateUp,
                    enabled = !isTransferring
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}


/**
 * A multi-purpose custom button
 */
@Composable
fun CustomButton(modifier: Modifier = Modifier,
                 text: String,
                 color: Color = Color.White,
                 onClickHandler: () -> Unit = {},
                 isTransferring: Boolean = false
) {
    val animateStateButtonColor = animateColorAsState(
        targetValue = if (isTransferring) colorResource(id = R.color.transfer_blue) else color,
        animationSpec = tween(1000, 0, LinearOutSlowInEasing), label = ""
    )

    Column(
        modifier
            .padding(10.dp)
            .padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = Modifier.height(50.dp),
            enabled = !isTransferring, // disable when isTransferring
            colors = ButtonDefaults.buttonColors(
                containerColor = animateStateButtonColor.value
            ),
            onClick = { onClickHandler() }
        ) {
            if(isTransferring) {
                Text(
                    text = stringResource(R.string.uploading_state),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            } else {
                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}


/**
 * An alert dialog that warns the user about missing or incorrect format
 * in the attributes/states.
 */
@Composable
fun ConfigurationUploadErrorDialog(title: String,
                                   description: @Composable (() -> Unit)? = null,
                                   closeDialog: () -> Unit)
{
    AlertDialog(
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        icon = {
            Icon(
                Icons.Default.Error,
                contentDescription = stringResource(R.string.error_icon)
            )
        },
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
        },
        text = description,
        onDismissRequest = { closeDialog() },
        confirmButton = {
            TextButton(
                onClick = {
                    closeDialog()
                }
            ) {
                Text(stringResource(R.string.exit_dialog))
            }
        },
    )
}


private fun makeToast(context: Context, description: String) {
    Toast.makeText(
        context,
        description,
        Toast.LENGTH_SHORT
    ).show()
}