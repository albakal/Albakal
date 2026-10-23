package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.albakal.data.local.AppDatabase
import com.example.albakal.data.model.PaymentMethod
import com.example.albakal.data.model.ProductCategory
import com.example.albakal.data.model.UserRole
import com.example.albakal.data.repository.AlBaqalRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun testAppNameIsAlBaqal() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("البقال", appName)
  }

  @Test
  fun testInitialDatabaseSeedAndAdminLogin() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val database = AppDatabase.getDatabase(context)
    val repo = AlBaqalRepository(database.albakalDao())

    // Seed initial data
    repo.seedInitialDataIfEmpty()

    // Test Admin account exists
    val admin = repo.getUserByEmail("mahmoudomran324@gmail.com")
    assertNotNull("Admin must exist", admin)
    assertEquals("Aa&9119045", admin?.passwordHash)
    assertEquals(UserRole.ADMIN, admin?.role)

    // Test Merchants exist
    val merchant = repo.getUserByEmail("baraka.market@albakal.com")
    assertNotNull("Merchant must exist", merchant)
    assertEquals(UserRole.MERCHANT, merchant?.role)

    // Test Products exist
    val products = repo.allProducts.first()
    assertTrue("Products should be seeded", products.isNotEmpty())

    // Test Settings exist
    val settings = repo.systemSettings.first()
    assertNotNull("Settings should be seeded", settings)
    assertEquals(20.0, settings?.deliveryFee ?: 0.0, 0.01)
  }
}
