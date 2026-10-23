package com.example.albakal.data.model

enum class UserRole {
    CUSTOMER,
    MERCHANT,
    ADMIN,
    SUPERVISOR
}

enum class ProductCategory(val displayNameAr: String) {
    FRUITS("فواكه"),
    VEGETABLES("خضروات"),
    DRY_GROCERY("بقالة جافة"),
    SWEETS_CHOCOLATE("حلويات وشوكولاتات"),
    SUPERMARKET("كل ما يتعلق بالسوبرماركت")
}

enum class OrderStatus(val displayNameAr: String) {
    RECEIVED("تم استلام الطلب"),
    PREPARING("جاري تحضير المنتج"),
    OUT_FOR_DELIVERY("جاري إرسال المنتج مع المندوب"),
    DELIVERED("تم التسليم بنجاح"),
    CANCELLED("ملغي")
}

enum class PaymentMethod(val displayNameAr: String) {
    CASH_ON_DELIVERY("كاش عند الاستلام"),
    VODAFONE_CASH("فودافون كاش"),
    INSTAPAY("انستاباي")
}

enum class SettlementStatus(val displayNameAr: String) {
    PENDING("قيد المراجعة"),
    APPROVED("تم التأكيد وتصفير المديونية"),
    REJECTED("مرفوض")
}

data class UserSession(
    val id: Long,
    val email: String,
    val fullName: String,
    val phone: String,
    val role: UserRole,
    val governorate: String,
    val district: String,
    val streetAddress: String,
    val isApproved: Boolean,
    val isBanned: Boolean,
    val banReason: String = "",
    val supervisorPermissions: Set<String> = emptySet(),
    val isMonthlySubscribed: Boolean = false
)
