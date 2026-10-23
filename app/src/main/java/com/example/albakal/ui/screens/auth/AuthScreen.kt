package com.example.albakal.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.albakal.data.model.EgyptLocations
import com.example.albakal.data.model.UserRole
import com.example.albakal.ui.AlBaqalViewModel
import com.example.albakal.ui.LoginResult
import com.example.albakal.ui.RegisterResult
import com.example.albakal.ui.common.ApkDownloadDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    viewModel: AlBaqalViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var showApkDialog by remember { mutableStateOf(false) }

    // Login Form State
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var isLoggingIn by remember { mutableStateOf(false) }

    // Register Form State
    var regRole by remember { mutableStateOf(UserRole.CUSTOMER) }
    var regFullName by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regGovernorate by remember { mutableStateOf("الإسكندرية") }
    var regDistrict by remember { mutableStateOf("سيدي جابر") }
    var regStreet by remember { mutableStateOf("") }
    var regBuilding by remember { mutableStateOf("") }
    var regFloor by remember { mutableStateOf("") }
    var regApartment by remember { mutableStateOf("") }
    var regError by remember { mutableStateOf<String?>(null) }
    var isRegistering by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Header & Branding
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBasket,
                    contentDescription = "شعار البقال",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(42.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "تطبيق البقال",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "السوبرماركت في متناول يدك - ربط حقيقي بين العميل والتاجر",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { showApkDialog = true }
                    .testTag("apk_download_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "APK",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تثبيت وتحميل حزمة التطبيق (APK) ومستودع GitHub",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Selector: تسجيل الدخول / حساب جديد
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0; loginError = null },
                    text = {
                        Text(
                            text = "تسجيل الدخول",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("tab_login")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1; regError = null },
                    text = {
                        Text(
                            text = "تسجيل حساب جديد",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("tab_register")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // TAB 0: LOGIN
            if (selectedTab == 0) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "مرحباً بك مجدداً",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "سجل دخولك ليتم توجيهك مباشرة لصفحتك (عميل / تاجر / إدارة)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )

                        if (loginError != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {
                                Text(
                                    text = loginError ?: "",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = loginEmail,
                            onValueChange = { loginEmail = it },
                            label = { Text("البريد الإلكتروني") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_email_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = { loginPassword = it },
                            label = { Text("كلمة المرور") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                    Icon(
                                        imageVector = if (loginPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            TextButton(
                                onClick = { showForgotPasswordDialog = true },
                                modifier = Modifier.testTag("forgot_password_button")
                            ) {
                                Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "نسيت كلمة السر؟", fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                if (loginEmail.isBlank() || loginPassword.isBlank()) {
                                    loginError = "يرجى كتابة البريد الإلكتروني وكلمة المرور"
                                    return@Button
                                }
                                isLoggingIn = true
                                loginError = null
                                coroutineScope.launch {
                                    val result = viewModel.login(loginEmail, loginPassword)
                                    isLoggingIn = false
                                    if (result is LoginResult.Error) {
                                        loginError = result.message
                                    }
                                }
                            },
                            enabled = !isLoggingIn,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_submit_button")
                        ) {
                            if (isLoggingIn) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(22.dp))
                            } else {
                                Text(text = "تسجيل الدخول", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Helpful test presets
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "حسابات مسجلة مسبقاً للتجربة المباشرة:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        loginEmail = "mahmoudomran324@gmail.com"
                                        loginPassword = "Aa&9119045"
                                    }
                            ) {
                                Text(
                                    text = "الأدمن",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        loginEmail = "baraka.market@albakal.com"
                                        loginPassword = "123456"
                                    }
                            ) {
                                Text(
                                    text = "تاجر بركة",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        loginEmail = "ahmed.customer@albakal.com"
                                        loginPassword = "123456"
                                    }
                            ) {
                                Text(
                                    text = "عميل أحمد",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // TAB 1: REGISTER
            if (selectedTab == 1) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "إنشاء حساب جديد",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "اختر نوع الحساب وأدخل بياناتك وعنوانك بدقة",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        // Role Selector: عميل / تاجر
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (regRole == UserRole.CUSTOMER) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { regRole = UserRole.CUSTOMER }
                                    .testTag("role_customer_select")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = if (regRole == UserRole.CUSTOMER) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "مستخدم عميل",
                                        fontWeight = FontWeight.Bold,
                                        color = if (regRole == UserRole.CUSTOMER) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (regRole == UserRole.MERCHANT) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { regRole = UserRole.MERCHANT }
                                    .testTag("role_merchant_select")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Storefront,
                                        contentDescription = null,
                                        tint = if (regRole == UserRole.MERCHANT) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "مستخدم تاجر",
                                        fontWeight = FontWeight.Bold,
                                        color = if (regRole == UserRole.MERCHANT) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        if (regError != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            ) {
                                Text(
                                    text = regError ?: "",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = regFullName,
                            onValueChange = { regFullName = it },
                            label = { Text(if (regRole == UserRole.CUSTOMER) "الاسم الكامل" else "اسم المحل أو التاجر") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_name_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text("البريد الإلكتروني") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_email_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regPhone,
                            onValueChange = { regPhone = it },
                            label = { Text("رقم الهاتف (للتواصل والواتساب)") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_phone_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text("كلمة المرور") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_password_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Governorates & Districts Dropdowns
                        Text(
                            text = "العنوان والموقع الجغرافي:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )

                        // Governorate selector
                        var govExpanded by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = regGovernorate,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("المحافظة") },
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { govExpanded = true }
                                    .testTag("gov_dropdown"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            DropdownMenu(
                                expanded = govExpanded,
                                onDismissRequest = { govExpanded = false }
                            ) {
                                EgyptLocations.defaultGovernorates.forEach { gov ->
                                    DropdownMenuItem(
                                        text = { Text(gov) },
                                        onClick = {
                                            regGovernorate = gov
                                            regDistrict = EgyptLocations.getDistrictsFor(gov).firstOrNull() ?: "المركز"
                                            govExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // District selector (Special focus on Alexandria districts)
                        var distExpanded by remember { mutableStateOf(false) }
                        val districtsList = EgyptLocations.getDistrictsFor(regGovernorate)
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = regDistrict,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("القسم / الحي / المنطقة") },
                                trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { distExpanded = true }
                                    .testTag("district_dropdown"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            DropdownMenu(
                                expanded = distExpanded,
                                onDismissRequest = { distExpanded = false }
                            ) {
                                districtsList.forEach { dist ->
                                    DropdownMenuItem(
                                        text = { Text(dist) },
                                        onClick = {
                                            regDistrict = dist
                                            distExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regStreet,
                            onValueChange = { regStreet = it },
                            label = { Text("اسم الشارع بالتفصيل") },
                            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_street_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = regBuilding,
                                onValueChange = { regBuilding = it },
                                label = { Text("رقم العمارة") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = regFloor,
                                onValueChange = { regFloor = it },
                                label = { Text("الدور") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = regApartment,
                                onValueChange = { regApartment = it },
                                label = { Text("شقة") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (regFullName.isBlank() || regEmail.isBlank() || regPhone.isBlank() || regPassword.isBlank() || regStreet.isBlank()) {
                                    regError = "يرجى استكمال جميع بيانات الحساب والعنوان"
                                    return@Button
                                }
                                isRegistering = true
                                regError = null
                                coroutineScope.launch {
                                    val res = viewModel.register(
                                        email = regEmail,
                                        pass = regPassword,
                                        fullName = regFullName,
                                        phone = regPhone,
                                        role = regRole,
                                        governorate = regGovernorate,
                                        district = regDistrict,
                                        street = regStreet,
                                        building = regBuilding,
                                        floor = regFloor,
                                        apartment = regApartment
                                    )
                                    isRegistering = false
                                    if (res is RegisterResult.Error) {
                                        regError = res.message
                                    }
                                }
                            },
                            enabled = !isRegistering,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("register_submit_button")
                        ) {
                            if (isRegistering) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(22.dp))
                            } else {
                                Text(
                                    text = "تسجيل الحساب والبدء",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // FORGOT PASSWORD DIALOG
    if (showForgotPasswordDialog) {
        var forgotPhone by remember { mutableStateOf("") }
        var forgotSubmitted by remember { mutableStateOf(false) }
        var forgotError by remember { mutableStateOf<String?>(null) }
        var isSubmittingForgot by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = {
                Text(text = "استعادة كلمة السر", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    if (!forgotSubmitted) {
                        Text(
                            text = "أدخل رقم الهاتف المسجل به في تطبيق البقال، وسيصل إشعار إلى الإدارة لإرسال كافة بيانات حسابك وكلمة السر إليك عبر واتساب:",
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = forgotPhone,
                            onValueChange = { forgotPhone = it },
                            label = { Text("رقم الهاتف") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("forgot_phone_input"),
                            shape = RoundedCornerShape(10.dp)
                        )
                        if (forgotError != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = forgotError ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                        }
                    } else {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "تم إرسال طلب استعادة كلمة السر للإدارة بنجاح!",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "سيقوم المشرف بالضغط على زر إرسال البيانات من لوحة التحكم لإرسال كافة بياناتك على وتساب الخاص برقمك: $forgotPhone",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                if (!forgotSubmitted) {
                    Button(
                        onClick = {
                            if (forgotPhone.isBlank()) {
                                forgotError = "يرجى كتابة رقم الهاتف"
                                return@Button
                            }
                            isSubmittingForgot = true
                            forgotError = null
                            coroutineScope.launch {
                                val found = viewModel.requestPasswordRecovery(forgotPhone)
                                isSubmittingForgot = false
                                if (found) {
                                    forgotSubmitted = true
                                } else {
                                    forgotError = "لم يتم العثور على حساب مسجل بهذا الرقم، يرجى التأكد من الرقم أو تسجيل حساب جديد"
                                }
                            }
                        },
                        enabled = !isSubmittingForgot,
                        modifier = Modifier.testTag("submit_forgot_phone")
                    ) {
                        Text("إرسال الطلب للإدارة")
                    }
                } else {
                    Button(onClick = { showForgotPasswordDialog = false }) {
                        Text("حسناً")
                    }
                }
            },
            dismissButton = {
                if (!forgotSubmitted) {
                    TextButton(onClick = { showForgotPasswordDialog = false }) {
                        Text("إلغاء")
                    }
                }
            }
        )
    }

    if (showApkDialog) {
        ApkDownloadDialog(onDismiss = { showApkDialog = false })
    }
}
