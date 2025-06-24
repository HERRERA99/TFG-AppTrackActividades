package com.aitor.trackactividades.authentication

import com.aitor.trackactividades.core.utils.isPasswordSecure
import com.aitor.trackactividades.core.utils.isUsernameValid
import org.junit.Test
import org.junit.jupiter.api.Assertions.*

class RegisterViewModelTest {

    // --- isPasswordSecure ---
    @Test
    fun `P1_a password with uppercase, lowercase and digits is valid`() {
        assertTrue(isPasswordSecure("Password123"))
    }

    @Test
    fun `P1_b password too short and lacks uppercase is invalid`() {
        assertFalse(isPasswordSecure("pass123"))
    }

    @Test
    fun `P1_c password without lowercase is invalid`() {
        assertFalse(isPasswordSecure("PASSWORD123"))
    }

    @Test
    fun `P1_d password without digits is invalid`() {
        assertFalse(isPasswordSecure("Password"))
    }

    @Test
    fun `P1_e valid mixed password`() {
        assertTrue(isPasswordSecure("Pass1234"))
    }

    // --- isUsernameValid ---
    @Test
    fun `U2_a valid username with underscore`() {
        assertTrue(isUsernameValid("user_name"))
    }

    @Test
    fun `U2_b valid username with hyphen`() {
        assertTrue(isUsernameValid("user-name"))
    }

    @Test
    fun `U2_c username with space is invalid`() {
        assertFalse(isUsernameValid("user name"))
    }

    @Test
    fun `U2_d username too short is invalid`() {
        assertFalse(isUsernameValid("us"))
    }

    @Test
    fun `U2_e username too long is invalid`() {
        assertFalse(isUsernameValid("asdasdadadasdasdadasad"))
    }

    @Test
    fun `U2_f username with invalid character is invalid`() {
        assertFalse(isUsernameValid("user@name"))
    }

    @Test
    fun `U2_g valid alphanumeric username`() {
        assertTrue(isUsernameValid("user123"))
    }
}