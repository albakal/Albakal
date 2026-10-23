package com.example.albakal.ui.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder

object WhatsAppHelper {

    fun formatEgyptianPhoneForWhatsApp(phone: String): String {
        val clean = phone.filter { it.isDigit() }
        return when {
            clean.startsWith("20") -> clean
            clean.startsWith("0") -> "20" + clean.substring(1)
            else -> "20$clean"
        }
    }

    fun openWhatsApp(context: Context, phone: String, message: String) {
        try {
            val formattedPhone = formatEgyptianPhoneForWhatsApp(phone)
            val encodedMessage = URLEncoder.encode(message, "UTF-8")
            val url = "https://api.whatsapp.com/send?phone=$formattedPhone&text=$encodedMessage"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح تطبيق واتساب: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    fun makePhoneCall(context: Context, phone: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح لوحة الاتصال: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun openWebUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح الرابط", Toast.LENGTH_SHORT).show()
        }
    }
}
