package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.RegisterRequest
import com.example.data.remote.AddressData
import com.example.ui.KliqViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    viewModel: KliqViewModel,
    onNavigateBackToLogin: () -> Unit,
    onRegistrationSuccess: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var refCode by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("ছাত্র / শিক্ষার্থী") }
    var passwordVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    val isLoading = viewModel.isAuthLoading
    val viewModelError = viewModel.authErrorMessage
    val usernameCheckResult = viewModel.usernameCheckResult
    val isCheckingUsername = viewModel.isCheckingUsername

    val activeError = localError ?: viewModelError

    LaunchedEffect(username) {
        if (username.length >= 3) {
            viewModel.checkUsername(username)
        }
    }

    val obsidianBg = Color(0xFF0F172A)
    val obsidianCard = Color(0xFF1E293B)
    val obsidianPrimary = Color(0xFF38BDF8)
    val obsidianText = Color(0xFFF8FAFC)
    val obsidianMuted = Color(0xFF94A3B8)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(obsidianBg)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = null,
                tint = obsidianPrimary,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "নতুন অ্যাকাউন্ট তৈরি করুন",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = obsidianText
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "রেজিস্ট্রেশন করলেই পান ৫০ টাকা বোনাস!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = obsidianMuted
                )
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Card container
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = obsidianCard)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (activeError != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = activeError,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Full Name
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("পূর্ণ নাম (Full Name)") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = obsidianPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = obsidianPrimary,
                            unfocusedBorderColor = obsidianMuted,
                            focusedLabelColor = obsidianPrimary,
                            unfocusedLabelColor = obsidianMuted,
                            focusedTextColor = obsidianText,
                            unfocusedTextColor = obsidianText
                        )
                    )

                    // Username
                    OutlinedTextField(
                        value = username,
                        onValueChange = { 
                            username = it.lowercase().replace(Regex("[^a-z0-9_]"), "")
                            if (username.length >= 3) {
                                viewModel.checkUsername(username)
                            }
                        },
                        label = { Text("ইউজারনেম (Username)") },
                        leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = obsidianPrimary) },
                        trailingIcon = {
                            if (username.length >= 3) {
                                if (isCheckingUsername) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = obsidianPrimary)
                                } else if (usernameCheckResult != null) {
                                    Icon(
                                        imageVector = if (usernameCheckResult.available) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                        contentDescription = null,
                                        tint = if (usernameCheckResult.available) Color.Green else Color.Red
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = obsidianPrimary,
                            unfocusedBorderColor = obsidianMuted,
                            focusedLabelColor = obsidianPrimary,
                            unfocusedLabelColor = obsidianMuted,
                            focusedTextColor = obsidianText,
                            unfocusedTextColor = obsidianText
                        )
                    )

                    // Phone
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("মোবাইল নম্বর (Phone)") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = obsidianPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = obsidianPrimary,
                            unfocusedBorderColor = obsidianMuted,
                            focusedLabelColor = obsidianPrimary,
                            unfocusedLabelColor = obsidianMuted,
                            focusedTextColor = obsidianText,
                            unfocusedTextColor = obsidianText
                        )
                    )

                    // Email (Optional)
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("ইমেইল (ঐচ্ছিক)") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = obsidianPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = obsidianPrimary,
                            unfocusedBorderColor = obsidianMuted,
                            focusedLabelColor = obsidianPrimary,
                            unfocusedLabelColor = obsidianMuted,
                            focusedTextColor = obsidianText,
                            unfocusedTextColor = obsidianText
                        )
                    )

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("পাসওয়ার্ড (কমপক্ষে ৬ অক্ষর)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = obsidianPrimary) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = obsidianMuted
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = obsidianPrimary,
                            unfocusedBorderColor = obsidianMuted,
                            focusedLabelColor = obsidianPrimary,
                            unfocusedLabelColor = obsidianMuted,
                            focusedTextColor = obsidianText,
                            unfocusedTextColor = obsidianText
                        )
                    )

                    // Referral Code
                    OutlinedTextField(
                        value = refCode,
                        onValueChange = { refCode = it },
                        label = { Text("রেফারেল কোড (ঐচ্ছিক)") },
                        leadingIcon = { Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = obsidianPrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = obsidianPrimary,
                            unfocusedBorderColor = obsidianMuted,
                            focusedLabelColor = obsidianPrimary,
                            unfocusedLabelColor = obsidianMuted,
                            focusedTextColor = obsidianText,
                            unfocusedTextColor = obsidianText
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Register Button
                    Button(
                        onClick = {
                            if (fullName.isBlank() || username.isBlank() || phone.isBlank() || password.length < 6) {
                                localError = "সব তথ্য সঠিকভাবে পূরণ করুন (পাসওয়ার্ড কমপক্ষে ৬ অক্ষর)!"
                                return@Button
                            }
                            localError = null
                            val request = RegisterRequest(
                                fullName = fullName,
                                username = username,
                                phone = phone,
                                email = email,
                                password = password,
                                refCode = refCode,
                                userType = userType,
                                presentAddress = AddressData(division = "ঢাকা", district = "ঢাকা"),
                                acceptedTerms = true
                            )
                            viewModel.registerUser(request, onSuccess = onRegistrationSuccess)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = obsidianPrimary),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = "অ্যাকাউন্ট খুলুন ও বোনাস নিন",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            )
                        }
                    }

                    // Back to login
                    TextButton(
                        onClick = onNavigateBackToLogin,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "ইতোমধ্যে অ্যাকাউন্ট আছে? লগইন করুন",
                            color = obsidianPrimary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
