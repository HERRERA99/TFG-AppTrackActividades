package com.aitor.trackactividades.authentication.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aitor.trackactividades.R
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext

@Composable
fun LoginScreen(
    navigateToRegister: () -> Unit,
    navigateToFeed: () -> Unit,
    loginViewModel: LoginViewModel
) {
    val shouldNavigateToFeed: Boolean by loginViewModel.navigateToFeed.observeAsState(initial = false)

    LaunchedEffect(shouldNavigateToFeed) {
        if (shouldNavigateToFeed) {
            navigateToFeed()
        }
    }

    val errorMessage: String? by loginViewModel.errorMessage.observeAsState()
    val context = LocalContext.current

    val currentErrorMessage by rememberUpdatedState(errorMessage)

    LaunchedEffect(currentErrorMessage) {
        currentErrorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }


    Scaffold { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                Modifier
                    .padding(24.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val isLoading: Boolean by loginViewModel.isLoading.observeAsState(initial = false)

                if (isLoading) {
                    CircularProgressIndicator(Modifier)
                } else {
                    LoginHeader(Modifier)
                    LoginBody(
                        Modifier,
                        loginViewModel = loginViewModel
                    )
                    Footer("No tienes cuenta?", "Registrate aquí", Modifier, navigateToRegister)
                }
            }
        }
    }
}

@Composable
fun LoginHeader(modifier: Modifier) {
    Icon(
        painter = painterResource(id = R.drawable.ic_launcher_foreground),
        contentDescription = "Logo",
        modifier = modifier.size(150.dp),
        tint = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun Footer(
    titleText: String,
    buttonText: String,
    modifier: Modifier,
    navigateToRegister: () -> Unit
) {
    Divider(
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
        thickness = 1.dp,
        modifier = Modifier.padding(top = 48.dp)
    )
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = titleText, color = MaterialTheme.colorScheme.onBackground)
            TextButton(onClick = { navigateToRegister() }) {
                Text(
                    text = buttonText,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun LoginBody(modifier: Modifier, loginViewModel: LoginViewModel) {
    val email: String by loginViewModel.identifier.observeAsState(initial = "")
    val password: String by loginViewModel.password.observeAsState(initial = "")
    val isLoginEnable: Boolean by loginViewModel.isLoginEnable.observeAsState(initial = false)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
    ) {
        EmailInput(
            email = email,
            register = false,
            onTextChange = { loginViewModel.onLoginChanged(it, password) })
        PasswordInput(
            text = "Contraseña",
            password = password,
            info = false,
            onTextChange = { loginViewModel.onLoginChanged(email, it) })
        ForgotPassword(modifier = Modifier.align(Alignment.End))
        LoginButton(isLoginEnable, loginViewModel)
    }
}

@Composable
fun ForgotPassword(modifier: Modifier) {
    TextButton(onClick = {}) {
        Text(
            text = "Olvidaste tu contraseña?",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun EmailInput(email: String, register: Boolean, onTextChange: (String) -> Unit) {
    OutlinedTextField(
        value = email,
        onValueChange = { onTextChange(it) },
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        },
        label = { Text(text = if (register) "Email" else "Usuario o Email") },
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        maxLines = 1,
        singleLine = true
    )
}

@Composable
fun PasswordInput(text: String, password: String, info: Boolean, onTextChange: (String) -> Unit) {
    var passwordVisibility by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = password,
        onValueChange = { onTextChange(it) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = text) },
        supportingText = if (info) {
            { Text(text = "Mínimo 8 caracteres, una mayúscula, una minúscula y un número.") }
        } else null,
        maxLines = 1,
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisibility) {
                Icons.Filled.Visibility
            } else {
                Icons.Filled.VisibilityOff
            }
            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                Icon(imageVector = image, contentDescription = "show password")
            }
        },
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        ),
        visualTransformation = if (passwordVisibility) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        }
    )
}

@Composable
fun LoginButton(loginEnable: Boolean, loginViewModel: LoginViewModel) {
    Button(
        onClick = { loginViewModel.onLoginSelected() },
        enabled = loginEnable,
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
        )
    ) {
        Text(text = "Log In")
    }
}




