package com.example.albakal.ui.screens.admin

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.albakal.data.local.DebtSettlementEntity
import com.example.albakal.data.local.PasswordRecoveryEntity
import com.example.albakal.data.local.ProductEntity
import com.example.albakal.data.local.SponsoredAdEntity
import com.example.albakal.data.local.UserEntity
import com.example.albakal.data.model.ProductCategory
import com.example.albakal.data.model.UserRole
import com.example.albakal.data.model.UserSession
import com.example.albakal.ui.AlBaqalViewModel
import com.example.albakal.ui.common.ApkDownloadDialog
import com.example.albakal.ui.common.StatCard
import com.example.albakal.ui.common.WhatsAppHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminContainerScreen(
    user: UserSession,
    viewModel: AlBaqalViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showChangeAdminCreds by remember { mutableStateOf(false) }
    var showApkDialog by remember { mutableStateOf(false) }

    val allRecoveryRequests by viewModel.allPasswordRecoveryRequests.collectAsStateWithLifecycle()
    val pendingRecoveryCount = allRecoveryRequests.count { !it.isResolved }

    val allSettlements by viewModel.allDebtSettlements.collectAsStateWithLifecycle()
    val pendingSettlementsCount = allSettlements.count { it.status.name == "PENDING" }

    val settings by viewModel.systemSettings.collectAsStateWithLifecycle()

    val isSuperAdmin = user.role == UserRole.ADMIN

    // Restrict tabs if supervisor has limited permissions
    fun canAccess(permission: String): Boolean {
        return isSuperAdmin || user.supervisorPermissions.contains(permission)
    }

    // Intercept back button
    BackHandler {
        if (selectedTab != 0) {
            selectedTab = 0
        } else {
            showLogoutConfirm = true
        }
    }

    val tabTitles = listOf(
        "نظرة عامة",
        "استعادة كلمة السر",
        "التجار والمتاجر",
        "العملاء",
        "تسديد المديونيات",
        "المشرفين",
        "بضائع المتاجر",
        "الإعلانات الممولة",
        "الإعدادات والماليات",
        "المحافظات والأحياء",
        if (settings.captainFleetEnabled) "أسطول الكباتن" else ""
    ).filter { it.isNotEmpty() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isSuperAdmin) "لوحة الإدارة المركزية" else "لوحة المشرف الإداري",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = user.email,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    if (isSuperAdmin) {
                        IconButton(
                            onClick = { showChangeAdminCreds = true },
                            modifier = Modifier.testTag("admin_change_creds_button")
                        ) {
                            Icon(Icons.Default.LockReset, contentDescription = "تغيير بيانات الأدمن", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    IconButton(onClick = { showApkDialog = true }) {
                        Icon(Icons.Default.Download, contentDescription = "APK", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(
                        onClick = { showLogoutConfirm = true },
                        modifier = Modifier.testTag("admin_logout_button")
                    ) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "خروج", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Scrollable Tabs Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 12.dp,
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                                if (title == "استعادة كلمة السر" && pendingRecoveryCount > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Badge { Text("$pendingRecoveryCount") }
                                }
                                if (title == "تسديد المديونيات" && pendingSettlementsCount > 0) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Badge { Text("$pendingSettlementsCount") }
                                }
                            }
                        },
                        modifier = Modifier.testTag("admin_tab_$index")
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedTab) {
                    0 -> AdminOverviewTab(viewModel = viewModel, onNavigateToTab = { selectedTab = it })
                    1 -> AdminPasswordRecoveryTab(viewModel = viewModel)
                    2 -> AdminMerchantsTab(viewModel = viewModel, canManage = canAccess("MERCHANTS"))
                    3 -> AdminCustomersTab(viewModel = viewModel, canManage = canAccess("MERCHANTS"))
                    4 -> AdminDebtSettlementsTab(viewModel = viewModel)
                    5 -> AdminSupervisorsTab(viewModel = viewModel, isSuperAdmin = isSuperAdmin)
                    6 -> AdminProductsTab(viewModel = viewModel, canManage = canAccess("PRODUCTS"))
                    7 -> AdminSponsoredAdsTab(viewModel = viewModel, canManage = canAccess("ADS"))
                    8 -> AdminSettingsTab(viewModel = viewModel, isSuperAdmin = isSuperAdmin)
                    9 -> AdminLocationsTab(viewModel = viewModel)
                    10 -> AdminCaptainsTab(viewModel = viewModel)
                }
            }
        }
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("تسجيل الخروج من الإدارة") },
            text = { Text("هل تريد بالتأكيد تسجيل الخروج؟") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        viewModel.logout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("خروج")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    if (showChangeAdminCreds) {
        var newEmail by remember { mutableStateOf(user.email) }
        var newPassword by remember { mutableStateOf("") }
        val context = LocalContext.current

        AlertDialog(
            onDismissRequest = { showChangeAdminCreds = false },
            title = { Text("تغيير بريد وكلمة مرور الأدمن") },
            text = {
                Column {
                    Text("يمكن للمدير الأساسي فقط تغيير بيانات دخوله:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        label = { Text("البريد الإلكتروني الجديد") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("كلمة المرور الجديدة") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newEmail.isNotBlank() && newPassword.isNotBlank()) {
                            viewModel.updateAdminCredentials(newEmail, newPassword)
                            Toast.makeText(context, "تم تحديث بيانات الأدمن بنجاح!", Toast.LENGTH_SHORT).show()
                            showChangeAdminCreds = false
                        }
                    }
                ) {
                    Text("حفظ التغييرات")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangeAdminCreds = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    if (showApkDialog) {
        ApkDownloadDialog(onDismiss = { showApkDialog = false })
    }
}

@Composable
fun AdminOverviewTab(
    viewModel: AlBaqalViewModel,
    onNavigateToTab: (Int) -> Unit
) {
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val orders by viewModel.allOrders.collectAsStateWithLifecycle()
    val products by viewModel.allProducts.collectAsStateWithLifecycle()
    val recoveryRequests by viewModel.allPasswordRecoveryRequests.collectAsStateWithLifecycle()
    val debtSettlements by viewModel.allDebtSettlements.collectAsStateWithLifecycle()

    val merchantsCount = users.count { it.role == UserRole.MERCHANT }
    val customersCount = users.count { it.role == UserRole.CUSTOMER }
    val totalRevenue = orders.filter { it.status.name == "DELIVERED" }.sumOf { it.grandTotal }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(text = "لوحة التحكم والإحصائيات الشاملة", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        item {
            StatCard(
                title = "إجمالي مبيعات التطبيق المكتملة",
                value = "$totalRevenue ج.م",
                subtitle = "${orders.size} طلب مسجل بالنظام",
                icon = Icons.Default.MonetizationOn,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "التجار المسجلين",
                    value = "$merchantsCount تاجر",
                    icon = Icons.Default.Store,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTab(2) }
                )
                StatCard(
                    title = "العملاء المسجلين",
                    value = "$customersCount عميل",
                    icon = Icons.Default.People,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToTab(3) }
                )
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "تنبيهات الإدارة والعمليات المعلقة:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    val pendingRecovery = recoveryRequests.count { !it.isResolved }
                    val pendingSettlements = debtSettlements.count { it.status.name == "PENDING" }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToTab(1) }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LockReset, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("طلبات استعادة كلمة السر المعلقة", fontSize = 13.sp)
                        }
                        Surface(
                            color = if (pendingRecovery > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ) {
                            Text(
                                text = "$pendingRecovery",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Divider()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToTab(4) }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("طلبات تسديد المديونيات من التجار", fontSize = 13.sp)
                        }
                        Surface(
                            color = if (pendingSettlements > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ) {
                            Text(
                                text = "$pendingSettlements",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// 1. PASSWORD RECOVERY TAB
@Composable
fun AdminPasswordRecoveryTab(
    viewModel: AlBaqalViewModel
) {
    val requests by viewModel.allPasswordRecoveryRequests.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "طلبات نسيت كلمة السر (${requests.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                text = "اضغط على زر «إرسال البيانات عبر وتساب» لإرسال كافة بيانات الدخول وكلمة السر للعميل أو التاجر فوراً:",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (requests.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("لا توجد طلبات استعادة كلمة مرور حالياً", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(requests, key = { it.id }) { req ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (!req.isResolved) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = req.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Surface(
                                color = if (req.isResolved) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (req.isResolved) "تم الإرسال" else "طلب جديد",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (req.isResolved) Color(0xFF15803D) else Color(0xFFB45309),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "رقم الهاتف: ${req.phone}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "البريد: ${req.userEmail}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "كلمة المرور الحالية: ${req.userPassword}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                val message = """
مرحباً ${req.userName}،
بناءً على طلبك من تطبيق «البقال»، إليك بيانات حسابك لتسجيل الدخول:
البريد الإلكتروني: ${req.userEmail}
كلمة المرور: ${req.userPassword}
نوع الحساب: ${req.userRole.name}

يمكنك الآن الدخول للتطبيق بكل سهولة.
                                """.trimIndent()
                                WhatsAppHelper.openWhatsApp(context, req.phone, message)
                                viewModel.resolvePasswordRecovery(req.id)
                                Toast.makeText(context, "تم فتح وتساب وإرسال البيانات للعميل", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("send_whatsapp_recovery_${req.id}")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("إرسال البيانات عبر وتساب", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

// 2. MERCHANTS TAB
@Composable
fun AdminMerchantsTab(
    viewModel: AlBaqalViewModel,
    canManage: Boolean
) {
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val merchants = remember(allUsers) {
        allUsers.filter { it.role == UserRole.MERCHANT }
    }
    val context = LocalContext.current

    var banDialogMerchant by remember { mutableStateOf<UserEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(text = "إدارة التجار ومتاجر السوبرماركت (${merchants.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        items(merchants, key = { it.id }) { merchant ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (merchant.isBanned) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = merchant.fullName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        if (merchant.isBanned) {
                            Surface(color = MaterialTheme.colorScheme.error, shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = "محظور: ${merchant.banReason}",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        } else {
                            Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = "نشط ومعتمد",
                                    color = Color(0xFF15803D),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "الهاتف: ${merchant.phone} | البريد: ${merchant.email}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "الموقع: ${merchant.governorate} - ${merchant.district} - ${merchant.street}", fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!merchant.isBanned) {
                            Button(
                                onClick = { banDialogMerchant = merchant },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Block, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("حظر التاجر", fontSize = 11.sp)
                            }
                        } else {
                            Button(
                                onClick = {
                                    viewModel.banUser(merchant.id, false, "")
                                    Toast.makeText(context, "تم إلغاء الحظر عن التاجر وإعادة منتجاته للمتجر", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("إلغاء الحظر", fontSize = 11.sp)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                val msg = "مرحباً تاجر ${merchant.fullName}، معك إدارة تطبيق البقال بخصوص متجرك:"
                                WhatsAppHelper.openWhatsApp(context, merchant.phone, msg)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("مراسلة وتساب", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    // BAN MERCHANT DIALOG
    if (banDialogMerchant != null) {
        val target = banDialogMerchant!!
        var reasonOption by remember { mutableStateOf("ارتفاع المديونية وعمولات التطبيق") }
        var customNote by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { banDialogMerchant = null },
            title = { Text("حظر التاجر وإخفاء بضائعه", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "اختر سبب الحظر. سيتم إخفاء بضائع التاجر تماماً من المتجر وإرسال إشعار فوري له عبر وتساب:",
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    listOf(
                        "ارتفاع المديونية وعمولات التطبيق",
                        "غش تجاري أو بضائع تالفة مكررة",
                        "مخالفة ميثاق الشرف وسياسات البقال"
                    ).forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { reasonOption = option }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            androidx.compose.material3.RadioButton(
                                selected = reasonOption == option,
                                onClick = { reasonOption = option }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = option, fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val fullReason = "$reasonOption $customNote".trim()
                        viewModel.banUser(target.id, true, fullReason)
                        val notice = "إشعار رسمي من إدارة تطبيق البقال:\nنحيطكم علماً بأنه تم حظر حسابكم كتاجر وإيقاف متجركم مؤقتاً.\nسبب الحظر: $fullReason.\nيرجى التواصل لتسوية الوضع."
                        WhatsAppHelper.openWhatsApp(context, target.phone, notice)
                        Toast.makeText(context, "تم حظر التاجر وإخفاء منتجاته وإرسال رسالة وتساب", Toast.LENGTH_LONG).show()
                        banDialogMerchant = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("تأكيد الحظر وإرسال وتساب")
                }
            },
            dismissButton = {
                TextButton(onClick = { banDialogMerchant = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

// 3. CUSTOMERS TAB
@Composable
fun AdminCustomersTab(
    viewModel: AlBaqalViewModel,
    canManage: Boolean
) {
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val customers = remember(allUsers) {
        allUsers.filter { it.role == UserRole.CUSTOMER }
    }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(text = "إدارة العملاء المسجلين (${customers.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        items(customers, key = { it.id }) { customer ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = customer.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        if (customer.isMonthlySubscribed) {
                            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = "مشترك شهري",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(text = "الهاتف: ${customer.phone} | البريد: ${customer.email}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "العنوان: ${customer.governorate} - ${customer.district} - ${customer.street}", fontSize = 11.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                WhatsAppHelper.openWhatsApp(context, customer.phone, "مرحباً ${customer.fullName}، معك إدارة تطبيق البقال:")
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("وتساب", fontSize = 11.sp)
                        }
                        if (canManage) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.deleteUser(customer.id)
                                    Toast.makeText(context, "تم حذف حساب العميل", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("حذف الحساب", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 4. DEBT SETTLEMENTS TAB
@Composable
fun AdminDebtSettlementsTab(
    viewModel: AlBaqalViewModel
) {
    val settlements by viewModel.allDebtSettlements.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(text = "مراجعة تسديد مديونيات التجار (${settlements.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "عند تأكيد الدفع يتم اعتماد السداد وتخفيض أو تصفير مديونية التاجر فوراً:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        if (settlements.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("لا توجد طلبات سداد مديونية حالياً", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(settlements, key = { it.id }) { s ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "التاجر: ${s.merchantName}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Surface(
                                color = if (s.status.name == "APPROVED") Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = s.status.displayNameAr,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (s.status.name == "APPROVED") Color(0xFF15803D) else Color(0xFFB45309),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "المبلغ المحول: ${s.amount} ج.م", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                        Text(text = "طريقة الدفع: ${s.paymentMethod}", fontSize = 12.sp)
                        Text(text = "رقم العملية / الإيصال: ${s.receiptScreenshotUri}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        if (s.status.name == "PENDING") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    viewModel.approveDebtSettlement(s.id)
                                    Toast.makeText(context, "تم تأكيد السداد وتصفير مديونية التاجر بنجاح!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("تأكيد استلام المبلغ وتصفير المديونية")
                            }
                        }
                    }
                }
            }
        }
    }
}

// 5. SUPERVISORS TAB (WITH CHECKBOX PERMISSIONS)
@Composable
fun AdminSupervisorsTab(
    viewModel: AlBaqalViewModel,
    isSuperAdmin: Boolean
) {
    val allUsers by viewModel.allUsers.collectAsStateWithLifecycle()
    val supervisors = remember(allUsers) {
        allUsers.filter { it.role == UserRole.SUPERVISOR }
    }
    var showAddSupervisorDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "إدارة المشرفين وتحديد الصلاحيات (${supervisors.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "تحديد صلاحيات دقيقة في مربعات اختيار لكل مشرف", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (isSuperAdmin) {
                    Button(onClick = { showAddSupervisorDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة مشرف", fontSize = 12.sp)
                    }
                }
            }
        }

        items(supervisors, key = { it.id }) { s ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = s.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        if (isSuperAdmin) {
                            IconButton(onClick = {
                                viewModel.deleteUser(s.id)
                                Toast.makeText(context, "تم إزالة المشرف", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                    Text(text = "البريد: ${s.email} | الهاتف: ${s.phone}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "الصلاحيات الممنوحة:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(text = s.supervisorPermissions.ifEmpty { "لا توجد صلاحيات محددة" }, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }

    if (showAddSupervisorDialog) {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var pass by remember { mutableStateOf("") }

        var permProducts by remember { mutableStateOf(true) }
        var permOrders by remember { mutableStateOf(true) }
        var permMerchants by remember { mutableStateOf(false) }
        var permAds by remember { mutableStateOf(false) }
        var permSettings by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddSupervisorDialog = false },
            title = { Text("تعيين مشرف جديد وتحديد الصلاحيات", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn {
                    item {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("اسم المشرف") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("البريد الإلكتروني") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("رقم الهاتف") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = pass,
                            onValueChange = { pass = it },
                            label = { Text("كلمة المرور") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "حدد الصلاحيات الممنوحة للمشرف:", fontWeight = FontWeight.Bold, fontSize = 12.sp)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = permProducts, onCheckedChange = { permProducts = it })
                            Text("إدارة المنتجات وبضائع المتاجر", fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = permOrders, onCheckedChange = { permOrders = it })
                            Text("إدارة ومتابعة الطلبات", fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = permMerchants, onCheckedChange = { permMerchants = it })
                            Text("إدارة التجار والعملاء والحظر", fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = permAds, onCheckedChange = { permAds = it })
                            Text("إدارة الإعلانات الممولة والعروض", fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = permSettings, onCheckedChange = { permSettings = it })
                            Text("إدارة أسعار الشحن والاشتراكات", fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isBlank() || email.isBlank() || pass.isBlank()) {
                            Toast.makeText(context, "يرجى استكمال البيانات", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        val perms = mutableSetOf<String>()
                        if (permProducts) perms.add("PRODUCTS")
                        if (permOrders) perms.add("ORDERS")
                        if (permMerchants) perms.add("MERCHANTS")
                        if (permAds) perms.add("ADS")
                        if (permSettings) perms.add("SETTINGS")

                        viewModel.addSupervisor(email, pass, name, phone, perms)
                        Toast.makeText(context, "تمت إضافة المشرف بنجاح", Toast.LENGTH_SHORT).show()
                        showAddSupervisorDialog = false
                    }
                ) {
                    Text("حفظ المشرف")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSupervisorDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

// 6. STORE PRODUCTS TAB
@Composable
fun AdminProductsTab(
    viewModel: AlBaqalViewModel,
    canManage: Boolean
) {
    val products by viewModel.allProducts.collectAsStateWithLifecycle()
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val merchants = remember(users) { users.filter { it.role == UserRole.MERCHANT } }
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "البضائع والمنتجات المعروضة (${products.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (canManage && merchants.isNotEmpty()) {
                    Button(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة سلعة لتاجر", fontSize = 11.sp)
                    }
                }
            }
        }

        items(products, key = { it.id }) { product ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = product.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "التاجر: ${product.merchantName} | ${product.category.displayNameAr}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "${product.price} ج.م", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                    }
                    if (canManage) {
                        IconButton(onClick = {
                            viewModel.deleteProduct(product.id)
                            Toast.makeText(context, "تم حذف السلعة", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog && merchants.isNotEmpty()) {
        var selectedMerchant by remember { mutableStateOf(merchants.first()) }
        var title by remember { mutableStateOf("") }
        var category by remember { mutableStateOf(ProductCategory.DRY_GROCERY) }
        var priceStr by remember { mutableStateOf("") }
        var unit by remember { mutableStateOf("1 قطعة") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("إضافة سلعة لمتجر تاجر") },
            text = {
                Column {
                    Text("اختر التاجر المستهدف:", fontSize = 12.sp)
                    // Simple merchant selection
                    OutlinedTextField(
                        value = selectedMerchant.fullName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("التاجر") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("اسم السلعة") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("السعر (ج.م)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val p = priceStr.toDoubleOrNull() ?: 0.0
                        if (title.isNotBlank() && p > 0) {
                            viewModel.addProduct(
                                merchantId = selectedMerchant.id,
                                merchantName = selectedMerchant.fullName,
                                title = title,
                                description = "أضيف بواسطة الإدارة بالاتفاق مع التاجر",
                                category = category,
                                price = p,
                                discountPrice = null,
                                unit = unit,
                                imageUrl = ""
                            )
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("إضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("إلغاء") }
            }
        )
    }
}

// 7. SPONSORED ADS TAB
@Composable
fun AdminSponsoredAdsTab(
    viewModel: AlBaqalViewModel,
    canManage: Boolean
) {
    val ads by viewModel.allSponsoredAds.collectAsStateWithLifecycle()
    var showAddAdDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "الإعلانات الممولة (${ads.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                if (canManage) {
                    Button(onClick = { showAddAdDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة إعلان", fontSize = 12.sp)
                    }
                }
            }
        }

        items(ads, key = { it.id }) { ad ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = ad.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = ad.subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (canManage) {
                        IconButton(onClick = {
                            viewModel.deleteSponsoredAd(ad.id)
                            Toast.makeText(context, "تم حذف الإعلان", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    if (showAddAdDialog) {
        var title by remember { mutableStateOf("") }
        var subtitle by remember { mutableStateOf("") }
        var imageUrl by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddAdDialog = false },
            title = { Text("إضافة إعلان ممول جديد") },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("عنوان الإعلان") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = subtitle,
                        onValueChange = { subtitle = it },
                        label = { Text("الوصف الفرعي") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("رابط صورة الإعلان (URL)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addSponsoredAd(
                                title = title,
                                subtitle = subtitle,
                                imageUrl = imageUrl.ifEmpty { "https://images.unsplash.com/photo-1542838132-92c53300491e?w=800&q=80" },
                                targetCategory = "ALL"
                            )
                            showAddAdDialog = false
                        }
                    }
                ) {
                    Text("حفظ ونشر")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAdDialog = false }) { Text("إلغاء") }
            }
        )
    }
}

// 8. SETTINGS & FINANCIALS TAB
@Composable
fun AdminSettingsTab(
    viewModel: AlBaqalViewModel,
    isSuperAdmin: Boolean
) {
    val settings by viewModel.systemSettings.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var deliveryFeeStr by remember(settings) { mutableStateOf(settings.deliveryFee.toString()) }
    var commissionStr by remember(settings) { mutableStateOf(settings.appCommissionPercent.toString()) }
    var subFeeStr by remember(settings) { mutableStateOf(settings.monthlySubscriptionFee.toString()) }
    var phoneStr by remember(settings) { mutableStateOf(settings.adminSupportPhone) }
    var whatsappStr by remember(settings) { mutableStateOf(settings.adminWhatsAppNumber) }
    var vodaCashStr by remember(settings) { mutableStateOf(settings.vodafoneCashNumber) }
    var instaPayStr by remember(settings) { mutableStateOf(settings.instaPayHandle) }
    var captainEnabled by remember(settings) { mutableStateOf(settings.captainFleetEnabled) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(text = "الإعدادات العامة والمالية للنظام", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "إعدادات الرسوم والعمولات:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = deliveryFeeStr,
                        onValueChange = { deliveryFeeStr = it },
                        label = { Text("مصاريف الشحن الافتراضية (ج.م)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = commissionStr,
                        onValueChange = { commissionStr = it },
                        label = { Text("نسبة عمولة التطبيق على التاجر (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = subFeeStr,
                        onValueChange = { subFeeStr = it },
                        label = { Text("سعر الاشتراك الشهري في الشحن للعميل (ج.م)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "أرقام الدعم وحسابات التحويل (فودافون كاش / انستاباي):", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = whatsappStr,
                        onValueChange = { whatsappStr = it },
                        label = { Text("رقم وتساب الإدارة للدعم (دولي مثلاً 2010...)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = phoneStr,
                        onValueChange = { phoneStr = it },
                        label = { Text("هاتف الاتصال للدعم") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = vodaCashStr,
                        onValueChange = { vodaCashStr = it },
                        label = { Text("رقم فودافون كاش لاستلام المديونيات") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = instaPayStr,
                        onValueChange = { instaPayStr = it },
                        label = { Text("عنوان انستاباي (IPA Handle)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "واجهة تعيين كباتن التوصيل", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = "يمكن إغلاق هذه الواجهة وإخفاؤها تماماً من التطبيق لحين تجهيز أسطول الكباتن",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = captainEnabled,
                        onCheckedChange = { captainEnabled = it }
                    )
                }
            }
        }

        item {
            Button(
                onClick = {
                    val updated = settings.copy(
                        deliveryFee = deliveryFeeStr.toDoubleOrNull() ?: 20.0,
                        appCommissionPercent = commissionStr.toDoubleOrNull() ?: 5.0,
                        monthlySubscriptionFee = subFeeStr.toDoubleOrNull() ?: 99.0,
                        adminSupportPhone = phoneStr.trim(),
                        adminWhatsAppNumber = whatsappStr.trim(),
                        vodafoneCashNumber = vodaCashStr.trim(),
                        instaPayHandle = instaPayStr.trim(),
                        captainFleetEnabled = captainEnabled
                    )
                    viewModel.updateSystemSettings(updated)
                    Toast.makeText(context, "تم حفظ وتحديث الإعدادات بنجاح!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("حفظ التغييرات في الإعدادات والماليات", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 9. GOVERNORATES & DISTRICTS TAB
@Composable
fun AdminLocationsTab(
    viewModel: AlBaqalViewModel
) {
    val customLocations by viewModel.customLocations.collectAsStateWithLifecycle()
    var newGov by remember { mutableStateOf("") }
    var newDist by remember { mutableStateOf("") }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(text = "إضافة وتثبيت محافظات وأحياء جديدة", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "يمكن للإدارة إضافة أي محافظة أو منطقة جديدة لتظهر تلقائياً في القوائم المنسدلة للعميل والتاجر:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    OutlinedTextField(
                        value = newGov,
                        onValueChange = { newGov = it },
                        label = { Text("اسم المحافظة الجديدة") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newDist,
                        onValueChange = { newDist = it },
                        label = { Text("اسم الحي أو القسم أو المنطقة") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (newGov.isNotBlank() && newDist.isNotBlank()) {
                                viewModel.addCustomLocation(newGov, newDist)
                                Toast.makeText(context, "تمت إضافة المنطقة بنجاح للقوائم!", Toast.LENGTH_SHORT).show()
                                newGov = ""
                                newDist = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("إضافة وتثبيت المنطقة")
                    }
                }
            }
        }

        item {
            Text(text = "المناطق المضافة يدوياً:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        items(customLocations, key = { it.id }) { loc ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${loc.governorate} - ${loc.district}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

// 10. CAPTAINS FLEET TAB
@Composable
fun AdminCaptainsTab(
    viewModel: AlBaqalViewModel
) {
    val orders by viewModel.allOrders.collectAsStateWithLifecycle()
    val deliveryOrders = remember(orders) {
        orders.filter { it.captainName.isNotEmpty() }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(text = "أسطول كباتن التوصيل والمناديب", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "متابعة الكباتن والطلبات الجاري تسليمها في أنحاء الإسكندرية والمحافظات:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        if (deliveryOrders.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("لا توجد شحنات مع الكباتن حالياً", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(deliveryOrders, key = { it.id }) { order ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "كابتن: ${order.captainName}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(text = order.captainPhone, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Text(text = "الطلب #${order.id} | العميل: ${order.customerName}", fontSize = 12.sp)
                        Text(text = "العنوان: ${order.governorate} - ${order.district}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
