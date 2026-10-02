package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlowingNatokFlixLogo
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.NatokGold
import com.example.ui.theme.NatokRed
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LoginScreen(
    onGoogleSignIn: () -> Unit,
    onFacebookSignIn: () -> Unit,
    onLoginSubmit: (usernameOrEmail: String, secretOrPass: String, rememberMe: Boolean) -> Unit,
    onSignUpSubmit: (name: String, email: String, phone: String, pass: String, refCode: String, gender: String, birthday: String) -> Unit,
    onSendResetOtp: (email: String, context: Context, callback: (success: Boolean, message: String, code: String) -> Unit) -> Unit = { _, _, _ -> },
    onResetUserPassword: (email: String, newPass: String, callback: (success: Boolean, message: String) -> Unit) -> Unit = { _, _, _ -> },
    loginError: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("natokflix_auth_prefs", Context.MODE_PRIVATE) }

    // 0 = Login (Existing User), 1 = Sign-Up (New User)
    var selectedAuthTab by remember { mutableIntStateOf(0) }

    // Remember Me State
    var rememberMe by remember { mutableStateOf(prefs.getBoolean("remember_me", true)) }

    // Terms & Conditions Agreement State ("I Agree" / "আমি সম্মত")
    var isTermsAgreed by remember { mutableStateOf(false) }
    var showTermsError by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    // Forgot Password & OTP State
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var resetStep by remember { mutableIntStateOf(1) } // 1 = Enter Email, 2 = Enter Code & New Password
    var resetEmailInput by remember { mutableStateOf("") }
    var resetCodeInput by remember { mutableStateOf("") }
    var newPasswordInput by remember { mutableStateOf("") }
    var confirmPasswordInput by remember { mutableStateOf("") }
    var generatedOtpCode by remember { mutableStateOf("") }
    var resetStatusMessage by remember { mutableStateOf<String?>(null) }
    var isResetError by remember { mutableStateOf(false) }

    // Login Form State (Starts completely empty unless saved credentials exist)
    var loginUsernameOrEmail by remember {
        mutableStateOf(if (rememberMe) prefs.getString("saved_email", "") ?: "" else "")
    }
    var loginPassword by remember {
        mutableStateOf(if (rememberMe) prefs.getString("saved_password", "") ?: "" else "")
    }

    // Sign-Up Form State
    var signUpSubmitted by remember { mutableStateOf(false) }
    var signUpStep by remember { mutableIntStateOf(1) } // 1 = Primary Info, 2 = Gender & Birthday
    var signUpName by remember { mutableStateOf("") }
    var signUpEmail by remember { mutableStateOf("") }
    var signUpPhone by remember { mutableStateOf("") }
    var signUpPassword by remember { mutableStateOf("") }
    var signUpReferralCode by remember { mutableStateOf("") }
    var signUpGender by remember { mutableStateOf("Boy") } // "Boy" or "Girl"
    var signUpBirthday by remember { mutableStateOf("15-08-2002") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2E090D),
                        BackgroundDark,
                        BackgroundDark
                    )
                )
            )
            .testTag("auth_screen_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Glowing 3D-styled NatokFlix Logo
            GlowingNatokFlixLogo(
                size = 80.dp,
                showText = false,
                enablePulse = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // App Brand Name: NatokFlix
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Natok",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "Flix",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NatokRed
                )
            }

            Text(
                text = "বাংলা নাটক ও শর্টস দেখুন, আয় করুন আনলিমিটেড",
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
            )

            // Auth Mode Toggle Tabs (Login vs Sign-Up)
            TabRow(
                selectedTabIndex = selectedAuthTab,
                containerColor = SurfaceCard,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedAuthTab]),
                        color = NatokRed,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedAuthTab == 0,
                    onClick = { selectedAuthTab = 0 },
                    text = {
                        Text(
                            text = "লগইন (Sign In)",
                            fontWeight = if (selectedAuthTab == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedAuthTab == 0) Color.White else TextSecondary
                        )
                    },
                    modifier = Modifier.testTag("tab_login")
                )
                Tab(
                    selected = selectedAuthTab == 1,
                    onClick = { selectedAuthTab = 1 },
                    text = {
                        Text(
                            text = "রেজিস্ট্রেশন (Sign Up)",
                            fontWeight = if (selectedAuthTab == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedAuthTab == 1) Color.White else TextSecondary
                        )
                    },
                    modifier = Modifier.testTag("tab_signup")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Social Logins (Google & Facebook)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Google Sign In Button
                Button(
                    onClick = onGoogleSignIn,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("google_auth_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4285F4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Google",
                            color = Color(0xFF1F1F1F),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Facebook Sign In Button
                Button(
                    onClick = onFacebookSignIn,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("facebook_auth_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "f",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Facebook",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f).height(1.dp).background(BorderDark))
                Text(
                    text = if (selectedAuthTab == 0) "অথবা একাউন্টে লগইন করুন" else "অথবা নতুন একাউন্ট তৈরি করুন",
                    fontSize = 11.sp,
                    color = Color(0xFF888899),
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                Box(modifier = Modifier.weight(1f).height(1.dp).background(BorderDark))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Form Section: Login vs Sign-Up
            AnimatedContent(
                targetState = selectedAuthTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "auth_form"
            ) { tabIndex ->
                if (tabIndex == 0) {
                    // ==========================================
                    // LOGIN FORM (Dual Access: User / Secret Admin)
                    // ==========================================
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = loginUsernameOrEmail,
                            onValueChange = { loginUsernameOrEmail = it },
                            label = { Text("ইউজারনেম বা জিমেইল (Email / Username)") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = if (loginError != null) Color.Red else TextSecondary)
                            },
                            placeholder = { Text("আপনার ইমেইল লিখুন (Enter Email)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_username_input"),
                            singleLine = true,
                            isError = loginError != null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (loginError != null) Color.Red else NatokRed,
                                unfocusedBorderColor = if (loginError != null) Color.Red else BorderDark,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                errorBorderColor = Color.Red,
                                errorLabelColor = Color.Red
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = { loginPassword = it },
                            label = { Text("পাসওয়ার্ড (Password)") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = if (loginError != null) Color.Red else TextSecondary)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input"),
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            isError = loginError != null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (loginError != null) Color.Red else NatokRed,
                                unfocusedBorderColor = if (loginError != null) Color.Red else BorderDark,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                errorBorderColor = Color.Red,
                                errorLabelColor = Color.Red
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Remember Me & Forgot Password Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Remember Me Checkbox
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { rememberMe = !rememberMe }
                            ) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { rememberMe = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = NatokRed,
                                        uncheckedColor = BorderDark,
                                        checkmarkColor = Color.White
                                    ),
                                    modifier = Modifier.testTag("remember_me_checkbox")
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "তথ্য মনে রাখুন",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                )
                            }

                            // Forgot Password Link
                            Text(
                                text = "পাসওয়ার্ড ভুলে গেছেন?",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NatokGold,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier
                                    .clickable {
                                        resetEmailInput = loginUsernameOrEmail
                                        resetCodeInput = ""
                                        newPasswordInput = ""
                                        confirmPasswordInput = ""
                                        resetStatusMessage = null
                                        resetStep = 1
                                        showForgotPasswordDialog = true
                                    }
                                    .testTag("forgot_password_button")
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (rememberMe) {
                                    prefs.edit()
                                        .putBoolean("remember_me", true)
                                        .putString("saved_email", loginUsernameOrEmail)
                                        .putString("saved_password", loginPassword)
                                        .apply()
                                } else {
                                    prefs.edit()
                                        .putBoolean("remember_me", false)
                                        .remove("saved_email")
                                        .remove("saved_password")
                                        .apply()
                                }
                                onLoginSubmit(loginUsernameOrEmail, loginPassword, rememberMe)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_login_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "প্রবেশ করুন (Sign In)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }



                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "নতুন ইউজার? ", fontSize = 12.sp, color = TextSecondary)
                            Text(
                                text = "এখানে ক্লিক করে রেজিস্টার করুন",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NatokGold,
                                modifier = Modifier.clickable { selectedAuthTab = 1 }
                            )
                        }
                    }
                } else {
                    // ==========================================
                    // SIGN-UP FORM (2-STEP REGISTRATION PROCESS)
                    // ==========================================
                    Column(modifier = Modifier.fillMaxWidth()) {
                        AnimatedContent(
                            targetState = signUpStep,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "signup_step_animation"
                        ) { currentStep ->
                            if (currentStep == 1) {
                                // ------------------------------------------
                                // STEP 1: Name, Phone, Email, Password, Referral, Terms
                                // ------------------------------------------
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Step 1 Rules Card
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C172B)),
                                        border = BorderStroke(1.dp, NatokGold.copy(alpha = 0.5f)),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("📌", fontSize = 14.sp)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "ধাপ ১: একাউন্ট রেজিষ্ট্রেশন (Step 1)",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = NatokGold
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "• নাম, মোবাইল নম্বর, ইমেইল ও পাসওয়ার্ড সঠিকভাবে দিন।\n• এরপর নেক্সট বাটনে চাপলে প্রফাইল ইনফরমেশন পেইজ আসবে।",
                                                fontSize = 11.sp,
                                                color = Color.White,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }

                                    // 1. Full Name Field
                                    val isNameError = signUpSubmitted && (signUpName.trim().length < 3 || signUpName.trim().length > 30)
                                    OutlinedTextField(
                                        value = signUpName,
                                        onValueChange = { if (it.length <= 30) signUpName = it },
                                        label = { Text("আপনার পুরো নাম (Full Name)") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Person, contentDescription = null, tint = if (isNameError) Color.Red else TextSecondary)
                                        },
                                        placeholder = { Text("AR Akhirul Islam (৩-৩০ অক্ষর)") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("signup_name_input"),
                                        singleLine = true,
                                        isError = isNameError,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = if (isNameError) Color.Red else NatokRed,
                                            unfocusedBorderColor = if (isNameError) Color.Red else BorderDark,
                                            errorBorderColor = Color.Red,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    if (isNameError) {
                                        Text(
                                            text = "⚠️ নাম অন্তত ৩ থেকে ৩০ অক্ষরের মধ্যে হতে হবে",
                                            fontSize = 11.sp,
                                            color = Color.Red,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 4.dp)
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.height(10.dp))
                                    }

                                    // 2. BD Phone Number Field
                                    val isPhoneError = signUpSubmitted && (!signUpPhone.trim().matches(Regex("^01[3-9]\\d{8}$")))
                                    OutlinedTextField(
                                        value = signUpPhone,
                                        onValueChange = { input ->
                                            if (input.length <= 11 && input.all { char -> char.isDigit() }) {
                                                signUpPhone = input
                                            }
                                        },
                                        label = { Text("মোবাইল নম্বর (১১ ডিজিট)") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Phone, contentDescription = null, tint = if (isPhoneError) Color.Red else TextSecondary)
                                        },
                                        placeholder = { Text("017XXXXXXXX (১১ ডিজিট)") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("signup_phone_input"),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        singleLine = true,
                                        isError = isPhoneError,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = if (isPhoneError) Color.Red else NatokRed,
                                            unfocusedBorderColor = if (isPhoneError) Color.Red else BorderDark,
                                            errorBorderColor = Color.Red,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    if (isPhoneError) {
                                        Text(
                                            text = "⚠️ সঠিক ১১ ডিজিটের বাংলাদেশী মোবাইল নম্বর দিন (যেমন: 01712345678)",
                                            fontSize = 11.sp,
                                            color = Color.Red,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 4.dp)
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.height(10.dp))
                                    }

                                    // 3. Email Address Field
                                    val isEmailError = signUpSubmitted && (!signUpEmail.trim().contains("@") || !signUpEmail.trim().contains(".") || signUpEmail.trim().length < 5)
                                    OutlinedTextField(
                                        value = signUpEmail,
                                        onValueChange = { signUpEmail = it },
                                        label = { Text("ইমেইল এড্রেস (Gmail)") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Email, contentDescription = null, tint = if (isEmailError) Color.Red else TextSecondary)
                                        },
                                        placeholder = { Text("আপনার ইমেইল লিখুন (e.g. user@gmail.com)") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("signup_email_input"),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                        singleLine = true,
                                        isError = isEmailError,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = if (isEmailError) Color.Red else NatokRed,
                                            unfocusedBorderColor = if (isEmailError) Color.Red else BorderDark,
                                            errorBorderColor = Color.Red,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    if (isEmailError) {
                                        Text(
                                            text = "⚠️ সঠিক ইমেইল এড্রেস লিখুন (যেমন: user@gmail.com)",
                                            fontSize = 11.sp,
                                            color = Color.Red,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 4.dp)
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.height(10.dp))
                                    }

                                    // 4. Password Field (Max 8 characters limit)
                                    val isPasswordError = signUpSubmitted && (signUpPassword.trim().length < 4 || signUpPassword.trim().length > 8)
                                    OutlinedTextField(
                                        value = signUpPassword,
                                        onValueChange = { input ->
                                            if (input.length <= 8) {
                                                signUpPassword = input
                                            }
                                        },
                                        label = { Text("পাসওয়ার্ড তৈরি করুন (সর্বোচ্চ ৮ ডিজিট)") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Lock, contentDescription = null, tint = if (isPasswordError) Color.Red else TextSecondary)
                                        },
                                        placeholder = { Text("৪ থেকে ৮ ডিজিটের পাসওয়ার্ড") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("signup_password_input"),
                                        visualTransformation = PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        singleLine = true,
                                        isError = isPasswordError,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = if (isPasswordError) Color.Red else NatokRed,
                                            unfocusedBorderColor = if (isPasswordError) Color.Red else BorderDark,
                                            errorBorderColor = Color.Red,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    if (isPasswordError) {
                                        Text(
                                            text = "⚠️ পাসওয়ার্ড ৪ থেকে ৮ অক্ষরের মধ্যে হতে হবে (সর্বোচ্চ ৮ ডিজিট)",
                                            fontSize = 11.sp,
                                            color = Color.Red,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 4.dp)
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.height(10.dp))
                                    }

                                    // 5. Referral Code Field (Optional)
                                    OutlinedTextField(
                                        value = signUpReferralCode,
                                        onValueChange = { signUpReferralCode = it },
                                        label = { Text("রেফার কোড (ঐচ্ছিক - ২০০ কয়েন বোনাস)") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Tag, contentDescription = null, tint = TextSecondary)
                                        },
                                        placeholder = { Text("ARFLIX77") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("signup_referral_input"),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = NatokRed,
                                            unfocusedBorderColor = BorderDark,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // 6. Terms & Conditions Checkbox
                                    val isTermsError = (showTermsError || signUpSubmitted) && !isTermsAgreed
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isTermsError) Color(0x33FF0000) else Color.Transparent)
                                            .border(if (isTermsError) 1.dp else 0.dp, if (isTermsError) Color.Red else Color.Transparent, RoundedCornerShape(8.dp))
                                            .padding(vertical = 4.dp, horizontal = 2.dp)
                                            .testTag("terms_agreement_container_signup")
                                    ) {
                                        Checkbox(
                                            checked = isTermsAgreed,
                                            onCheckedChange = { checked ->
                                                isTermsAgreed = checked
                                                if (checked) showTermsError = false
                                            },
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = NatokRed,
                                                uncheckedColor = if (isTermsError) Color.Red else BorderDark,
                                                checkmarkColor = Color.White
                                            ),
                                            modifier = Modifier.testTag("terms_agreement_checkbox_signup")
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                    isTermsAgreed = !isTermsAgreed
                                                    if (isTermsAgreed) showTermsError = false
                                                }
                                        ) {
                                            Text(
                                                text = "I agree to Terms & Privacy Policy ",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = TextPrimary
                                            )
                                            Text(
                                                text = "See More",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NatokGold,
                                                textDecoration = TextDecoration.Underline,
                                                modifier = Modifier.clickable { showTermsDialog = true }
                                            )
                                        }
                                    }

                                    if (isTermsError) {
                                        Text(
                                            text = "⚠️ অ্যাপস ব্যবহারে সম্মত হলে এখানে টিক চিহ্ন দিন (Please check to agree)",
                                            fontSize = 11.sp,
                                            color = Color.Red,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(start = 8.dp, top = 2.dp, bottom = 4.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Step 1 Next Button
                                    Button(
                                        onClick = {
                                            signUpSubmitted = true
                                            val hasError = (signUpName.trim().length < 3 || signUpName.trim().length > 30) ||
                                                    (!signUpPhone.trim().matches(Regex("^01[3-9]\\d{8}$"))) ||
                                                    (signUpPassword.trim().length < 4 || signUpPassword.trim().length > 8) ||
                                                    (!signUpEmail.trim().contains("@") || !signUpEmail.trim().contains(".") || signUpEmail.trim().length < 5)

                                            if (!isTermsAgreed) {
                                                showTermsError = true
                                            }

                                            if (hasError || !isTermsAgreed) {
                                                Toast.makeText(context, "⚠️ অনুগ্রহ করে সঠিক তথ্য দিয়ে লাল চিহ্নিত ক্ষেত্রগুলো পূরণ করুন!", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }

                                            showTermsError = false
                                            signUpStep = 2
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp)
                                            .testTag("next_signup_step_button"),
                                        colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text(
                                            text = "পরবর্তী ধাপ ➔ (Next Step)",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            } else {
                                // ------------------------------------------
                                // STEP 2: Gender & Birthday Selection Page
                                // ------------------------------------------
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E172B)),
                                        border = BorderStroke(1.dp, NatokGold.copy(alpha = 0.6f)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text("👤", fontSize = 16.sp)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "ধাপ ২: প্রফাইল ডিটেইলস (Step 2)",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = NatokGold
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "আপনার জেন্ডার ও জন্মতারিখ সিলেক্ট করে রেজিস্ট্রেশন সম্পন্ন করুন। এই তথ্যগুলো প্রফাইলে শো করবে।",
                                                fontSize = 11.sp,
                                                color = Color.White,
                                                lineHeight = 16.sp
                                            )
                                        }
                                    }

                                    // Gender Selection (Boy / Girl)
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = "জেন্ডার নির্বাচন করুন (Gender)",
                                            fontSize = 13.sp,
                                            color = TextSecondary,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            // Boy Option
                                            val isBoy = signUpGender == "Boy"
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(48.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(if (isBoy) Color(0xFF1565C0) else SurfaceCard)
                                                    .border(1.dp, if (isBoy) Color(0xFF64B5F6) else BorderDark, RoundedCornerShape(10.dp))
                                                    .clickable { signUpGender = "Boy" }
                                                    .testTag("gender_boy_btn"),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.Male,
                                                        contentDescription = "Boy",
                                                        tint = if (isBoy) Color.White else TextSecondary,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "ছেলে (Male / Boy)",
                                                        fontSize = 13.sp,
                                                        fontWeight = if (isBoy) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (isBoy) Color.White else TextSecondary
                                                    )
                                                }
                                            }

                                            // Girl Option
                                            val isGirl = signUpGender == "Girl"
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(48.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(if (isGirl) Color(0xFFC2185B) else SurfaceCard)
                                                    .border(1.dp, if (isGirl) Color(0xFFF48FB1) else BorderDark, RoundedCornerShape(10.dp))
                                                    .clickable { signUpGender = "Girl" }
                                                    .testTag("gender_girl_btn"),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.Female,
                                                        contentDescription = "Girl",
                                                        tint = if (isGirl) Color.White else TextSecondary,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "মেয়ে (Female / Girl)",
                                                        fontSize = 13.sp,
                                                        fontWeight = if (isGirl) FontWeight.Bold else FontWeight.Normal,
                                                        color = if (isGirl) Color.White else TextSecondary
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Date of Birth Field
                                    OutlinedTextField(
                                        value = signUpBirthday,
                                        onValueChange = { signUpBirthday = it },
                                        label = { Text("জন্মতারিখ (Date of Birth)") },
                                        leadingIcon = {
                                            Icon(Icons.Default.Cake, contentDescription = null, tint = NatokGold)
                                        },
                                        placeholder = { Text("15-08-2002 (দিন-মাস-বছর)") },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("signup_birthday_input"),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = NatokRed,
                                            unfocusedBorderColor = BorderDark,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    )

                                    Spacer(modifier = Modifier.height(18.dp))

                                    // Row: Back & Submit Buttons
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { signUpStep = 1 },
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(50.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                                            border = BorderStroke(1.dp, BorderDark),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text("⬅️ আগের ধাপ", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        }

                                        Button(
                                            onClick = {
                                                onSignUpSubmit(
                                                    signUpName.trim(),
                                                    signUpEmail.trim(),
                                                    signUpPhone.trim(),
                                                    signUpPassword.trim(),
                                                    signUpReferralCode.trim(),
                                                    signUpGender,
                                                    signUpBirthday.trim()
                                                )
                                            },
                                            modifier = Modifier
                                                .weight(1.5f)
                                                .height(50.dp)
                                                .testTag("submit_signup_button"),
                                            colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                text = "রেজিস্ট্রেশন সম্পন্ন করুন 🚀",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(text = "ইতিমধ্যে একাউন্ট আছে? ", fontSize = 12.sp, color = TextSecondary)
                            Text(
                                text = "লগইন করুন",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NatokGold,
                                modifier = Modifier.clickable { selectedAuthTab = 0 }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "NatokFlix v2.5 • অফিশিয়াল বাংলা নাটক স্ট্রিমিং ও রিওয়ার্ড প্ল্যাটফর্ম",
                fontSize = 11.sp,
                color = Color(0xFF6E6E80),
                textAlign = TextAlign.Center
            )
        }
    }

    // Terms & Conditions Modal Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Gavel,
                    contentDescription = null,
                    tint = NatokGold,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "ব্যবহারের শর্তাবলী ও নীতিমালার চুক্তিপত্র",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 320.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "NatokFlix অ্যাপ ব্যবহারের মাধ্যমে আপনি নিচের শর্তাবলীতে পূর্ণ সম্মতি জ্ঞাপন করছেন:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )

                    AuthTermsBulletText("১. একাউন্ট সত্যতা:", "প্রতিটি ব্যবহারকারী কেবল একটি নিজস্ব একাউন্ট ব্যবহার করতে পারবেন। ভুয়া তথ্য দিয়ে একাধিক একাউন্ট খোলা নিষিদ্ধ।")
                    AuthTermsBulletText("২. নিয়মমাফিক রিওয়ার্ড:", "ভিডিও ও নাটক দেখে অর্জিত পয়েন্ট কেবল নীতিমালার অধীনে বিডিটি ক্যাশে রূপান্তরিত হবে। কোনো হ্যাক, অটো-ক্লিক বা ভিপিএন ব্যবহার গ্রহণযোগ্য নয়।")
                    AuthTermsBulletText("৩. উত্তোলন নীতি (Withdrawal):", "সর্বনিম্ন ৩০০ টাকা (৩০০ কয়েন = ৩০০ টাকা) হলে বিকাশ, নগদ বা রকেটে পেমেন্ট রিকোয়েস্ট করা যাবে। উইথড্র সার্ভিস প্রতিদিন সকাল ১০:০০ টা থেকে রাত ০৮:০০ টা পর্যন্ত সক্রিয় থাকে। অ্যাডমিন যাচাই শেষে পেমেন্ট প্রসেস হবে।")
                    AuthTermsBulletText("৪. তথ্য সুরক্ষা:", "আপনার প্রদানকৃত ইমেইল ও মোবাইল নম্বর শুধুমাত্র একাউন্ট শনাক্তকরণ ও নিরাপত্তার উদ্দেশ্যে ব্যবহৃত হবে।")
                    AuthTermsBulletText("৫. একাউন্ট স্থগিতের অধিকার:", "যেকোনো প্রতারণামূলক কর্মকাণ্ড শনাক্ত হলে কর্তৃপক্ষ একাউন্ট সাময়িক বা স্থায়ীভাবে বাতিল করার পূর্ণ অধিকার রাখে।")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isTermsAgreed = true
                        showTermsError = false
                        showTermsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NatokRed)
                ) {
                    Text("আমি সম্মত (I Agree)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("বন্ধ করুন", color = TextSecondary)
                }
            },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Forgot Password & Email Verification OTP Dialog
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                showForgotPasswordDialog = false
                resetStep = 1
                resetStatusMessage = null
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.LockReset,
                    contentDescription = null,
                    tint = NatokGold,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = if (resetStep == 1) "পাসওয়ার্ড রিসেট করুন" else "৬-ডিজিটের কোড ও নতুন পাসওয়ার্ড",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (resetStatusMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isResetError) Color(0x33FF0000) else Color(0x3300FF00))
                                .border(1.dp, if (isResetError) Color.Red else Color(0xFF81C784), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = resetStatusMessage!!,
                                fontSize = 11.sp,
                                color = if (isResetError) Color.Red else Color(0xFF81C784),
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (resetStep == 1) {
                        Text(
                            text = "আপনার রেজিস্টার্ড ইমেইল এড্রেসটি লিখুন। NatokFlix থেকে ৬ ডিজিটের ভেরিফিকেশন সিকিউরিটি কোড ইমেইল করা হবে।",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = resetEmailInput,
                            onValueChange = { resetEmailInput = it },
                            label = { Text("রেজিস্টার্ড ইমেইল (Gmail)") },
                            placeholder = { Text("আপনার ইমেইল লিখুন") },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = NatokGold)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reset_email_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NatokRed,
                                unfocusedBorderColor = BorderDark,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    } else {
                        // Step 2: Code Verification & New Password
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E132B)),
                            border = BorderStroke(1.dp, NatokGold.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("📧", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "[NatokFlix] ইমেইল প্রেরিত!",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NatokGold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "প্রাপক: $resetEmailInput\nআপনার ইমেইলে প্রেরিত সিকিউরিটি কোড: $generatedOtpCode",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        OutlinedTextField(
                            value = resetCodeInput,
                            onValueChange = { if (it.length <= 6) resetCodeInput = it },
                            label = { Text("৬ ডিজিটের সিকিউরিটি কোড (OTP)") },
                            placeholder = { Text("কোড লিখুন (e.g. $generatedOtpCode)") },
                            leadingIcon = {
                                Icon(Icons.Default.Key, contentDescription = null, tint = NatokGold)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reset_code_input"),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NatokRed,
                                unfocusedBorderColor = BorderDark,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newPasswordInput,
                            onValueChange = { newPasswordInput = it },
                            label = { Text("নতুন পাসওয়ার্ড (New Password)") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = TextSecondary)
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("new_password_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NatokRed,
                                unfocusedBorderColor = BorderDark,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = confirmPasswordInput,
                            onValueChange = { confirmPasswordInput = it },
                            label = { Text("পাসওয়ার্ড নিশ্চিত করুন (Confirm)") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = TextSecondary)
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("confirm_password_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NatokRed,
                                unfocusedBorderColor = BorderDark,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                if (resetStep == 1) {
                    Button(
                        onClick = {
                            onSendResetOtp(resetEmailInput, context) { success, msg, code ->
                                resetStatusMessage = msg
                                isResetError = !success
                                if (success) {
                                    generatedOtpCode = code
                                    resetStep = 2
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                        modifier = Modifier.testTag("send_otp_button")
                    ) {
                        Text("কোড পাঠান (Send Code)", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            if (resetCodeInput.trim() != generatedOtpCode) {
                                resetStatusMessage = "ভুল কোড! সঠিক ৬-ডিজিটের কোড প্রবেশ করান।"
                                isResetError = true
                                return@Button
                            }
                            if (newPasswordInput.trim().length < 4) {
                                resetStatusMessage = "নতুন পাসওয়ার্ড অন্তত ৪ অক্ষরের হতে হবে।"
                                isResetError = true
                                return@Button
                            }
                            if (newPasswordInput.trim() != confirmPasswordInput.trim()) {
                                resetStatusMessage = "পাসওয়ার্ড দুটি মিলছে না! আবার চেষ্টা করুন।"
                                isResetError = true
                                return@Button
                            }

                            onResetUserPassword(resetEmailInput, newPasswordInput.trim()) { success, msg ->
                                if (success) {
                                    Toast.makeText(context, "✅ $msg", Toast.LENGTH_LONG).show()
                                    loginUsernameOrEmail = resetEmailInput
                                    loginPassword = newPasswordInput.trim()
                                    showForgotPasswordDialog = false
                                    resetStep = 1
                                } else {
                                    resetStatusMessage = msg
                                    isResetError = true
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NatokRed),
                        modifier = Modifier.testTag("submit_reset_password_button")
                    ) {
                        Text("পাসওয়ার্ড পরিবর্তন করুন", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showForgotPasswordDialog = false
                        resetStep = 1
                        resetStatusMessage = null
                    }
                ) {
                    Text("বাতিল (Cancel)", color = TextSecondary)
                }
            },
            containerColor = SurfaceCard,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun AuthTermsBulletText(title: String, desc: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = NatokGold
        )
        Text(
            text = desc,
            fontSize = 11.sp,
            color = TextSecondary,
            lineHeight = 15.sp
        )
    }
}
