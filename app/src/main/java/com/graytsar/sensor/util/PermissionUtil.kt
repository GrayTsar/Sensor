package com.graytsar.sensor.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

/**
 * Utility class to request and check permissions for the app.
 */
object PermissionUtil {

    /**
     * Verifies if post notification permission was granted.
     */
    fun isNotificationPermissionGranted(context: Context): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= 33) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                NotificationManagerCompat.from(context).areNotificationsEnabled()
            }
        } catch (e: Exception) {
            false
        }
    }
}