package com.example.albakal.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.albakal.data.model.OrderStatus
import com.example.albakal.data.model.PaymentMethod
import com.example.albakal.data.model.ProductCategory
import com.example.albakal.data.model.SettlementStatus
import com.example.albakal.data.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email: String,
    val passwordHash: String,
    val fullName: String,
    val phone: String,
    val role: UserRole,
    val governorate: String,
    val district: String,
    val street: String,
    val building: String = "",
    val floor: String = "",
    val apartment: String = "",
    val isApproved: Boolean = true,
    val isBanned: Boolean = false,
    val banReason: String = "",
    val supervisorPermissions: String = "", // Comma-separated: "PRODUCTS,ORDERS,MERCHANTS,ADS,SETTINGS"
    val isMonthlySubscribed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val merchantId: Long,
    val merchantName: String,
    val title: String,
    val description: String,
    val category: ProductCategory,
    val price: Double,
    val discountPrice: Double? = null,
    val unit: String = "قطعة",
    val imageUrl: String = "",
    val isAvailable: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val governorate: String,
    val district: String,
    val merchantId: Long,
    val merchantName: String,
    val itemsSummary: String, // Readable summary: "2x موز بلدي، 1x لبن جهينة"
    val totalProductAmount: Double,
    val deliveryFee: Double,
    val grandTotal: Double,
    val paymentMethod: PaymentMethod,
    val status: OrderStatus = OrderStatus.RECEIVED,
    val captainName: String = "",
    val captainPhone: String = "",
    val isPreparingSoundAcknowledged: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "debt_settlements")
data class DebtSettlementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val merchantId: Long,
    val merchantName: String,
    val amount: Double,
    val paymentMethod: String, // "فودافون كاش" or "انستاباي"
    val receiptScreenshotUri: String = "",
    val status: SettlementStatus = SettlementStatus.PENDING,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "password_recovery_requests")
data class PasswordRecoveryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phone: String,
    val userEmail: String,
    val userName: String,
    val userPassword: String,
    val userRole: UserRole,
    val isResolved: Boolean = false,
    val requestedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sponsored_ads")
data class SponsoredAdEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val targetCategory: String = "ALL",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "system_settings")
data class SystemSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val deliveryFee: Double = 20.0,
    val appCommissionPercent: Double = 5.0,
    val monthlySubscriptionFee: Double = 99.0,
    val adminSupportPhone: String = "01012345678",
    val adminWhatsAppNumber: String = "201012345678",
    val vodafoneCashNumber: String = "01012345678",
    val instaPayHandle: String = "albakal@instapay",
    val captainFleetEnabled: Boolean = true
)

@Entity(tableName = "custom_locations")
data class CustomLocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val governorate: String,
    val district: String
)
