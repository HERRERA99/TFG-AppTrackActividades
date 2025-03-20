package com.aitor.trackactividades.authentication.presentation

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.aitor.trackactividades.core.model.Gender
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
    val navigateToFeedState: Boolean by registerViewModel.navigateToFeed.observeAsState(initial = false)

    LaunchedEffect(navigateToFeedState) {
        if (navigateToFeedState) {
            navigateToFeed()
        }
    }

    val errorMessage: String? by registerViewModel.errorMessage.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()), // Agregar scroll
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
    val name: String by registerViewModel.firstname.observeAsState(initial = "")
    val username: String by registerViewModel.username.observeAsState(initial = "")
    val surname: String by registerViewModel.lastname.observeAsState(initial = "")
    val birthDate: LocalDate by registerViewModel.birthDate.observeAsState(initial = LocalDate.now())
    val gender: Gender by registerViewModel.gender.observeAsState(initial = Gender.MASCULINO)
    val weight: Double by registerViewModel.weight.observeAsState(initial = 0.0)
    val height: Int by registerViewModel.height.observeAsState(initial = 0)
    val textoInfo: String by registerViewModel.textoInfo.observeAsState(initial = "")

    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()), // Agregar scroll
        verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UsernameInput(
            text = "Usuario",
            variable = username,
            onTextChange = {
                registerViewModel.onRegisterChanged(
                    email,
                    it,
                    password1,
                    password2,
                    name,
                    surname,
                    birthDate,
                    gender,
                    weight,
                    height
                )
            }
        )

        EmailInput(
            email = email,
            register = true,
            onTextChange = {
                registerViewModel.onRegisterChanged(
                    it,
                    username,
                    password1,
                    password2,
                    name,
                    surname,
                    birthDate,
                    gender,
                    weight,
                    height
                )
            })
        PasswordInput(
            text = "Contraseña",
            password = password1,
            info = true,
            onTextChange = {
                registerViewModel.onRegisterChanged(
                    email,
                    username,
                    it,
                    password2,
                    name,
                    surname,
                    birthDate,
                    gender,
                    weight,
                    height
                )
            })
        PasswordInput(
            text = "Repite contraseña",
            password = password2,
            info = false,
            onTextChange = {
                registerViewModel.onRegisterChanged(
                    email,
                    username,
                    password1,
                    it,
                    name,
                    surname,
                    birthDate,
                    gender,
                    weight,
                    height
                )
            }
        )

        NameInput(
            text = "Nombre",
            variable = name,
            onTextChange = {
                registerViewModel.onRegisterChanged(
                    email,
                    username,
                    password1,
                    password2,
                    it,
                    surname,
                    birthDate,
                    gender,
                    weight,
                    height
                )
            }
        )

        NameInput(
            text = "Apellidos",
            variable = surname,
            onTextChange = {
                registerViewModel.onRegisterChanged(
                    email,
                    username,
                    password1,
                    password2,
                    name,
                    it,
                    birthDate,
                    gender,
                    weight,
                    height
                )
            }
        )


        DateOfBirthInput(
            selectedDate = birthDate,
            onDateSelected = {
                registerViewModel.onRegisterChanged(
                    email,
                    username,
                    password1,
                    password2,
                    name,
                    surname,
                    it,
                    gender,
                    weight,
                    height
                )
            },
            modifier = Modifier.fillMaxWidth(),
            showDatePicker = showDatePicker,
            setShowDatePicker =
            {
                showDatePicker = it
            }
        )


        GenderInput(
            selectedGender = gender,
            modifier = Modifier.fillMaxWidth(),
            onGenderSelected =
            {
                registerViewModel.onRegisterChanged(
                    email,
                    username,
                    password1,
                    password2,
                    name,
                    surname,
                    birthDate,
                    it,
                    weight,
                    height
                )
            }
        )

        WeightInput(
            weight = weight,
            onWeightChange = {
                registerViewModel.onRegisterChanged(
                    email,
                    username,
                    password1,
                    password2,
                    name,
                    surname,
                    birthDate,
                    gender,
                    it,
                    height
                )
            }
        )

        HeightInput(
            height = height,
            onHeightChange = {
                registerViewModel.onRegisterChanged(
                    email,
                    username,
                    password1,
                    password2,
                    name,
                    surname,
                    birthDate,
                    gender,
                    weight,
                    it
                )
            }
        )
    }

    Text(text = textoInfo, color = Color.Red)

    RegisterButton(
        registerViewModel = registerViewModel
    )
}

@Composable
fun WeightInput(
    weight: Double,
    onWeightChange: (Double) -> Unit
) {
    var textValue by remember { mutableStateOf(weight.takeIf { it > 0 }?.toString() ?: "") }

    OutlinedTextField(
        value = textValue,
        onValueChange = { newValue ->
            textValue = newValue
            val weightDouble = newValue.toDoubleOrNull()
            if (weightDouble != null && weightDouble > 0) {
                onWeightChange(weightDouble)
            } else if (newValue.isEmpty()) {
                onWeightChange(0.0) // Evita valores negativos pero permite borrar
            }
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = "Peso") },
        leadingIcon = { Icon(imageVector = Icons.Default.FitnessCenter, contentDescription = "Peso") },
        trailingIcon = { Text(text = "Kg", style = MaterialTheme.typography.bodyMedium) },
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        maxLines = 1,
        singleLine = true
    )
}


@Composable
fun HeightInput(
    height: Int,
    onHeightChange: (Int) -> Unit
) {
    OutlinedTextField(
        value = if (height > 0) height.toString() else "",
        onValueChange = { newValue ->
            val heightInt = newValue.toIntOrNull()
            if (heightInt != null && heightInt > 0) {
                onHeightChange(heightInt)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = "Altura") },
        leadingIcon = { Icon(imageVector = Icons.Default.Height, contentDescription = "Altura") },
        trailingIcon = { Text(text = "cm", style = MaterialTheme.typography.bodyMedium) },
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        maxLines = 1,
        singleLine = true
    )
}




@Composable
fun GenderInput(selectedGender: Gender, modifier: Modifier, onGenderSelected: (Gender) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val genderOptions = Gender.entries.toTypedArray()
    var gender by remember { mutableStateOf(selectedGender) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = gender.name,
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
                    text = { Text(option.name) },
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

@Composable
fun UsernameInput(text: String, variable: String, onTextChange: (String) -> Unit) {
    OutlinedTextField(
        value = variable,
        onValueChange = { onTextChange(it) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = text) },
        supportingText = { Text(text = "Solo letras, números, guiones bajos (_) y guiones (-).") },
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
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
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
        selectedDate?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) ?: "Selecciona fecha"

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
                BoxWithConstraints {
                    // 360 is minimum because DatePicker uses 12.dp horizontal padding and 48.dp for each week day
                    val scale =
                        remember(this.maxWidth) { if (this.maxWidth > 360.dp) 1f else (this.maxWidth / 360.dp) }
                    // Make sure there is always enough room, so use requiredWidthIn
                    Box(modifier = Modifier.requiredWidthIn(min = 360.dp)) {
                        // Scale in case the width is too large for the screen
                        DatePicker(
                            modifier = Modifier.scale(scale),
                            state = datePickerState,
                            title = null,
                            headline = null,
                            showModeToggle = false,
                        )
                    }
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


