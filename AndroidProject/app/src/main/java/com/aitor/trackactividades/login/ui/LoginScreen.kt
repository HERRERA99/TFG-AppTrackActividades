package com.aitor.trackactividades.login.ui

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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

@Composable
fun LoginScreen(
    navigateToRegister: () -> Unit,
    navigateToFeed: () -> Unit,
    loginViewModel: LoginViewModel
) {
    val navigateToHome: Boolean by loginViewModel.navigateToFeed.observeAsState(initial = false)

    LaunchedEffect(navigateToHome) {
        if (navigateToHome) {
            navigateToFeed()
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
                    Header(Modifier)
                    Body(
                        Modifier,
                        loginViewModel = loginViewModel
                    )
                    Footer(Modifier, navigateToRegister)
                }
            }
        }
    }
}

@Composable
fun Header(modifier: Modifier) {
    Icon(
        painter = painterResource(id = R.drawable.ic_launcher_foreground),
        contentDescription = "Logo",
        modifier = modifier.size(150.dp),
        tint = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun Footer(modifier: Modifier, navigateToRegister: () -> Unit) {
    Divider(
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
        thickness = 1.dp,
        modifier = Modifier.padding(top = 48.dp)
    )
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Don't have an account?", color = MaterialTheme.colorScheme.onBackground)
            TextButton(onClick = { navigateToRegister() }) {
                Text(text = "Sing Up", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun Body(modifier: Modifier, loginViewModel: LoginViewModel) {
    val email: String by loginViewModel.email.observeAsState(initial = "")
    val password: String by loginViewModel.password.observeAsState(initial = "")
    val isLoginEnable: Boolean by loginViewModel.isLoginEnable.observeAsState(initial = false)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
    ) {
        EmailInput(email, onTextChange = { loginViewModel.onLoginChanged(it, password) })
        PasswordInput(password, onTextChange = { loginViewModel.onLoginChanged(email, it) })
        ForgotPassword(modifier = Modifier.align(Alignment.End))
        LoginButton(isLoginEnable, loginViewModel)
    }
}

@Composable
fun ForgotPassword(modifier: Modifier) {
    TextButton(onClick = {}) {
        Text(
            text = "Forgot password?",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun EmailInput(email: String, onTextChange: (String) -> Unit) {
    TextField(
        value = email,
        onValueChange = { onTextChange(it) },
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
        placeholder = { Text(text = "Email") },
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color(0xFFB2B2B2),
            unfocusedTextColor = Color(0xFFB2B2B2),
            focusedContainerColor = Color(0xFFEFEFEF),
            unfocusedContainerColor = Color(0xFFEFEFEF),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        maxLines = 1,
        singleLine = true
    )
}

@Composable
fun PasswordInput(password: String, onTextChange: (String) -> Unit) {
    var passwordVisibility by remember { mutableStateOf(false) }
    TextField(
        value = password,
        onValueChange = { onTextChange(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "Password") },
        maxLines = 1,
        singleLine = true,
        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
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
            focusedTextColor = Color(0xFFB2B2B2),
            unfocusedTextColor = Color(0xFFB2B2B2),
            focusedContainerColor = Color(0xFFEFEFEF),
            unfocusedContainerColor = Color(0xFFEFEFEF),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
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
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = Color(0xFF78C8F9),
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContentColor = Color.White
        )
    ) {
        Text(text = "Log In")
    }
}




