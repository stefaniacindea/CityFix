package com.example.licenta

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.licenta.ui.theme.LicentaTheme
import com.example.licenta.viewmodel.AuthViewModel
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    private val cererePermisiuneNotificari = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("PERMISIUNE", "Notificarile au fost Aprobate!")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        cerePermisiuneaDeNotificari()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM", "Token telefon: ${task.result}")
            }
        }

        val authViewModel = AuthViewModel()

        setContent {
            LicentaTheme {
                AppNavigation(
                    authViewModel = authViewModel
                )
            }
        }
    }

    private fun cerePermisiuneaDeNotificari() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                cererePermisiuneNotificari.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}