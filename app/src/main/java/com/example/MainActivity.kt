package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.albakal.data.model.UserRole
import com.example.albakal.ui.AlBaqalViewModel
import com.example.albakal.ui.screens.admin.AdminContainerScreen
import com.example.albakal.ui.screens.auth.AuthScreen
import com.example.albakal.ui.screens.customer.CustomerContainerScreen
import com.example.albakal.ui.screens.merchant.MerchantContainerScreen
import com.example.ui.theme.AlbakalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                AlbakalTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        AlBaqalApp()
                    }
                }
            }
        }
    }
}

@Composable
fun AlBaqalApp(viewModel: AlBaqalViewModel = viewModel()) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    if (currentUser == null) {
        AuthScreen(viewModel = viewModel)
    } else {
        val user = currentUser!!
        when (user.role) {
            UserRole.CUSTOMER -> {
                CustomerContainerScreen(user = user, viewModel = viewModel)
            }
            UserRole.MERCHANT -> {
                MerchantContainerScreen(user = user, viewModel = viewModel)
            }
            UserRole.ADMIN, UserRole.SUPERVISOR -> {
                AdminContainerScreen(user = user, viewModel = viewModel)
            }
        }
    }
}
