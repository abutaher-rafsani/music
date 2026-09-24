package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.remote.AddressData
import com.example.data.remote.LoginRequest
import com.example.data.remote.RegisterRequest
import com.example.ui.KliqViewModel
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    onResetDemoData: () -> Unit,
    onShowOtpReset: () -> Unit,
    viewModel: KliqViewModel = viewModel()
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var currentStep by remember { mutableIntStateOf(1) } // 1 to 4

    // Login Form State
    var loginIdentifier by remember { mutableStateOf("+8801711223344") }
    var loginPassword by remember { mutableStateOf("123456") }
    var showLoginPassword by remember { mutableStateOf(false) }

    // Step 1: Mandatory Fields
    var fullName by remember { mutableStateOf("তানভীর আহমেদ") }
    var username by remember { mutableStateOf("tanvir_" + (100..999).random()) }
    var phone by remember { mutableStateOf("+8801711223344") }
    var email by remember { mutableStateOf("tanvir@kliq.app") }
    var registerPassword by remember { mutableStateOf("123456") }
    var showRegisterPassword by remember { mutableStateOf(false) }

    // Step 2: Address Information
    val divisions = listOf("ঢাকা", "চট্টগ্রাম", "রাজশাহী", "খুলনা", "সিলেট", "বরিশাল", "রংপুর", "ময়মনসিংহ")
    var selectedDivision by remember { mutableStateOf("ঢাকা") }
    var district by remember { mutableStateOf("ঢাকা") }
    var upazila by remember { mutableStateOf("ধানমন্ডি") }
    var unionCity by remember { mutableStateOf("ওয়ার্ড ১৫") }
    var villageWard by remember { mutableStateOf("রোড ৭/এ") }
    var addressDetails by remember { mutableStateOf("বাড়ি নং ৪২, ফ্ল্যাট ৩বি") }
    var sameAddress by remember { mutableStateOf(true) }

    var permDivision by remember { mutableStateOf("ঢাকা") }
    var permDistrict by remember { mutableStateOf("ঢাকা") }
    var permUpazila by remember { mutableStateOf("মিরপুর") }

    // Step 3: Education, Career & Personal
    val userTypes = listOf("ছাত্র / শিক্ষার্থী", "ফ্রিল্যান্সার", "চাকরিজীবী", "উদ্যোক্তা", "ডিজিটাল ক্রিয়েটর")
    var selectedUserType by remember { mutableStateOf("ছাত্র / শিক্ষার্থী") }
    var jobInfo by remember { mutableStateOf("ফুল-স্ট্যাক ডেভেলপার ও স্কাউট অডিটর") }
    var university by remember { mutableStateOf("ঢাকা বিশ্ববিদ্যালয়") }
    var college by remember { mutableStateOf("নটর ডেম কলেজ") }
    var school by remember { mutableStateOf("আইডিয়াল স্কুল") }
    var dob by remember { mutableStateOf("2001-08-15") }
    var gender by remember { mutableStateOf("male") }
    val availableInterests = listOf("প্রযুক্তি", "ফ্রিল্যান্সিং", "মাইক্রো-টাস্ক", "ভিডিও মেকিং", "কোডিং", "ডিজাইন", "মার্কেট অডিট")
    var selectedInterests by remember { mutableStateOf(listOf("প্রযুক্তি", "মাইক্রো-টাস্ক", "কোডিং")) }

    // Step 4: Profile, Settings & Refer
    val presetAvatars = listOf(
        "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=400&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=400&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1580489944761-15a19d654956?w=400&auto=format&fit=crop&q=80"
    )
    var selectedAvatarUrl by remember { mutableStateOf(presetAvatars[0]) }
    var bio by remember { mutableStateOf("🚀 KLIQ ক্রিয়েটর ও ভেরিফাইড স্কাউট লিডার | ঢাকা") }
    var refCode by remember { mutableStateOf("") }
    var contactSyncEnabled by remember { mutableStateOf(true) }
    var acceptedTerms by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Video / Graphic Hero Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=1200&auto=format&fit=crop&q=80",
                contentDescription = "Hero Cover",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dark gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                ObsidianBlack.copy(alpha = 0.6f),
                                ObsidianBlack
                            )
                        )
                    )
            )

            // Frosted floating KLIQ branding capsule
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color(0xCC0E0E17))
                    .border(1.5.dp, CardGlowBorder, RoundedCornerShape(30.dp))
                    .padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "KLIQ",
                        color = IceWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "BD",
                        color = ElectricCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // 2. ৳50 Instant Signup Incentive Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0x3300D9FF), Color(0x337928CA), Color(0x33FF00A8))
                    )
                )
                .border(1.dp, ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(GoldCoin.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "৳", color = GoldCoin, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "৳৫০ ইনস্ট্যান্ট সাইনআপ বোনাস",
                        color = IceWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "রেজিস্ট্রেশন করলেই ওয়ালেটে ৫০ টাকা ক্যাশ + ২০০০ এনগেজমেন্ট কয়েন!",
                        color = MetallicSilver,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Tab Segment: Login vs Multi-Step Register
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(ObsidianSurface)
                .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (!isRegisterMode) ElectricCyan.copy(alpha = 0.18f) else Color.Transparent)
                    .clickable { isRegisterMode = false }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "লগইন করুন (Sign In)",
                    color = if (!isRegisterMode) ElectricCyan else TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isRegisterMode) NeonPurple.copy(alpha = 0.25f) else Color.Transparent)
                    .clickable { isRegisterMode = true }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "৪-ধাপে রেজিস্ট্রেশন (Join)",
                    color = if (isRegisterMode) IceWhite else TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Error message if any
        if (viewModel.authErrorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x33FF0055))
                    .border(1.dp, Color(0xFFFF0055), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFFF4081), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = viewModel.authErrorMessage!!, color = Color.White, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (!isRegisterMode) {
            // ==========================================
            // LOGIN FORM
            // ==========================================
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                hasNeonAccent = true
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "লগইন করুন",
                        color = IceWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "মোবাইল নাম্বার, ইউজারনেম বা ইমেইল দিয়ে প্রবেশ করুন",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = loginIdentifier,
                        onValueChange = { loginIdentifier = it },
                        label = { Text("মোবাইল / ইউজারনেম / ইমেইল", color = TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = ElectricCyan)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = IceWhite,
                            unfocusedTextColor = IceWhite,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedContainerColor = ObsidianSurface,
                            unfocusedContainerColor = ObsidianSurface
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_identifier_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = loginPassword,
                        onValueChange = { loginPassword = it },
                        label = { Text("পাসওয়ার্ড", color = TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = ElectricCyan)
                        },
                        trailingIcon = {
                            IconButton(onClick = { showLoginPassword = !showLoginPassword }) {
                                Icon(
                                    if (showLoginPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password",
                                    tint = TextMuted
                                )
                            }
                        },
                        visualTransformation = if (showLoginPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = IceWhite,
                            unfocusedTextColor = IceWhite,
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = ObsidianBorder,
                            focusedContainerColor = ObsidianSurface,
                            unfocusedContainerColor = ObsidianSurface
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "পাসওয়ার্ড ভুলে গেছেন?",
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable { onShowOtpReset() }
                                .padding(vertical = 4.dp)
                        )

                        Text(
                            text = "নতুন একাউন্ট খুলুন ➔",
                            color = NeonPurple,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { isRegisterMode = true; currentStep = 1 }
                                .padding(vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    PillButton(
                        text = if (viewModel.isAuthLoading) "লগইন হচ্ছে..." else "লগইন করুন 🚀",
                        onClick = {
                            viewModel.loginUser(
                                LoginRequest(identifier = loginIdentifier, password = loginPassword),
                                onSuccess = onLoginSuccess
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_submit_button")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Demo Credentials Pill
                    Text(
                        text = "দ্রুত ডেমো লগইন:",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x2200D9FF))
                                .border(1.dp, ElectricCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .clickable {
                                    loginIdentifier = "+8801711223344"
                                    loginPassword = "••••••••"
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+8801711223344", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x227928CA))
                                .border(1.dp, NeonPurple.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .clickable {
                                    loginIdentifier = "tanvir_kliq"
                                    loginPassword = "••••••••"
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("tanvir_kliq", color = IceWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        } else {
            // ==========================================
            // MULTI-STEP REGISTRATION FORM (Steps 1 to 4)
            // ==========================================
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                hasNeonAccent = true
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Step Progress Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (currentStep) {
                                1 -> "ধাপ ১: প্রাথমিক তথ্য"
                                2 -> "ধাপ ২: NID ও ঠিকানা"
                                3 -> "ধাপ ৩: ক্যারিয়ার ও শিক্ষা"
                                else -> "ধাপ ৪: প্রোফাইল ও রেফার"
                            },
                            color = IceWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "ধাপ $currentStep / ৪",
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (step in 1..4) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (step <= currentStep) ElectricCyan else ObsidianBorder
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    when (currentStep) {
                        // ----------------------------------------------------
                        // STEP 1: Mandatory Fields
                        // ----------------------------------------------------
                        1 -> {
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = { Text("পুরো নাম (Full Name) *", color = TextMuted) },
                                leadingIcon = {
                                    Icon(Icons.Default.Badge, contentDescription = null, tint = ElectricCyan)
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = IceWhite,
                                    unfocusedTextColor = IceWhite,
                                    focusedBorderColor = ElectricCyan,
                                    unfocusedBorderColor = ObsidianBorder,
                                    focusedContainerColor = ObsidianSurface,
                                    unfocusedContainerColor = ObsidianSurface
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().testTag("reg_name_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Live Username Availability Check
                            OutlinedTextField(
                                value = username,
                                onValueChange = {
                                    username = it.lowercase().replace(Regex("[^a-z0-9_]"), "")
                                    if (username.length >= 3) {
                                        viewModel.checkUsername(username)
                                    }
                                },
                                label = { Text("ইউজারনেম (Username) *", color = TextMuted) },
                                leadingIcon = {
                                    Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = ElectricCyan)
                                },
                                trailingIcon = {
                                    if (viewModel.isCheckingUsername) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = ElectricCyan, strokeWidth = 2.dp)
                                    } else if (viewModel.usernameCheckResult != null) {
                                        Icon(
                                            if (viewModel.usernameCheckResult!!.available) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                            contentDescription = null,
                                            tint = if (viewModel.usernameCheckResult!!.available) SuccessGreen else Color(0xFFFF3366)
                                        )
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = IceWhite,
                                    unfocusedTextColor = IceWhite,
                                    focusedBorderColor = ElectricCyan,
                                    unfocusedBorderColor = ObsidianBorder,
                                    focusedContainerColor = ObsidianSurface,
                                    unfocusedContainerColor = ObsidianSurface
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().testTag("reg_username_input")
                            )

                            // Username availability message pill
                            if (viewModel.usernameCheckResult != null) {
                                Text(
                                    text = viewModel.usernameCheckResult!!.message,
                                    color = if (viewModel.usernameCheckResult!!.available) SuccessGreen else Color(0xFFFF3366),
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("মোবাইল নাম্বার *", color = TextMuted) },
                                leadingIcon = {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = ElectricCyan)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = IceWhite,
                                    unfocusedTextColor = IceWhite,
                                    focusedBorderColor = ElectricCyan,
                                    unfocusedBorderColor = ObsidianBorder,
                                    focusedContainerColor = ObsidianSurface,
                                    unfocusedContainerColor = ObsidianSurface
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().testTag("reg_phone_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("ইমেইল (Email)", color = TextMuted) },
                                leadingIcon = {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = ElectricCyan)
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = IceWhite,
                                    unfocusedTextColor = IceWhite,
                                    focusedBorderColor = ElectricCyan,
                                    unfocusedBorderColor = ObsidianBorder,
                                    focusedContainerColor = ObsidianSurface,
                                    unfocusedContainerColor = ObsidianSurface
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().testTag("reg_email_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = registerPassword,
                                onValueChange = { registerPassword = it },
                                label = { Text("পাসওয়ার্ড (কমপক্ষে ৬ অক্ষর) *", color = TextMuted) },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = ElectricCyan)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { showRegisterPassword = !showRegisterPassword }) {
                                        Icon(
                                            if (showRegisterPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = null,
                                            tint = TextMuted
                                        )
                                    }
                                },
                                visualTransformation = if (showRegisterPassword) VisualTransformation.None else PasswordVisualTransformation(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = IceWhite,
                                    unfocusedTextColor = IceWhite,
                                    focusedBorderColor = ElectricCyan,
                                    unfocusedBorderColor = ObsidianBorder,
                                    focusedContainerColor = ObsidianSurface,
                                    unfocusedContainerColor = ObsidianSurface
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth().testTag("reg_password_input")
                            )
                        }

                        // ----------------------------------------------------
                        // STEP 2: Address Information
                        // ----------------------------------------------------
                        2 -> {
                            Text(
                                text = "বর্তমান ঠিকানা (Present Address)",
                                color = ElectricCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Division Selector
                            Text(text = "বিভাগ নির্বাচন করুন:", color = TextMuted, fontSize = 11.sp)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                divisions.forEach { div ->
                                    val isSelected = selectedDivision == div
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) ElectricCyan else ObsidianSurface)
                                            .border(1.dp, if (isSelected) ElectricCyan else ObsidianBorder, RoundedCornerShape(10.dp))
                                            .clickable { selectedDivision = div }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = div,
                                            color = if (isSelected) ObsidianBlack else IceWhite,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = district,
                                    onValueChange = { district = it },
                                    label = { Text("জেলা", color = TextMuted) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = IceWhite,
                                        unfocusedTextColor = IceWhite,
                                        focusedContainerColor = ObsidianSurface,
                                        unfocusedContainerColor = ObsidianSurface
                                    )
                                )
                                OutlinedTextField(
                                    value = upazila,
                                    onValueChange = { upazila = it },
                                    label = { Text("উপজেলা / থানা", color = TextMuted) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = IceWhite,
                                        unfocusedTextColor = IceWhite,
                                        focusedContainerColor = ObsidianSurface,
                                        unfocusedContainerColor = ObsidianSurface
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = unionCity,
                                    onValueChange = { unionCity = it },
                                    label = { Text("ইউনিয়ন / সিটি", color = TextMuted) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = IceWhite,
                                        unfocusedTextColor = IceWhite,
                                        focusedContainerColor = ObsidianSurface,
                                        unfocusedContainerColor = ObsidianSurface
                                    )
                                )
                                OutlinedTextField(
                                    value = villageWard,
                                    onValueChange = { villageWard = it },
                                    label = { Text("গ্রাম / ওয়ার্ড", color = TextMuted) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = IceWhite,
                                        unfocusedTextColor = IceWhite,
                                        focusedContainerColor = ObsidianSurface,
                                        unfocusedContainerColor = ObsidianSurface
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = addressDetails,
                                onValueChange = { addressDetails = it },
                                label = { Text("বিস্তারিত ঠিকানা (রাস্তা, বাড়ি নং)", color = TextMuted) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = IceWhite,
                                    unfocusedTextColor = IceWhite,
                                    focusedContainerColor = ObsidianSurface,
                                    unfocusedContainerColor = ObsidianSurface
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Same Address Toggle
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { sameAddress = !sameAddress },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = sameAddress,
                                    onCheckedChange = { sameAddress = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = ElectricCyan,
                                        uncheckedColor = TextMuted
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "স্থায়ী ঠিকানা ও বর্তমান ঠিকানা একই",
                                    color = IceWhite,
                                    fontSize = 13.sp
                                )
                            }

                            if (!sameAddress) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "স্থায়ী ঠিকানা (Permanent Address)",
                                    color = NeonPurple,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = permDistrict,
                                        onValueChange = { permDistrict = it },
                                        label = { Text("স্থায়ী জেলা", color = TextMuted) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = IceWhite,
                                            unfocusedTextColor = IceWhite,
                                            focusedContainerColor = ObsidianSurface,
                                            unfocusedContainerColor = ObsidianSurface
                                        )
                                    )
                                    OutlinedTextField(
                                        value = permUpazila,
                                        onValueChange = { permUpazila = it },
                                        label = { Text("স্থায়ী উপজেলা", color = TextMuted) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = IceWhite,
                                            unfocusedTextColor = IceWhite,
                                            focusedContainerColor = ObsidianSurface,
                                            unfocusedContainerColor = ObsidianSurface
                                        )
                                    )
                                }
                            }
                        }

                        // ----------------------------------------------------
                        // STEP 3: Education, Career & Personal
                        // ----------------------------------------------------
                        3 -> {
                            Text(text = "ব্যবহারকারীর ধরন:", color = TextMuted, fontSize = 11.sp)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                userTypes.forEach { type ->
                                    val isSelected = selectedUserType == type
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) NeonPurple else ObsidianSurface)
                                            .border(1.dp, if (isSelected) NeonPurple else ObsidianBorder, RoundedCornerShape(10.dp))
                                            .clickable { selectedUserType = type }
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = type,
                                            color = if (isSelected) IceWhite else TextMuted,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = jobInfo,
                                onValueChange = { jobInfo = it },
                                label = { Text("পেশা / পদবী (Job Info)", color = TextMuted) },
                                leadingIcon = { Icon(Icons.Default.Work, contentDescription = null, tint = ElectricCyan) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = IceWhite,
                                    unfocusedTextColor = IceWhite,
                                    focusedContainerColor = ObsidianSurface,
                                    unfocusedContainerColor = ObsidianSurface
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = university,
                                onValueChange = { university = it },
                                label = { Text("বিশ্ববিদ্যালয় (University)", color = TextMuted) },
                                leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = ElectricCyan) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = IceWhite,
                                    unfocusedTextColor = IceWhite,
                                    focusedContainerColor = ObsidianSurface,
                                    unfocusedContainerColor = ObsidianSurface
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = college,
                                    onValueChange = { college = it },
                                    label = { Text("কলেজ", color = TextMuted) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = IceWhite,
                                        unfocusedTextColor = IceWhite,
                                        focusedContainerColor = ObsidianSurface,
                                        unfocusedContainerColor = ObsidianSurface
                                    )
                                )
                                OutlinedTextField(
                                    value = school,
                                    onValueChange = { school = it },
                                    label = { Text("স্কুল", color = TextMuted) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = IceWhite,
                                        unfocusedTextColor = IceWhite,
                                        focusedContainerColor = ObsidianSurface,
                                        unfocusedContainerColor = ObsidianSurface
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Gender & DOB
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = dob,
                                    onValueChange = { dob = it },
                                    label = { Text("জন্মতারিখ (YYYY-MM-DD)", color = TextMuted) },
                                    modifier = Modifier.weight(1.2f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = IceWhite,
                                        unfocusedTextColor = IceWhite,
                                        focusedContainerColor = ObsidianSurface,
                                        unfocusedContainerColor = ObsidianSurface
                                    )
                                )

                                Row(
                                    modifier = Modifier.weight(1.3f),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    listOf("male" to "পুরুষ", "female" to "নারী").forEach { (genKey, genLabel) ->
                                        val isGen = gender == genKey
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isGen) ElectricCyan.copy(alpha = 0.2f) else ObsidianSurface)
                                                .border(1.dp, if (isGen) ElectricCyan else ObsidianBorder, RoundedCornerShape(10.dp))
                                                .clickable { gender = genKey }
                                                .padding(vertical = 12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = genLabel, color = if (isGen) ElectricCyan else TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Interests chips
                            Text(text = "আগ্রহের বিষয়সমূহ:", color = TextMuted, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                availableInterests.forEach { interest ->
                                    val isSelected = selectedInterests.contains(interest)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) Color(0x3300D9FF) else ObsidianSurface)
                                            .border(1.dp, if (isSelected) ElectricCyan else ObsidianBorder, RoundedCornerShape(10.dp))
                                            .clickable {
                                                selectedInterests = if (isSelected) {
                                                    selectedInterests - interest
                                                } else {
                                                    selectedInterests + interest
                                                }
                                            }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = interest,
                                            color = if (isSelected) ElectricCyan else TextMuted,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }

                        // ----------------------------------------------------
                        // STEP 4: Profile, Settings & Refer
                        // ----------------------------------------------------
                        4 -> {
                            Text(text = "প্রোফাইল অ্যাভাটার নির্বাচন:", color = TextMuted, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                presetAvatars.forEach { avatar ->
                                    val isSelected = selectedAvatarUrl == avatar
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, if (isSelected) ElectricCyan else Color.Transparent, CircleShape)
                                            .clickable { selectedAvatarUrl = avatar }
                                    ) {
                                        AsyncImage(
                                            model = avatar,
                                            contentDescription = "Avatar",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = bio,
                                onValueChange = { bio = it },
                                label = { Text("বায়ো / নিজের পরিচয়", color = TextMuted) },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = ElectricCyan) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = IceWhite,
                                    unfocusedTextColor = IceWhite,
                                    focusedContainerColor = ObsidianSurface,
                                    unfocusedContainerColor = ObsidianSurface
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = refCode,
                                onValueChange = { refCode = it.uppercase() },
                                label = { Text("কারো রেফারেল কোড থাকলে দিন (ঐচ্ছিক)", color = TextMuted) },
                                leadingIcon = { Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = GoldCoin) },
                                placeholder = { Text("e.g. TAN8492", color = TextSubtle) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = IceWhite,
                                    unfocusedTextColor = IceWhite,
                                    focusedContainerColor = ObsidianSurface,
                                    unfocusedContainerColor = ObsidianSurface
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Contact Sync Toggle
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ObsidianSurface)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Contacts, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(text = "ফোনবুক কন্টাক্ট কানেক্ট করুন", color = IceWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        Text(text = "পরিচিত বন্ধুদের খুঁজে পেতে অটো-সিঙ্ক", color = TextMuted, fontSize = 10.sp)
                                    }
                                }
                                Switch(
                                    checked = contactSyncEnabled,
                                    onCheckedChange = { contactSyncEnabled = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = IceWhite,
                                        checkedTrackColor = ElectricCyan
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Terms Acceptance
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { acceptedTerms = !acceptedTerms },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = acceptedTerms,
                                    onCheckedChange = { acceptedTerms = it },
                                    colors = CheckboxDefaults.colors(checkedColor = ElectricCyan, uncheckedColor = TextMuted)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "আমি KLIQ এর টার্মস ও প্রাইভেসি পলিসি মেনে নিচ্ছি",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Step Navigation Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (currentStep > 1) {
                            OutlinedButton(
                                onClick = { currentStep-- },
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = IceWhite),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("পূর্ববর্তী")
                            }
                        }

                        if (currentStep < 4) {
                            PillButton(
                                text = "পরবর্তী ধাপ ➔",
                                onClick = {
                                    if (currentStep == 1) {
                                        if (fullName.isBlank() || username.isBlank() || phone.isBlank() || registerPassword.length < 6) {
                                            viewModel.showToast("অনুগ্রহ করে ধাপ ১ এর সঠিক তথ্য পূরণ করুন!")
                                            return@PillButton
                                        }
                                    }
                                    currentStep++
                                },
                                modifier = Modifier.weight(1.5f)
                            )
                        } else {
                            PillButton(
                                text = if (viewModel.isAuthLoading) "অ্যাকাউন্ট তৈরি হচ্ছে..." else "রেজিস্ট্রেশন ও ৳৫০ বোনাস নিন 🎁",
                                onClick = {
                                    if (!acceptedTerms) {
                                        viewModel.showToast("টার্মস ও কন্ডিশন গ্রহণ করুন!")
                                        return@PillButton
                                    }

                                    val request = RegisterRequest(
                                        fullName = fullName,
                                        username = username,
                                        phone = phone,
                                        email = email,
                                        password = registerPassword,
                                        presentAddress = AddressData(
                                            division = selectedDivision,
                                            district = district,
                                            upazila = upazila,
                                            unionCity = unionCity,
                                            villageWard = villageWard,
                                            details = addressDetails
                                        ),
                                        permanentAddress = if (sameAddress) null else AddressData(
                                            division = permDivision,
                                            district = permDistrict,
                                            upazila = permUpazila
                                        ),
                                        sameAddress = sameAddress,
                                        jobInfo = jobInfo,
                                        university = university,
                                        college = college,
                                        school = school,
                                        dob = dob,
                                        gender = gender,
                                        interests = selectedInterests,
                                        profilePhotoUrl = selectedAvatarUrl,
                                        bio = bio,
                                        userType = selectedUserType,
                                        contactSyncEnabled = contactSyncEnabled,
                                        refCode = refCode,
                                        acceptedTerms = acceptedTerms
                                    )

                                    viewModel.registerUser(request, onSuccess = onLoginSuccess)
                                },
                                modifier = Modifier
                                    .weight(2f)
                                    .testTag("reg_submit_button")
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. Ecosystem Highlights Showcase
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "KLIQ প্লাটফর্ম ফিচারসমূহ",
                color = MetallicSilver,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column {
                        Text(text = "🔍", fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Scout Gigs", color = IceWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "লোকাল অডিট ও শপ ভেরিফিকেশন বাউন্টি", color = TextMuted, fontSize = 10.sp)
                    }
                }

                GlassCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column {
                        Text(text = "💸", fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Instant Cashout", color = IceWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "বিকাশ ও নগদে ১-ট্যাপে ইনস্ট্যান্ট উইথড্রল", color = TextMuted, fontSize = 10.sp)
                    }
                }

                GlassCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column {
                        Text(text = "📱", fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Connector", color = IceWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "কন্টাক্ট ম্যাচিং ও .vcf ফোনবুক এক্সপোর্ট", color = TextMuted, fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 5. Testing & Reset Button
        Button(
            onClick = onResetDemoData,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E1020),
                contentColor = NeonHotPink
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .border(1.dp, NeonHotPink.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .testTag("reset_demo_data_button")
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ডাটাবেজ ডেমো ডাটা রিসেট করুন",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
