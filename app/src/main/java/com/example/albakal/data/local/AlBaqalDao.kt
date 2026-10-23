package com.example.albakal.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.albakal.data.model.OrderStatus
import com.example.albakal.data.model.SettlementStatus
import com.example.albakal.data.model.UserRole
import kotlinx.coroutines.flow.Flow

@Dao
interface AlBaqalDao {
    // Users
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserByIdFlow(id: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE role = :role ORDER BY createdAt DESC")
    fun getUsersByRole(role: UserRole): Flow<List<UserEntity>>

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: Long)

    @Query("UPDATE users SET isApproved = :approved WHERE id = :userId")
    suspend fun updateUserApproval(userId: Long, approved: Boolean)

    @Query("UPDATE users SET isBanned = :banned, banReason = :reason WHERE id = :userId")
    suspend fun updateUserBanStatus(userId: Long, banned: Boolean, reason: String)

    @Query("UPDATE users SET isMonthlySubscribed = :subscribed WHERE id = :userId")
    suspend fun updateMonthlySubscription(userId: Long, subscribed: Boolean)

    // Products
    @Query("SELECT * FROM products ORDER BY createdAt DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE merchantId = :merchantId ORDER BY createdAt DESC")
    fun getProductsByMerchant(merchantId: Long): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProduct(id: Long)

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): ProductEntity?

    // Orders
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getOrdersByCustomer(customerId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE merchantId = :merchantId ORDER BY createdAt DESC")
    fun getOrdersByMerchant(merchantId: Long): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE merchantId = :merchantId AND status = :status AND isPreparingSoundAcknowledged = 0")
    fun getUnacknowledgedReceivedOrders(merchantId: Long, status: OrderStatus = OrderStatus.RECEIVED): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :status, isPreparingSoundAcknowledged = 1 WHERE id = :orderId")
    suspend fun setOrderPreparing(orderId: Long, status: OrderStatus = OrderStatus.PREPARING)

    @Query("UPDATE orders SET status = :status, captainName = :captainName, captainPhone = :captainPhone WHERE id = :orderId")
    suspend fun setOrderOutForDelivery(orderId: Long, captainName: String, captainPhone: String, status: OrderStatus = OrderStatus.OUT_FOR_DELIVERY)

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: OrderStatus)

    // Debt Settlements
    @Query("SELECT * FROM debt_settlements ORDER BY createdAt DESC")
    fun getAllDebtSettlements(): Flow<List<DebtSettlementEntity>>

    @Query("SELECT * FROM debt_settlements WHERE merchantId = :merchantId ORDER BY createdAt DESC")
    fun getDebtSettlementsByMerchant(merchantId: Long): Flow<List<DebtSettlementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebtSettlement(settlement: DebtSettlementEntity): Long

    @Query("UPDATE debt_settlements SET status = :status WHERE id = :settlementId")
    suspend fun updateDebtSettlementStatus(settlementId: Long, status: SettlementStatus)

    // Password Recovery
    @Query("SELECT * FROM password_recovery_requests ORDER BY requestedAt DESC")
    fun getAllPasswordRecoveryRequests(): Flow<List<PasswordRecoveryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPasswordRecovery(req: PasswordRecoveryEntity): Long

    @Query("UPDATE password_recovery_requests SET isResolved = 1 WHERE id = :id")
    suspend fun markPasswordRecoveryResolved(id: Long)

    // Sponsored Ads
    @Query("SELECT * FROM sponsored_ads WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveSponsoredAds(): Flow<List<SponsoredAdEntity>>

    @Query("SELECT * FROM sponsored_ads ORDER BY createdAt DESC")
    fun getAllSponsoredAds(): Flow<List<SponsoredAdEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSponsoredAd(ad: SponsoredAdEntity): Long

    @Query("DELETE FROM sponsored_ads WHERE id = :id")
    suspend fun deleteSponsoredAd(id: Long)

    // System Settings
    @Query("SELECT * FROM system_settings WHERE id = 1 LIMIT 1")
    fun getSystemSettingsFlow(): Flow<SystemSettingsEntity?>

    @Query("SELECT * FROM system_settings WHERE id = 1 LIMIT 1")
    suspend fun getSystemSettings(): SystemSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: SystemSettingsEntity)

    // Custom Locations
    @Query("SELECT * FROM custom_locations ORDER BY governorate ASC")
    fun getAllCustomLocations(): Flow<List<CustomLocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomLocation(location: CustomLocationEntity): Long

    @Query("DELETE FROM custom_locations WHERE id = :id")
    suspend fun deleteCustomLocation(id: Long)
}
