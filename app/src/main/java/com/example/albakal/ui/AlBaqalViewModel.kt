package com.example.albakal.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.albakal.data.local.AppDatabase
import com.example.albakal.data.local.CustomLocationEntity
import com.example.albakal.data.local.DebtSettlementEntity
import com.example.albakal.data.local.OrderEntity
import com.example.albakal.data.local.PasswordRecoveryEntity
import com.example.albakal.data.local.ProductEntity
import com.example.albakal.data.local.SponsoredAdEntity
import com.example.albakal.data.local.SystemSettingsEntity
import com.example.albakal.data.local.UserEntity
import com.example.albakal.data.model.OrderStatus
import com.example.albakal.data.model.PaymentMethod
import com.example.albakal.data.model.ProductCategory
import com.example.albakal.data.model.SettlementStatus
import com.example.albakal.data.model.UserRole
import com.example.albakal.data.model.UserSession
import com.example.albakal.data.repository.AlBaqalRepository
import com.example.albakal.ui.common.SoundAlertManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlBaqalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AlBaqalRepository

    private val _currentUser = MutableStateFlow<UserSession?>(null)
    val currentUser: StateFlow<UserSession?> = _currentUser.asStateFlow()

    private val _isHonorCodeAccepted = MutableStateFlow(false)
    val isHonorCodeAccepted: StateFlow<Boolean> = _isHonorCodeAccepted.asStateFlow()

    // Cart: Product ID -> (Product, Quantity)
    private val _cart = MutableStateFlow<Map<Long, Pair<ProductEntity, Int>>>(emptyMap())
    val cart: StateFlow<Map<Long, Pair<ProductEntity, Int>>> = _cart.asStateFlow()

    // Active unacknowledged orders for ringing
    private val _hasPendingSoundAlert = MutableStateFlow(false)
    val hasPendingSoundAlert: StateFlow<Boolean> = _hasPendingSoundAlert.asStateFlow()

    val allUsers: StateFlow<List<UserEntity>>
    val allProducts: StateFlow<List<ProductEntity>>
    val allOrders: StateFlow<List<OrderEntity>>
    val allDebtSettlements: StateFlow<List<DebtSettlementEntity>>
    val allPasswordRecoveryRequests: StateFlow<List<PasswordRecoveryEntity>>
    val activeSponsoredAds: StateFlow<List<SponsoredAdEntity>>
    val allSponsoredAds: StateFlow<List<SponsoredAdEntity>>
    val systemSettings: StateFlow<SystemSettingsEntity>
    val customLocations: StateFlow<List<CustomLocationEntity>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AlBaqalRepository(database.albakalDao())

        allUsers = repository.allUsers.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allProducts = repository.allProducts.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allOrders = repository.allOrders.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allDebtSettlements = repository.allDebtSettlements.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allPasswordRecoveryRequests = repository.allPasswordRecoveryRequests.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        activeSponsoredAds = repository.activeSponsoredAds.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allSponsoredAds = repository.allSponsoredAds.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        systemSettings = repository.systemSettings
            .combine(MutableStateFlow(SystemSettingsEntity())) { settings, default ->
                settings ?: default
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SystemSettingsEntity())

        customLocations = repository.customLocations.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        // Seed initial data
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }

        // Monitor orders for merchant audio alarm
        viewModelScope.launch {
            combine(currentUser, allOrders) { user, orders ->
                if (user != null && user.role == UserRole.MERCHANT) {
                    val unackOrders = orders.filter {
                        it.merchantId == user.id &&
                                it.status == OrderStatus.RECEIVED &&
                                !it.isPreparingSoundAcknowledged
                    }
                    unackOrders.isNotEmpty()
                } else {
                    false
                }
            }.collect { shouldRing ->
                _hasPendingSoundAlert.value = shouldRing
                if (shouldRing) {
                    SoundAlertManager.startRepeatingOrderAlert(viewModelScope)
                } else {
                    SoundAlertManager.stopOrderAlert()
                }
            }
        }
    }

    // Auth methods
    suspend fun login(email: String, pass: String): LoginResult {
        val cleanEmail = email.trim().lowercase()
        val user = repository.getUserByEmail(cleanEmail)
            ?: return LoginResult.Error("البريد الإلكتروني غير مسجل بالتطبيق")

        if (user.passwordHash != pass.trim()) {
            return LoginResult.Error("كلمة المرور غير صحيحة، يرجى المحاولة مرة أخرى")
        }

        if (user.isBanned) {
            return LoginResult.Error("تم حظر هذا الحساب من قبل الإدارة. سبب الحظر: ${user.banReason.ifEmpty { "مخالفة الشروط والسياسات" }}")
        }

        val session = UserSession(
            id = user.id,
            email = user.email,
            fullName = user.fullName,
            phone = user.phone,
            role = user.role,
            governorate = user.governorate,
            district = user.district,
            streetAddress = "${user.street} ${if (user.building.isNotEmpty()) "عمارة ${user.building}" else ""} ${if (user.floor.isNotEmpty()) "الدور ${user.floor}" else ""} ${if (user.apartment.isNotEmpty()) "شقة ${user.apartment}" else ""}".trim(),
            isApproved = user.isApproved,
            isBanned = user.isBanned,
            banReason = user.banReason,
            supervisorPermissions = user.supervisorPermissions.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet(),
            isMonthlySubscribed = user.isMonthlySubscribed
        )
        _currentUser.value = session
        _isHonorCodeAccepted.value = (user.role != UserRole.MERCHANT)
        return LoginResult.Success(session)
    }

    suspend fun register(
        email: String,
        pass: String,
        fullName: String,
        phone: String,
        role: UserRole,
        governorate: String,
        district: String,
        street: String,
        building: String,
        floor: String,
        apartment: String
    ): RegisterResult {
        val cleanEmail = email.trim().lowercase()
        if (repository.getUserByEmail(cleanEmail) != null) {
            return RegisterResult.Error("هذا البريد الإلكتروني مسجل مسبقاً")
        }
        if (repository.getUserByPhone(phone.trim()) != null) {
            return RegisterResult.Error("رقم الهاتف هذا مسجل مسبقاً بحساب آخر")
        }

        val newEntity = UserEntity(
            email = cleanEmail,
            passwordHash = pass.trim(),
            fullName = fullName.trim(),
            phone = phone.trim(),
            role = role,
            governorate = governorate,
            district = district,
            street = street.trim(),
            building = building.trim(),
            floor = floor.trim(),
            apartment = apartment.trim(),
            isApproved = true // Immediate activation enabled so users can test both customer and merchant immediately
        )
        val id = repository.registerUser(newEntity)
        val session = UserSession(
            id = id,
            email = newEntity.email,
            fullName = newEntity.fullName,
            phone = newEntity.phone,
            role = newEntity.role,
            governorate = newEntity.governorate,
            district = newEntity.district,
            streetAddress = "${newEntity.street} ${if (newEntity.building.isNotEmpty()) "عمارة ${newEntity.building}" else ""}".trim(),
            isApproved = newEntity.isApproved,
            isBanned = false,
            isMonthlySubscribed = false
        )
        _currentUser.value = session
        _isHonorCodeAccepted.value = (role != UserRole.MERCHANT)
        return RegisterResult.Success(session)
    }

    fun acceptHonorCode() {
        _isHonorCodeAccepted.value = true
    }

    suspend fun requestPasswordRecovery(phone: String): Boolean {
        return repository.requestPasswordRecovery(phone)
    }

    fun logout() {
        SoundAlertManager.stopOrderAlert()
        _currentUser.value = null
        _cart.value = emptyMap()
        _isHonorCodeAccepted.value = false
    }

    // Cart Operations
    fun addToCart(product: ProductEntity) {
        val current = _cart.value.toMutableMap()
        val existing = current[product.id]
        if (existing != null) {
            current[product.id] = Pair(product, existing.second + 1)
        } else {
            current[product.id] = Pair(product, 1)
        }
        _cart.value = current
    }

    fun removeFromCart(productId: Long) {
        val current = _cart.value.toMutableMap()
        val existing = current[productId] ?: return
        if (existing.second > 1) {
            current[productId] = Pair(existing.first, existing.second - 1)
        } else {
            current.remove(productId)
        }
        _cart.value = current
    }

    fun deleteFromCart(productId: Long) {
        val current = _cart.value.toMutableMap()
        current.remove(productId)
        _cart.value = current
    }

    fun clearCart() {
        _cart.value = emptyMap()
    }

    // Place Order
    suspend fun checkoutOrder(
        paymentMethod: PaymentMethod,
        merchantId: Long,
        merchantName: String
    ): Boolean {
        val user = _currentUser.value ?: return false
        val cartItems = _cart.value.values.toList()
        if (cartItems.isEmpty()) return false

        val totalProduct = cartItems.sumOf { (product, qty) ->
            val unitPrice = product.discountPrice ?: product.price
            unitPrice * qty
        }

        val deliveryFee = if (user.isMonthlySubscribed) 0.0 else systemSettings.value.deliveryFee
        val grandTotal = totalProduct + deliveryFee

        val summary = cartItems.joinToString("، ") { (p, qty) ->
            "$qty x ${p.title}"
        }

        val order = OrderEntity(
            customerId = user.id,
            customerName = user.fullName,
            customerPhone = user.phone,
            deliveryAddress = user.streetAddress,
            governorate = user.governorate,
            district = user.district,
            merchantId = merchantId,
            merchantName = merchantName,
            itemsSummary = summary,
            totalProductAmount = totalProduct,
            deliveryFee = deliveryFee,
            grandTotal = grandTotal,
            paymentMethod = paymentMethod,
            status = OrderStatus.RECEIVED
        )

        repository.placeOrder(order)
        clearCart()
        return true
    }

    // Merchant Actions
    fun prepareOrder(orderId: Long) {
        viewModelScope.launch {
            repository.setOrderPreparing(orderId)
            SoundAlertManager.stopOrderAlert()
        }
    }

    fun sendOrderWithCaptain(orderId: Long, captainName: String, captainPhone: String) {
        viewModelScope.launch {
            repository.setOrderOutForDelivery(orderId, captainName, captainPhone)
        }
    }

    fun markOrderDelivered(orderId: Long) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, OrderStatus.DELIVERED)
        }
    }

    fun addProduct(
        merchantId: Long,
        merchantName: String,
        title: String,
        description: String,
        category: ProductCategory,
        price: Double,
        discountPrice: Double?,
        unit: String,
        imageUrl: String
    ) {
        viewModelScope.launch {
            repository.addProduct(
                ProductEntity(
                    merchantId = merchantId,
                    merchantName = merchantName,
                    title = title,
                    description = description,
                    category = category,
                    price = price,
                    discountPrice = discountPrice,
                    unit = unit,
                    imageUrl = imageUrl
                )
            )
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
        }
    }

    fun submitDebtSettlement(
        merchantId: Long,
        merchantName: String,
        amount: Double,
        paymentMethod: String,
        receiptScreenshotUri: String
    ) {
        viewModelScope.launch {
            repository.submitDebtSettlement(
                DebtSettlementEntity(
                    merchantId = merchantId,
                    merchantName = merchantName,
                    amount = amount,
                    paymentMethod = paymentMethod,
                    receiptScreenshotUri = receiptScreenshotUri,
                    status = SettlementStatus.PENDING
                )
            )
        }
    }

    // Customer Actions
    fun toggleMonthlySubscription(userId: Long, enable: Boolean) {
        viewModelScope.launch {
            repository.updateMonthlySubscription(userId, enable)
            if (_currentUser.value?.id == userId) {
                _currentUser.value = _currentUser.value?.copy(isMonthlySubscribed = enable)
            }
        }
    }

    // Admin Actions
    fun approveUser(userId: Long, approved: Boolean) {
        viewModelScope.launch {
            repository.updateUserApproval(userId, approved)
        }
    }

    fun banUser(userId: Long, banned: Boolean, reason: String) {
        viewModelScope.launch {
            repository.updateUserBanStatus(userId, banned, reason)
        }
    }

    fun deleteUser(userId: Long) {
        viewModelScope.launch {
            repository.deleteUser(userId)
        }
    }

    fun addSupervisor(
        email: String,
        pass: String,
        fullName: String,
        phone: String,
        permissions: Set<String>
    ) {
        viewModelScope.launch {
            repository.registerUser(
                UserEntity(
                    email = email.trim().lowercase(),
                    passwordHash = pass.trim(),
                    fullName = fullName.trim(),
                    phone = phone.trim(),
                    role = UserRole.SUPERVISOR,
                    governorate = "الإسكندرية",
                    district = "المركز الرئيسي",
                    street = "المقر الإداري",
                    isApproved = true,
                    isBanned = false,
                    supervisorPermissions = permissions.joinToString(",")
                )
            )
        }
    }

    fun approveDebtSettlement(settlementId: Long) {
        viewModelScope.launch {
            repository.updateDebtSettlementStatus(settlementId, SettlementStatus.APPROVED)
        }
    }

    fun resolvePasswordRecovery(requestId: Long) {
        viewModelScope.launch {
            repository.markPasswordRecoveryResolved(requestId)
        }
    }

    fun addSponsoredAd(title: String, subtitle: String, imageUrl: String, targetCategory: String) {
        viewModelScope.launch {
            repository.addSponsoredAd(
                SponsoredAdEntity(
                    title = title,
                    subtitle = subtitle,
                    imageUrl = imageUrl,
                    targetCategory = targetCategory
                )
            )
        }
    }

    fun deleteSponsoredAd(adId: Long) {
        viewModelScope.launch {
            repository.deleteSponsoredAd(adId)
        }
    }

    fun updateSystemSettings(settings: SystemSettingsEntity) {
        viewModelScope.launch {
            repository.updateSettings(settings)
        }
    }

    fun addCustomLocation(gov: String, dist: String) {
        viewModelScope.launch {
            repository.addCustomLocation(gov.trim(), dist.trim())
        }
    }

    fun updateAdminCredentials(newEmail: String, newPass: String) {
        viewModelScope.launch {
            val currentAdmin = _currentUser.value ?: return@launch
            val entity = repository.getUserById(currentAdmin.id) ?: return@launch
            val updated = entity.copy(
                email = newEmail.trim().lowercase(),
                passwordHash = newPass.trim()
            )
            repository.updateUser(updated)
            _currentUser.value = currentAdmin.copy(email = updated.email)
        }
    }
}

sealed class LoginResult {
    data class Success(val session: UserSession) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

sealed class RegisterResult {
    data class Success(val session: UserSession) : RegisterResult()
    data class Error(val message: String) : RegisterResult()
}
