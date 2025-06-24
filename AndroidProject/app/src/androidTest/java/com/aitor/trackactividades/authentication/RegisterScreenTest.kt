package com.aitor.trackactividades.authentication

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.aitor.trackactividades.authentication.domain.RegisterUseCase
import com.aitor.trackactividades.authentication.presentation.RegisterScreen
import com.aitor.trackactividades.authentication.presentation.RegisterViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not

class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var mockRegisterUseCase: RegisterUseCase
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setup() {
        mockRegisterUseCase = mockk()
        viewModel = RegisterViewModel(mockRegisterUseCase)
    }

    @Test
    fun testSuccessfulRegistration() = runTest {
        // Mock successful registration
        coEvery { mockRegisterUseCase(any()) } returns mockk()

        composeTestRule.setContent {
            RegisterScreen(
                registerViewModel = viewModel,
                navigateToLogin = {}
            )
        }

        // Fill in all fields
        composeTestRule.onNodeWithTag("usernameField").performTextInput("testuser")
        composeTestRule.onNodeWithTag("emailField").performTextInput("test@example.com")
        composeTestRule.onNodeWithTag("passwordField").performTextInput("Password123")
        composeTestRule.onNodeWithTag("repeatPasswordField").performTextInput("Password123")
        composeTestRule.onNodeWithTag("nameField").performTextInput("Test")
        composeTestRule.onNodeWithTag("surnameField").performTextInput("User")
        composeTestRule.onNodeWithTag("weightField").performTextInput("70")
        composeTestRule.onNodeWithTag("heightField").performTextInput("170")

        // Click register button
        composeTestRule.onNodeWithTag("registerButton").performClick()

        testScheduler.advanceUntilIdle()

        // Verify success message
        composeTestRule.onNodeWithText("Verifica la cuenta con el email recibido.").assertIsDisplayed()
    }

    @Test
    fun testEmailInvalido() = runTest {
        coEvery { mockRegisterUseCase(any()) } returns mockk()

        composeTestRule.setContent {
            RegisterScreen(
                registerViewModel = viewModel,
                navigateToLogin = {}
            )
        }

        composeTestRule.onNodeWithTag("usernameField").performTextInput("testuser")
        composeTestRule.onNodeWithTag("emailField").performTextInput("test@.com")
        composeTestRule.onNodeWithTag("passwordField").performTextInput("12345678Aa")
        composeTestRule.onNodeWithTag("repeatPasswordField").performTextInput("12345678Aa")
        composeTestRule.onNodeWithTag("nameField").performTextInput("Test")
        composeTestRule.onNodeWithTag("surnameField").performTextInput("User")
        composeTestRule.onNodeWithTag("dateField").performClick()
        composeTestRule.onNodeWithTag("weightField").performTextInput("70")
        composeTestRule.onNodeWithTag("heightField").performTextInput("170")

        // Click register button
        composeTestRule.onNodeWithTag("registerButton").performClick()

        composeTestRule.onNodeWithTag("textoInfo").assertTextEquals("El correo electrónico no es válido. \n")
    }

    @Test
    fun testUsernameInvalido() = runTest {
        coEvery { mockRegisterUseCase(any()) } returns mockk()

        composeTestRule.setContent {
            RegisterScreen(
                registerViewModel = viewModel,
                navigateToLogin = {}
            )
        }

        composeTestRule.onNodeWithTag("usernameField").performTextInput("a")
        composeTestRule.onNodeWithTag("emailField").performTextInput("test@gmail.com")
        composeTestRule.onNodeWithTag("passwordField").performTextInput("12345678Aa")
        composeTestRule.onNodeWithTag("repeatPasswordField").performTextInput("12345678Aa")
        composeTestRule.onNodeWithTag("nameField").performTextInput("Test")
        composeTestRule.onNodeWithTag("surnameField").performTextInput("User")
        composeTestRule.onNodeWithTag("weightField").performTextInput("70")
        composeTestRule.onNodeWithTag("heightField").performTextInput("170")

        // Click register button
        composeTestRule.onNodeWithTag("registerButton").performClick()

        composeTestRule.onNodeWithTag("textoInfo").assertTextEquals("El nombre de usuario no es válido. \n")
    }

    @Test
    fun testPasswordsNoCoinciden() = runTest {
        coEvery { mockRegisterUseCase(any()) } returns mockk()

        composeTestRule.setContent {
            RegisterScreen(
                registerViewModel = viewModel,
                navigateToLogin = {}
            )
        }

        composeTestRule.onNodeWithTag("usernameField").performTextInput("aitor")
        composeTestRule.onNodeWithTag("emailField").performTextInput("test@gmail.com")
        composeTestRule.onNodeWithTag("passwordField").performTextInput("12345678Aa")
        composeTestRule.onNodeWithTag("repeatPasswordField").performTextInput("kdfbheha129631A")
        composeTestRule.onNodeWithTag("nameField").performTextInput("Test")
        composeTestRule.onNodeWithTag("surnameField").performTextInput("User")
        composeTestRule.onNodeWithTag("weightField").performTextInput("70")
        composeTestRule.onNodeWithTag("heightField").performTextInput("170")

        // Click register button
        composeTestRule.onNodeWithTag("registerButton").performClick()

        composeTestRule.onNodeWithTag("textoInfo").assertTextEquals("Las contraseñas no coinciden. \n")
    }

    @Test
    fun testPasswordsFormatoIncorrecto() = runTest {
        coEvery { mockRegisterUseCase(any()) } returns mockk()

        composeTestRule.setContent {
            RegisterScreen(
                registerViewModel = viewModel,
                navigateToLogin = {}
            )
        }

        composeTestRule.onNodeWithTag("usernameField").performTextInput("aitor")
        composeTestRule.onNodeWithTag("emailField").performTextInput("test@gmail.com")
        composeTestRule.onNodeWithTag("passwordField").performTextInput("12")
        composeTestRule.onNodeWithTag("repeatPasswordField").performTextInput("12")
        composeTestRule.onNodeWithTag("nameField").performTextInput("Test")
        composeTestRule.onNodeWithTag("surnameField").performTextInput("User")
        composeTestRule.onNodeWithTag("weightField").performTextInput("70")
        composeTestRule.onNodeWithTag("heightField").performTextInput("170")

        // Click register button
        composeTestRule.onNodeWithTag("registerButton").performClick()

        composeTestRule.onNodeWithTag("textoInfo").assertTextEquals("Formato de contraseña incorrecto")
    }

    @Test
    fun testCamposVacios() = runTest {
        coEvery { mockRegisterUseCase(any()) } returns mockk()

        composeTestRule.setContent {
            RegisterScreen(
                registerViewModel = viewModel,
                navigateToLogin = {}
            )
        }

        // Click register button
        composeTestRule.onNodeWithTag("registerButton").performClick()

        composeTestRule.onNodeWithText("Todos los campos son obligatorios.").assertIsDisplayed()
    }
}
