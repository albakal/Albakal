package com.example.albakal.data.repository

import com.example.albakal.data.local.AlBaqalDao
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class AlBaqalRepository(private val dao: AlBaqalDao) {

    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()
    val allProducts: Flow<List<ProductEntity>> = dao.getAllProducts()
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()
    val allDebtSettlements: Flow<List<DebtSettlementEntity>> = dao.getAllDebtSettlements()
    val allPasswordRecoveryRequests: Flow<List<PasswordRecoveryEntity>> = dao.getAllPasswordRecoveryRequests()
    val activeSponsoredAds: Flow<List<SponsoredAdEntity>> = dao.getActiveSponsoredAds()
    val allSponsoredAds: Flow<List<SponsoredAdEntity>> = dao.getAllSponsoredAds()
    val systemSettings: Flow<SystemSettingsEntity?> = dao.getSystemSettingsFlow()
    val customLocations: Flow<List<CustomLocationEntity>> = dao.getAllCustomLocations()

    fun getProductsByMerchant(merchantId: Long): Flow<List<ProductEntity>> =
        dao.getProductsByMerchant(merchantId)

    fun getOrdersByCustomer(customerId: Long): Flow<List<OrderEntity>> =
        dao.getOrdersByCustomer(customerId)

    fun getOrdersByMerchant(merchantId: Long): Flow<List<OrderEntity>> =
        dao.getOrdersByMerchant(merchantId)

    fun getUnacknowledgedReceivedOrders(merchantId: Long): Flow<List<OrderEntity>> =
        dao.getUnacknowledgedReceivedOrders(merchantId)

    fun getDebtSettlementsByMerchant(merchantId: Long): Flow<List<DebtSettlementEntity>> =
        dao.getDebtSettlementsByMerchant(merchantId)

    suspend fun getUserByEmail(email: String): UserEntity? = withContext(Dispatchers.IO) {
        dao.getUserByEmail(email.trim().lowercase())
    }

    suspend fun getUserByPhone(phone: String): UserEntity? = withContext(Dispatchers.IO) {
        dao.getUserByPhone(phone.trim())
    }

    suspend fun getUserById(id: Long): UserEntity? = withContext(Dispatchers.IO) {
        dao.getUserById(id)
    }

    fun getUserByIdFlow(id: Long): Flow<UserEntity?> = dao.getUserByIdFlow(id)

    suspend fun registerUser(user: UserEntity): Long = withContext(Dispatchers.IO) {
        dao.insertUser(user.copy(email = user.email.trim().lowercase(), phone = user.phone.trim()))
    }

    suspend fun updateUser(user: UserEntity) = withContext(Dispatchers.IO) {
        dao.updateUser(user)
    }

    suspend fun deleteUser(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteUser(id)
    }

    suspend fun updateUserApproval(userId: Long, approved: Boolean) = withContext(Dispatchers.IO) {
        dao.updateUserApproval(userId, approved)
    }

    suspend fun updateUserBanStatus(userId: Long, banned: Boolean, reason: String) = withContext(Dispatchers.IO) {
        dao.updateUserBanStatus(userId, banned, reason)
    }

    suspend fun updateMonthlySubscription(userId: Long, subscribed: Boolean) = withContext(Dispatchers.IO) {
        dao.updateMonthlySubscription(userId, subscribed)
    }

    suspend fun addProduct(product: ProductEntity): Long = withContext(Dispatchers.IO) {
        dao.insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        dao.updateProduct(product)
    }

    suspend fun deleteProduct(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteProduct(id)
    }

    suspend fun placeOrder(order: OrderEntity): Long = withContext(Dispatchers.IO) {
        dao.insertOrder(order)
    }

    suspend fun setOrderPreparing(orderId: Long) = withContext(Dispatchers.IO) {
        dao.setOrderPreparing(orderId)
    }

    suspend fun setOrderOutForDelivery(orderId: Long, captainName: String, captainPhone: String) = withContext(Dispatchers.IO) {
        dao.setOrderOutForDelivery(orderId, captainName, captainPhone)
    }

    suspend fun updateOrderStatus(orderId: Long, status: OrderStatus) = withContext(Dispatchers.IO) {
        dao.updateOrderStatus(orderId, status)
    }

    suspend fun submitDebtSettlement(settlement: DebtSettlementEntity): Long = withContext(Dispatchers.IO) {
        dao.insertDebtSettlement(settlement)
    }

    suspend fun updateDebtSettlementStatus(settlementId: Long, status: SettlementStatus) = withContext(Dispatchers.IO) {
        dao.updateDebtSettlementStatus(settlementId, status)
    }

    suspend fun requestPasswordRecovery(phone: String): Boolean = withContext(Dispatchers.IO) {
        val user = dao.getUserByPhone(phone.trim())
        if (user != null) {
            dao.insertPasswordRecovery(
                PasswordRecoveryEntity(
                    phone = user.phone,
                    userEmail = user.email,
                    userName = user.fullName,
                    userPassword = user.passwordHash,
                    userRole = user.role
                )
            )
            true
        } else {
            false
        }
    }

    suspend fun markPasswordRecoveryResolved(id: Long) = withContext(Dispatchers.IO) {
        dao.markPasswordRecoveryResolved(id)
    }

    suspend fun addSponsoredAd(ad: SponsoredAdEntity): Long = withContext(Dispatchers.IO) {
        dao.insertSponsoredAd(ad)
    }

    suspend fun deleteSponsoredAd(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteSponsoredAd(id)
    }

    suspend fun updateSettings(settings: SystemSettingsEntity) = withContext(Dispatchers.IO) {
        dao.insertOrUpdateSettings(settings)
    }

    suspend fun addCustomLocation(gov: String, dist: String): Long = withContext(Dispatchers.IO) {
        dao.insertCustomLocation(CustomLocationEntity(governorate = gov, district = dist))
    }

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val admin = dao.getUserByEmail("mahmoudomran324@gmail.com")
        if (admin == null) {
            // Seed Admin
            dao.insertUser(
                UserEntity(
                    email = "mahmoudomran324@gmail.com",
                    passwordHash = "Aa&9119045",
                    fullName = "إدارة البقال المركزية",
                    phone = "01012345678",
                    role = UserRole.ADMIN,
                    governorate = "الإسكندرية",
                    district = "سيدي جابر",
                    street = "شارع المشير أحمد إسماعيل",
                    isApproved = true,
                    isBanned = false
                )
            )

            // Seed Settings
            dao.insertOrUpdateSettings(
                SystemSettingsEntity(
                    deliveryFee = 20.0,
                    appCommissionPercent = 5.0,
                    monthlySubscriptionFee = 99.0,
                    adminSupportPhone = "01012345678",
                    adminWhatsAppNumber = "201012345678",
                    vodafoneCashNumber = "01012345678",
                    instaPayHandle = "albakal@instapay",
                    captainFleetEnabled = true
                )
            )

            // Seed 2 Real Merchants in Alexandria
            val m1Id = dao.insertUser(
                UserEntity(
                    email = "baraka.market@albakal.com",
                    passwordHash = "123456",
                    fullName = "سوبرماركت البركة وماركت الخضار",
                    phone = "01287654321",
                    role = UserRole.MERCHANT,
                    governorate = "الإسكندرية",
                    district = "سيدي جابر",
                    street = "شارع المشير طنطاوي عمارة 10",
                    isApproved = true,
                    isBanned = false
                )
            )

            val m2Id = dao.insertUser(
                UserEntity(
                    email = "amana.grocer@albakal.com",
                    passwordHash = "123456",
                    fullName = "بقالة الأمانة والفواكه الطازجة",
                    phone = "01198765432",
                    role = UserRole.MERCHANT,
                    governorate = "الإسكندرية",
                    district = "شرق الإسكندرية (سموحة/سيدي بشر)",
                    street = "شارع فوزي معاذ ميدان علي بن أبي طالب",
                    isApproved = true,
                    isBanned = false
                )
            )

            // Seed 1 Sample Customer in Alexandria
            dao.insertUser(
                UserEntity(
                    email = "ahmed.customer@albakal.com",
                    passwordHash = "123456",
                    fullName = "أحمد محمود الإسكندراني",
                    phone = "01099887766",
                    role = UserRole.CUSTOMER,
                    governorate = "الإسكندرية",
                    district = "سيدي جابر",
                    street = "شارع بورسعيد",
                    building = "15",
                    floor = "4",
                    apartment = "8",
                    isApproved = true,
                    isBanned = false
                )
            )

            // Seed Supermarket Products
            val sampleProducts = listOf(
                // Fruits
                ProductEntity(
                    merchantId = m1Id,
                    merchantName = "سوبرماركت البركة وماركت الخضار",
                    title = "موز بلدي سكري فاخر",
                    description = "موز بلدي مصري طازج درجة أولى غني بالبوتاسيوم والطاقة",
                    category = ProductCategory.FRUITS,
                    price = 35.0,
                    discountPrice = 28.0,
                    unit = "1 كجم",
                    imageUrl = "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?w=500&q=80"
                ),
                ProductEntity(
                    merchantId = m2Id,
                    merchantName = "بقالة الأمانة والفواكه الطازجة",
                    title = "تفاح أحمر سكري إيطالي",
                    description = "تفاح أحمر طازج ومقرمش عالي الجودة وممتاز للأطفال",
                    category = ProductCategory.FRUITS,
                    price = 85.0,
                    discountPrice = 75.0,
                    unit = "1 كجم",
                    imageUrl = "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=500&q=80"
                ),
                ProductEntity(
                    merchantId = m2Id,
                    merchantName = "بقالة الأمانة والفواكه الطازجة",
                    title = "برتقال عصير بلدي طازج",
                    description = "برتقال صيفي/شتوي غني بفيتامين سي ممتاز للعصائر اليومية",
                    category = ProductCategory.FRUITS,
                    price = 22.0,
                    unit = "1 كجم",
                    imageUrl = "https://images.unsplash.com/photo-1582979512210-99b6a53386f9?w=500&q=80"
                ),
                ProductEntity(
                    merchantId = m1Id,
                    merchantName = "سوبرماركت البركة وماركت الخضار",
                    title = "فراولة طازجة منتقاة بالواحدة",
                    description = "فراولة حمراء ناضجة طازجة من المزرعة مباشرة",
                    category = ProductCategory.FRUITS,
                    price = 45.0,
                    discountPrice = 38.0,
                    unit = "طبق 500 جم",
                    imageUrl = "https://images.unsplash.com/photo-1464965911861-746a04b4bca6?w=500&q=80"
                ),

                // Vegetables
                ProductEntity(
                    merchantId = m1Id,
                    merchantName = "سوبرماركت البركة وماركت الخضار",
                    title = "طماطم بلدي طازجة للطبخ والسلطة",
                    description = "طماطم حمراء متماسكة منتقاة بعناية لجميع أكلات المطبخ",
                    category = ProductCategory.VEGETABLES,
                    price = 18.0,
                    unit = "1 كجم",
                    imageUrl = "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=500&q=80"
                ),
                ProductEntity(
                    merchantId = m1Id,
                    merchantName = "سوبرماركت البركة وماركت الخضار",
                    title = "بطاطس تحمير شيبسي ممتازة",
                    description = "بطاطس رملية عالية الجودة للقلي المقرمش والطبخ",
                    category = ProductCategory.VEGETABLES,
                    price = 25.0,
                    discountPrice = 20.0,
                    unit = "1 كجم",
                    imageUrl = "https://images.unsplash.com/photo-1518977676601-b53f82aba655?w=500&q=80"
                ),
                ProductEntity(
                    merchantId = m2Id,
                    merchantName = "بقالة الأمانة والفواكه الطازجة",
                    title = "خيار بلدي صوب مقرمش",
                    description = "خيار طازج وصغير للسلطات والسندوتشات",
                    category = ProductCategory.VEGETABLES,
                    price = 22.0,
                    unit = "1 كجم",
                    imageUrl = "https://images.unsplash.com/photo-1604977042946-1eecc30f269e?w=500&q=80"
                ),
                ProductEntity(
                    merchantId = m2Id,
                    merchantName = "بقالة الأمانة والفواكه الطازجة",
                    title = "بصل أحمر بلدي عالي الجودة",
                    description = "بصل أحمر مصري ممتاز للطبخ يدوم طويلاً",
                    category = ProductCategory.VEGETABLES,
                    price = 24.0,
                    unit = "1 كجم",
                    imageUrl = "https://images.unsplash.com/photo-1508747703725-719777637510?w=500&q=80"
                ),

                // Dry Grocery
                ProductEntity(
                    merchantId = m1Id,
                    merchantName = "سوبرماركت البركة وماركت الخضار",
                    title = "أرز مصري فاخر الضحى 1 كجم",
                    description = "أرز أبيض مصري حبة عريضة منقى ومغسول مسبقاً",
                    category = ProductCategory.DRY_GROCERY,
                    price = 38.0,
                    discountPrice = 34.0,
                    unit = "1 كيس",
                    imageUrl = "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500&q=80"
                ),
                ProductEntity(
                    merchantId = m1Id,
                    merchantName = "سوبرماركت البركة وماركت الخضار",
                    title = "مكرونة الملكة أقلام 400 جم",
                    description = "مكرونة مصنوعة من أجود أنواع السميد للباشاميل والطواجن",
                    category = ProductCategory.DRY_GROCERY,
                    price = 16.0,
                    unit = "1 كيس",
                    imageUrl = "https://images.unsplash.com/photo-1621996346565-e3d5d628169e?w=500&q=80"
                ),
                ProductEntity(
                    merchantId = m1Id,
                    merchantName = "سوبرماركت البركة وماركت الخضار",
                    title = "زيت ذرة عافية نقي 800 مل",
                    description = "زيت ذرة نقي خفيف وصحي للطهي والقلي",
                    category = ProductCategory.DRY_GROCERY,
                    price = 98.0,
                    discountPrice = 89.0,
                    unit = "1 زجاجة",
                    imageUrl = "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=500&q=80"
                ),
                ProductEntity(
                    merchantId = m2Id,
                    merchantName = "بقالة الأمانة والفواكه الطازجة",
                    title = "شاي العروسة ناعم 250 جم",
                    description = "شاي أسود أسواني مصري أصيل غني المذاق والنكهة",
                    category = ProductCategory.DRY_GROCERY,
                    price = 55.0,
                    unit = "1 عبوة",
                    imageUrl = "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=500&q=80"
                ),

                // Sweets & Chocolates
                ProductEntity(
                    merchantId = m1Id,
                    merchantName = "سوبرماركت البركة وماركت الخضار",
                    title = "شيكولاتة كادبوري ديري ميلك سادة 90 جم",
                    description = "شيكولاتة الحليب الفاخرة المليئة بالحنان من كادبوري",
                    category = ProductCategory.SWEETS_CHOCOLATE,
                    price = 45.0,
                    discountPrice = 39.0,
                    unit = "1 بار",
                    imageUrl = "https://images.unsplash.com/photo-1549007994-cb92caebd54b?w=500&q=80"
                ),
                ProductEntity(
                    merchantId = m1Id,
                    merchantName = "سوبرماركت البركة وماركت الخضار",
                    title = "بسكويت أوريو الأصلي 6 قطع",
                    description = "بسكويت الكاكاو الشهير محشو بكريمة الفانيليا اللذيذة",
                    category = ProductCategory.SWEETS_CHOCOLATE,
                    price = 15.0,
                    unit = "1 باكو",
                    imageUrl = "https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?w=500&q=80"
                ),
                ProductEntity(
                    merchantId = m2Id,
                    merchantName = "بقالة الأمانة والفواكه الطازجة",
                    title = "شيكولاتة جلاكسي ناعمة بحليب الصافي",
                    description = "نعومة لا تقاوم من شيكولاتة جلاكسي الفاخرة",
                    category = ProductCategory.SWEETS_CHOCOLATE,
                    price = 48.0,
                    unit = "1 بار",
                    imageUrl = "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=500&q=80"
                ),

                // Supermarket & Household
                ProductEntity(
                    merchantId = m1Id,
                    merchantName = "سوبرماركت البركة وماركت الخضار",
                    title = "حليب جهينة كامل الدسم 1 لتر",
                    description = "حليب طبيعي معقم 100% بدون مواد حافظة لصحة العائلة",
                    category = ProductCategory.SUPERMARKET,
                    price = 44.0,
                    discountPrice = 41.0,
                    unit = "1 لتر",
                    imageUrl = "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=500&q=80"
                ),
                ProductEntity(
                    merchantId = m1Id,
                    merchantName = "سوبرماركت البركة وماركت الخضار",
                    title = "جبنة دومتي فيتا بيضاء 500 جم",
                    description = "جبنة بيضاء كريمية نباتية الدهن سهلة الفرد ومثالية للإفطار",
                    category = ProductCategory.SUPERMARKET,
                    price = 38.0,
                    unit = "1 علبة",
                    imageUrl = "https://images.unsplash.com/photo-1634487359989-3e90c9432133?w=500&q=80"
                ),
                ProductEntity(
                    merchantId = m1Id,
                    merchantName = "سوبرماركت البركة وماركت الخضار",
                    title = "كرتونة بيض أحمر بلدي طازج 30 بيضة",
                    description = "بيض مزارع أحمر طازج يومياً مغذٍ ومثالي لجميع الوجبات",
                    category = ProductCategory.SUPERMARKET,
                    price = 165.0,
                    discountPrice = 155.0,
                    unit = "1 طبق 30 بيضة",
                    imageUrl = "https://images.unsplash.com/photo-1516467508483-a7212febe31a?w=500&q=80"
                ),
                ProductEntity(
                    merchantId = m2Id,
                    merchantName = "بقالة الأمانة والفواكه الطازجة",
                    title = "صابون سائل فيري بليمون مركز 650 مل",
                    description = "قوة تنظيف الدهون بقطرة واحدة ولمعان استثنائي للأواني",
                    category = ProductCategory.SUPERMARKET,
                    price = 48.0,
                    unit = "1 زجاجة",
                    imageUrl = "https://images.unsplash.com/photo-1585670270608-b4be4fb880ed?w=500&q=80"
                )
            )

            for (p in sampleProducts) {
                dao.insertProduct(p)
            }

            // Seed Sponsored Ads
            dao.insertSponsoredAd(
                SponsoredAdEntity(
                    title = "عروض التوفير الأسبوعية الكبرى في الإسكندرية!",
                    subtitle = "خصم يصل إلى 30% على الفواكه والخضار الطازج عند الشراء من سوبرماركت البركة",
                    imageUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=800&q=80",
                    targetCategory = "FRUITS"
                )
            )
            dao.insertSponsoredAd(
                SponsoredAdEntity(
                    title = "اشترك في باقة التوصيل الشهري ووفر في كل طلب",
                    subtitle = "توصيل مجاني غير محدود لجميع طلباتك بـ 99 جنيه فقط شهرياً",
                    imageUrl = "https://images.unsplash.com/photo-1526367790999-0150786686a2?w=800&q=80",
                    targetCategory = "SUPERMARKET"
                )
            )
        }
    }
}
