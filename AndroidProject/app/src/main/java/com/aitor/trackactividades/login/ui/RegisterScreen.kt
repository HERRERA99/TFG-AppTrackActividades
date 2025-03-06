package com.aitor.trackactividades.login.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegisterScreen(
    registerViewModel: RegisterViewModel,
    navigateToLogin: () -> Unit,
    navigateToFeed: () -> Unit
) {
    val navigateToFeed: Boolean by registerViewModel.navigateToFeed.observeAsState(initial = false)

    LaunchedEffect(navigateToFeed) {
        if (navigateToFeed) {
            navigateToFeed()
        }
    }

    Scaffold { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                Modifier
                    .padding(24.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val isLoading: Boolean by registerViewModel.isLoading.observeAsState(initial = false)

                if (isLoading) {
                    CircularProgressIndicator(Modifier)
                } else {
                    RegisterHeader(Modifier)
                    RegisterBody(
                        Modifier.weight(1f),
                        registerViewModel = registerViewModel
                    )
                    Footer("Ya tienes cuenta?", "Log in", Modifier, navigateToLogin)
                }
            }
        }
    }
}

@Composable
fun RegisterHeader(modifier: Modifier) {
    Text(
        text = "Crear cuenta",
        modifier = modifier.padding(bottom = 16.dp),
        style = MaterialTheme.typography.headlineLarge
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegisterBody(modifier: Modifier, registerViewModel: RegisterViewModel) {
    val email: String by registerViewModel.email.observeAsState(initial = "")
    val password1: String by registerViewModel.password1.observeAsState(initial = "")
    val password2: String by registerViewModel.password2.observeAsState(initial = "")
    val name: String by registerViewModel.name.observeAsState(initial = "")
    val surname: String by registerViewModel.surname.observeAsState(initial = "")
    val birthDate: LocalDate by registerViewModel.birthDate.observeAsState(initial = LocalDate.now())
    val gender: String by registerViewModel.gender.observeAsState(initial = "")
    val textoInfo: String by registerViewModel.textoInfo.observeAsState(initial = "")

    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailInput(
            email = email,
            onTextChange = {
                registerViewModel.onRegisterChanged(
                    it,
                    password1,
                    password2,
                    name,
                    surname,
                    birthDate,
                    gender
                )
            })
        PasswordInput(
            text = "Contraseña",
            password = password1,
            onTextChange = {
                registerViewModel.onRegisterChanged(
                    email,
                    it,
                    password2,
                    name,
                    surname,
                    birthDate,
                    gender
                )
            })
        PasswordInput(
            text = "Repite contraseña",
            password = password2,
            onTextChange = {
                registerViewModel.onRegisterChanged(
                    email,
                    password1,
                    it,
                    name,
                    surname,
                    birthDate,
                    gender
                )
            }
        )

        NameInput(
            text = "Nombre",
            variable = name,
            onTextChange = {
                registerViewModel.onRegisterChanged(
                    email,
                    password1,
                    password2,
                    it,
                    surname,
                    birthDate,
                    gender
                )
            }
        )

        NameInput(
            text = "Apellido",
            variable = surname,
            onTextChange = {
                registerViewModel.onRegisterChanged(
                    email,
                    password1,
                    password2,
                    name,
                    it,
                    birthDate,
                    gender
                )
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()) {
                DateOfBirthInput(
                    selectedDate = birthDate,
                    onDateSelected = {
                        registerViewModel.onRegisterChanged(
                            email,
                            password1,
                            password2,
                            name,
                            surname,
                            it,
                            gender
                        )
                    },
                    modifier = Modifier,
                    showDatePicker = showDatePicker,
                    setShowDatePicker =
                    {
                        showDatePicker = it
                    }
                )
            }
            Box(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()) {
                GenderInput(
                    selectedGender = gender,
                    modifier = Modifier,
                    onGenderSelected =
                    {
                        registerViewModel.onRegisterChanged(
                            email,
                            password1,
                            password2,
                            name,
                            surname,
                            birthDate,
                            it
                        )
                    }
                )
            }
        }

        Text(text = textoInfo, color = Color.Red)

        RegisterButton(
            registerViewModel = registerViewModel
        )
    }
}

@Composable
fun GenderInput(selectedGender: String, modifier: Modifier, onGenderSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Masculino", "Femenino", "Otros")
    var gender by remember { mutableStateOf(selectedGender) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = gender,
            onValueChange = {},
            modifier = modifier
                .clickable { expanded = true },
            colors = TextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onBackground,
                disabledContainerColor = Color.Transparent,
                disabledIndicatorColor = MaterialTheme.colorScheme.primary,
                disabledLabelColor = MaterialTheme.colorScheme.onPrimary,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            },
            readOnly = true,
            enabled = false,
            label = { Text(text = "Género") }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            genderOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        gender = option
                        onGenderSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun NameInput(text: String, variable: String, onTextChange: (String) -> Unit) {
    OutlinedTextField(
        value = variable,
        onValueChange = { onTextChange(it) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = text) },
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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateOfBirthInput(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier,
    showDatePicker: Boolean,
    setShowDatePicker: (Boolean) -> Unit
) {
    val formattedDate =
        selectedDate?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "Selecciona fecha"

    OutlinedTextField(
        value = formattedDate,
        onValueChange = {},
        modifier = modifier
            .clickable { setShowDatePicker(true) },
        readOnly = true,
        enabled = false,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        },
        placeholder = { Text(text = "Fecha de nacimiento") },
        label = { Text(text = "Fecha de nacimiento") },
        colors = TextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor = Color.Transparent,
            disabledIndicatorColor = MaterialTheme.colorScheme.primary,
            disabledLabelColor = MaterialTheme.colorScheme.onPrimary,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        AlertDialog(
            onDismissRequest = { setShowDatePicker(false) },
            confirmButton = {
                Button(onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis ?: return@Button
                    val localDate = Instant.ofEpochMilli(selectedMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    onDateSelected(localDate)
                    setShowDatePicker(false)
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(onClick = { setShowDatePicker(false) }) {
                    Text("Cancelar")
                }
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    DatePicker(state = datePickerState)
                }
            }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegisterButton(registerViewModel: RegisterViewModel) {
    Button(
        onClick = { registerViewModel.onRegisterSelected() },
        enabled = true,
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


