package com.example.albakal.ui.screens.merchant

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.albakal.data.local.OrderEntity
import com.example.albakal.data.local.ProductEntity
import com.example.albakal.data.model.OrderStatus
import com.example.albakal.data.model.ProductCategory
import com.example.albakal.data.model.UserSession
import com.example.albakal.ui.AlBaqalViewModel
import com.example.albakal.ui.common.MerchantHonorCodeDialog
import com.example.albakal.ui.common.OrderStatusBadge
import com.example.albakal.ui.common.StatCard
import com.example.albakal.ui.common.WhatsAppHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantContainerScreen(
    user: UserSession,
    viewModel: AlBaqalViewModel,
    modifier: Modifier = Modifier
) {
    var selectedBottomNav by remember { mutableIntStateOf(0) }
    var showLogoutConfirm by remember { mutableStateOf(false) }

    val isHonorCodeAccepted by viewModel.isHonorCodeAccepted.collectAsStateWithLifecycle()
    val hasPendingSoundAlert by viewModel.hasPendingSoundAlert.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val merchantOrders = remember(allOrders, user.id) {
        allOrders.filter { it.merchantId == user.id }
    }
    val newOrdersCount = merchantOrders.count { it.status == OrderStatus.RECEIVED }

    // Intercept back button so merchant stays inside app unless they click logout
    BackHandler {
        if (selectedBottomNav != 0) {
            selectedBottomNav = 0
        } else {
            showLogoutConfirm = true
        }
    }

    // Show Honor Code dialog if not yet accepted in this session
    if (!isHonorCodeAccepted) {
        MerchantHonorCodeDialog(
            onAccept = { viewModel.acceptHonorCode() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.fullName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "تاجر",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${user.governorate} - ${user.district}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showLogoutConfirm = true },
                        modifier = Modifier.testTag("merchant_logout_top")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "تسجيل خروج",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedBottomNav == 0,
                    onClick = { selectedBottomNav = 0 },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (newOrdersCount > 0) {
                                    Badge { Text("$newOrdersCount") }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = "الطلبات")
                        }
                    },
                    label = { Text("الطلبات") },
                    modifier = Modifier.testTag("merchant_nav_orders")
                )
                NavigationBarItem(
                    selected = selectedBottomNav == 1,
                    onClick = { selectedBottomNav = 1 },
                    icon = { Icon(Icons.Default.Store, contentDescription = "بضائعي") },
                    label = { Text("بضائعي") },
                    modifier = Modifier.testTag("merchant_nav_products")
                )
                NavigationBarItem(
                    selected = selectedBottomNav == 2,
                    onClick = { selectedBottomNav = 2 },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "الماليات") },
                    label = { Text("الماليات والمديونية") },
                    modifier = Modifier.testTag("merchant_nav_finance")
                )
                NavigationBarItem(
                    selected = selectedBottomNav == 3,
                    onClick = { selectedBottomNav = 3 },
                    icon = { Icon(Icons.Default.SupportAgent, contentDescription = "الدعم") },
                    label = { Text("الدعم") },
                    modifier = Modifier.testTag("merchant_nav_support")
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Audio Alarm Banner when new order is pending
            AnimatedVisibility(visible = hasPendingSoundAlert) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تنبيه صوتي مستمر: وصل طلب جديد! اضغط «جاري تحضير المنتج» لإيقاف الرنين.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedBottomNav) {
                    0 -> MerchantOrdersTab(user = user, viewModel = viewModel)
                    1 -> MerchantProductsTab(user = user, viewModel = viewModel)
                    2 -> MerchantFinanceTab(user = user, viewModel = viewModel)
                    3 -> MerchantSupportTab(user = user, viewModel = viewModel, onLogout = { showLogoutConfirm = true })
                }
            }
        }
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text("تسجيل الخروج") },
            text = { Text("هل تريد بالتأكيد تسجيل الخروج من لوحة التاجر؟") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        viewModel.logout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("نعم، تسجيل خروج")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun MerchantOrdersTab(
    user: UserSession,
    viewModel: AlBaqalViewModel
) {
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val merchantOrders = remember(allOrders, user.id) {
        allOrders.filter { it.merchantId == user.id }
    }
    val context = LocalContext.current

    var dispatchDialogOrder by remember { mutableStateOf<OrderEntity?>(null) }

    if (merchantOrders.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "لا توجد طلبات واردة حالياً", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "عندما يطلب العميل من متجرك سيصلك تنبيه فوري وصوت رنين مستمر حتى تبدأ بالتحضير",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "الطلبات الواردة لمتجرك (${merchantOrders.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }

        items(merchantOrders, key = { it.id }) { order ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (order.status == OrderStatus.RECEIVED) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
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
                        Text(text = "طلب #${order.id}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        OrderStatusBadge(status = order.status)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "العميل: ${order.customerName} (${order.customerPhone})",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "العنوان: ${order.governorate} - ${order.district} - ${order.deliveryAddress}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "السلع: ${order.itemsSummary}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "الدفع: ${order.paymentMethod.displayNameAr}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "قيمة المنتجات: ${order.totalProductAmount} ج.م",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    if (order.captainName.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "المندوب: ${order.captainName} (${order.captainPhone})",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ACTION BUTTONS BASED ON STATE
                    when (order.status) {
                        OrderStatus.RECEIVED -> {
                            Button(
                                onClick = {
                                    viewModel.prepareOrder(order.id)
                                    Toast.makeText(context, "تم إيقاف الرنين والبدء في تحضير الطلب", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("merchant_prepare_order_${order.id}")
                            ) {
                                Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("جاري تحضير المنتج (إيقاف الرنين)")
                            }
                        }
                        OrderStatus.PREPARING -> {
                            Button(
                                onClick = { dispatchDialogOrder = order },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("merchant_dispatch_order_${order.id}")
                            ) {
                                Icon(Icons.Default.DeliveryDining, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("جاري إرسال المنتج مع المندوب")
                            }
                        }
                        OrderStatus.OUT_FOR_DELIVERY -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.markOrderDelivered(order.id)
                                        Toast.makeText(context, "تم تأكيد تسليم الطلب للعميل بنجاح", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("تم التسليم", fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = {
                                        WhatsAppHelper.makePhoneCall(context, order.customerPhone)
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("اتصال بالعميل", fontSize = 12.sp)
                                }
                            }
                        }
                        OrderStatus.DELIVERED -> {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "تم تسليم الطلب بنجاح وإغلاقه",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                        OrderStatus.CANCELLED -> {}
                    }
                }
            }
        }
    }

    // DISPATCH ORDER MODAL (Input Captain Name & Phone)
    if (dispatchDialogOrder != null) {
        val currentOrder = dispatchDialogOrder!!
        var captainName by remember { mutableStateOf("") }
        var captainPhone by remember { mutableStateOf("") }
        var dispatchError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { dispatchDialogOrder = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeliveryDining,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "إرسال الطلب مع المندوب / الكابتن",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "اكتب اسم ورقم هاتف المندوب ليتمكن العميل من متابعة طلبه والتواصل معه مباشرة:",
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = captainName,
                        onValueChange = { captainName = it },
                        label = { Text("اسم المندوب / الكابتن") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("captain_name_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = captainPhone,
                        onValueChange = { captainPhone = it },
                        label = { Text("رقم هاتف المندوب") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("captain_phone_input"),
                        shape = RoundedCornerShape(10.dp)
                    )
                    if (dispatchError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = dispatchError ?: "", color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (captainName.isBlank() || captainPhone.isBlank()) {
                            dispatchError = "يرجى كتابة اسم ورقم هاتف المندوب"
                            return@Button
                        }
                        viewModel.sendOrderWithCaptain(currentOrder.id, captainName.trim(), captainPhone.trim())
                        Toast.makeText(context, "تم تحويل الطلب للكابتن وإرسال إشعار للعميل!", Toast.LENGTH_LONG).show()
                        dispatchDialogOrder = null
                    },
                    modifier = Modifier.testTag("confirm_dispatch_button")
                ) {
                    Text("إرسال الطلب")
                }
            },
            dismissButton = {
                TextButton(onClick = { dispatchDialogOrder = null }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantProductsTab(
    user: UserSession,
    viewModel: AlBaqalViewModel
) {
    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    val myProducts = remember(allProducts, user.id) {
        allProducts.filter { it.merchantId == user.id }
    }
    val context = LocalContext.current

    var showAddDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "إدارة بضائع متجرك", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(text = "${myProducts.size} منتجات معروضة", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.testTag("merchant_add_product_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة سلعة", fontSize = 12.sp)
                }
            }
        }

        items(myProducts, key = { it.id }) { product ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        if (product.imageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = product.imageUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Store,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(28.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = product.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "القسم: ${product.category.displayNameAr}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "${product.price} ج.م", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                            if (product.discountPrice != null) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "(عرض: ${product.discountPrice} ج.م)", color = MaterialTheme.colorScheme.secondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    IconButton(onClick = { editingProduct = product }) {
                        Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = {
                        viewModel.deleteProduct(product.id)
                        Toast.makeText(context, "تم حذف المنتج", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }

    // ADD / EDIT PRODUCT DIALOG
    if (showAddDialog || editingProduct != null) {
        val isEditing = editingProduct != null
        var title by remember { mutableStateOf(editingProduct?.title ?: "") }
        var description by remember { mutableStateOf(editingProduct?.description ?: "") }
        var category by remember { mutableStateOf(editingProduct?.category ?: ProductCategory.FRUITS) }
        var priceStr by remember { mutableStateOf(editingProduct?.price?.toString() ?: "") }
        var discountPriceStr by remember { mutableStateOf(editingProduct?.discountPrice?.toString() ?: "") }
        var unit by remember { mutableStateOf(editingProduct?.unit ?: "1 كجم") }
        var imageUrl by remember { mutableStateOf(editingProduct?.imageUrl ?: "") }
        var catExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                editingProduct = null
            },
            title = {
                Text(if (isEditing) "تعديل السلعة" else "إضافة سلعة جديدة", fontWeight = FontWeight.Bold)
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("اسم السلعة / المنتج") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("product_title_input"),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Category Dropdown
                        ExposedDropdownMenuBox(
                            expanded = catExpanded,
                            onExpandedChange = { catExpanded = !catExpanded }
                        ) {
                            OutlinedTextField(
                                value = category.displayNameAr,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("القسم") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = catExpanded,
                                onDismissRequest = { catExpanded = false }
                            ) {
                                ProductCategory.values().forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat.displayNameAr) },
                                        onClick = {
                                            category = cat
                                            catExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = priceStr,
                                onValueChange = { priceStr = it },
                                label = { Text("السعر (ج.م)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = discountPriceStr,
                                onValueChange = { discountPriceStr = it },
                                label = { Text("سعر العرض (اختياري)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("الوحدة (مثال: 1 كجم، طبق، عبوة، لتر)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = imageUrl,
                            onValueChange = { imageUrl = it },
                            label = { Text("رابط صورة المنتج (URL)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("وصف السلعة") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val price = priceStr.toDoubleOrNull() ?: 0.0
                        val discountPrice = discountPriceStr.toDoubleOrNull()
                        if (title.isBlank() || price <= 0.0) {
                            Toast.makeText(context, "يرجى كتابة اسم السلعة وسعر صحيح", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (isEditing) {
                            val updated = editingProduct!!.copy(
                                title = title.trim(),
                                description = description.trim(),
                                category = category,
                                price = price,
                                discountPrice = discountPrice,
                                unit = unit.trim(),
                                imageUrl = imageUrl.trim()
                            )
                            viewModel.updateProduct(updated)
                        } else {
                            viewModel.addProduct(
                                merchantId = user.id,
                                merchantName = user.fullName,
                                title = title.trim(),
                                description = description.trim(),
                                category = category,
                                price = price,
                                discountPrice = discountPrice,
                                unit = unit.trim(),
                                imageUrl = imageUrl.trim()
                            )
                        }
                        showAddDialog = false
                        editingProduct = null
                    }
                ) {
                    Text(if (isEditing) "حفظ التعديلات" else "إضافة السلعة")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddDialog = false
                    editingProduct = null
                }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun MerchantFinanceTab(
    user: UserSession,
    viewModel: AlBaqalViewModel
) {
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val allDebtSettlements by viewModel.allDebtSettlements.collectAsStateWithLifecycle()
    val settings by viewModel.systemSettings.collectAsStateWithLifecycle()

    val myOrders = remember(allOrders, user.id) {
        allOrders.filter { it.merchantId == user.id && it.status == OrderStatus.DELIVERED }
    }
    val mySettlements = remember(allDebtSettlements, user.id) {
        allDebtSettlements.filter { it.merchantId == user.id }
    }

    // Monthly profits and debt calculations
    val totalDeliveredSales = myOrders.sumOf { it.totalProductAmount }
    val commissionRate = settings.appCommissionPercent / 100.0
    val totalAppCommissions = totalDeliveredSales * commissionRate
    val totalApprovedSettlements = mySettlements.filter { it.status.name == "APPROVED" }.sumOf { it.amount }
    val currentDebt = (totalAppCommissions - totalApprovedSettlements).coerceAtLeast(0.0)
    val netMonthlyProfits = (totalDeliveredSales - totalAppCommissions).coerceAtLeast(0.0)

    var showSettleDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(text = "المعاملات المالية والمديونيات", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        item {
            StatCard(
                title = "الأرباح الشهرية الصافية",
                value = "$netMonthlyProfits ج.م",
                subtitle = "تُصفر تلقائياً كل شهر بعد تسوية الحسابات",
                icon = Icons.Default.MonetizationOn,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        item {
            StatCard(
                title = "المديونية الحالية (عمولة التطبيق ${settings.appCommissionPercent}%)",
                value = "$currentDebt ج.م",
                subtitle = if (currentDebt > 0) "يمكنك تسديد جزء أو المبلغ بالكامل ورفع إيصال التحويل" else "لا توجد مديونيات متأخرة عليك",
                icon = Icons.Default.AccountBalanceWallet,
                containerColor = if (currentDebt > 0) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (currentDebt > 0) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Button(
                onClick = { showSettleDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("merchant_pay_debt_button")
            ) {
                Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("تسديد المديونية ورفع إثبات التحويل")
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "سجل طلبات تسديد المديونية:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        if (mySettlements.isEmpty()) {
            item {
                Text(
                    text = "لم تقم بتقديم طلبات سداد سابقة",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(mySettlements, key = { it.id }) { s ->
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
                        Column {
                            Text(text = "سداد مبلغ: ${s.amount} ج.م", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "طريقة الدفع: ${s.paymentMethod}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(
                            color = when (s.status.name) {
                                "APPROVED" -> Color(0xFFDCFCE7)
                                "PENDING" -> Color(0xFFFEF3C7)
                                else -> Color(0xFFFEE2E2)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = s.status.displayNameAr,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (s.status.name) {
                                    "APPROVED" -> Color(0xFF15803D)
                                    "PENDING" -> Color(0xFFB45309)
                                    else -> Color(0xFFB91C1C)
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // SETTLE DEBT DIALOG
    if (showSettleDialog) {
        var amountStr by remember { mutableStateOf(if (currentDebt > 0) currentDebt.toString() else "") }
        var selectedMethod by remember { mutableStateOf("فودافون كاش") }
        var screenshotUri by remember { mutableStateOf("") }
        val context = LocalContext.current

        AlertDialog(
            onDismissRequest = { showSettleDialog = false },
            title = {
                Text("تسديد المديونية ورفع إثبات الدفع", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "اختر طريقة الدفع وحوّل المبلغ ثم ارفع لقطة الشاشة (إثبات الدفع) لتأكيد الإدارة وتصفير مديونيتك:",
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "حسابات تحويل مديونية تطبيق البقال:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text(text = "• فودافون كاش: ${settings.vodafoneCashNumber}", fontSize = 11.sp)
                            Text(text = "• انستاباي: ${settings.instaPayHandle}", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("المبلغ المراد سداده (ج.م)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("settle_amount_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedMethod == "فودافون كاش") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedMethod = "فودافون كاش" }
                        ) {
                            Text(
                                text = "فودافون كاش",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedMethod == "انستاباي") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedMethod = "انستاباي" }
                        ) {
                            Text(
                                text = "انستاباي",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = screenshotUri,
                        onValueChange = { screenshotUri = it },
                        label = { Text("رابط إثبات التحويل أو رقم المعاملة") },
                        placeholder = { Text("أدخل رقم العملية أو رابط لقطة الشاشة") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = amountStr.toDoubleOrNull() ?: 0.0
                        if (amount <= 0.0) {
                            Toast.makeText(context, "يرجى تحديد مبلغ سداد صحيح", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        viewModel.submitDebtSettlement(
                            merchantId = user.id,
                            merchantName = user.fullName,
                            amount = amount,
                            paymentMethod = selectedMethod,
                            receiptScreenshotUri = screenshotUri.ifEmpty { "إيصال معتمد - رقم العملية #${System.currentTimeMillis() % 100000}" }
                        )
                        Toast.makeText(context, "تم إرسال إثبات السداد للإدارة بنجاح!", Toast.LENGTH_LONG).show()
                        showSettleDialog = false
                    },
                    modifier = Modifier.testTag("submit_debt_settlement_button")
                ) {
                    Text("إرسال الإثبات")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSettleDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun MerchantSupportTab(
    user: UserSession,
    viewModel: AlBaqalViewModel,
    onLogout: () -> Unit
) {
    val settings by viewModel.systemSettings.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showSuggestionDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "دعم التجار والشكاوى والاقتراحات", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "يمكنك التواصل المباشر مع إدارة البقال لحل أي مشكلة أو الاستفسار عن المديونيات أو تقديم اقتراحات لتطوير النظام.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showSuggestionDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("كتابة اقتراح/شكوى", fontSize = 12.sp)
                        }
                        Button(
                            onClick = {
                                val msg = "مرحباً إدارة البقال، أنا التاجر ${user.fullName} (هاتف: ${user.phone})، لدي استفسار بخصوص المتجر:"
                                WhatsAppHelper.openWhatsApp(context, settings.adminWhatsAppNumber, msg)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("وتساب الإدارة", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("merchant_logout_button")
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("تسجيل الخروج من حساب التاجر")
            }
        }
    }

    if (showSuggestionDialog) {
        var text by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showSuggestionDialog = false },
            title = { Text("صندوق الاقتراحات والشكاوى") },
            text = {
                Column {
                    Text("اكتب رسالتك وستصل للإدارة فوراً:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (text.isNotBlank()) {
                            Toast.makeText(context, "تم إرسال رسالتك لإدارة البقال بنجاح", Toast.LENGTH_LONG).show()
                            showSuggestionDialog = false
                        }
                    }
                ) {
                    Text("إرسال")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSuggestionDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}
