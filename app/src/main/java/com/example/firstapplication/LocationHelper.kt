package com.example.firstapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat

class LocationHelper(private val activity: ComponentActivity) {
    private val locationManager: LocationManager =
        activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    // Observable state for location
    val locationState = mutableStateOf<Location?>(null)

    // Mock location for testing
    val mockLocation = mutableStateOf<String?>(null)

    // Location updates listener
    private val locationListener =
        object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationState.value = location
                mockLocation.value = null
            }

            override fun onStatusChanged(
                provider: String?,
                status: Int,
                extras: Bundle?,
            ) {}

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {
                Toast.makeText(activity, "位置服务已禁用: $provider", Toast.LENGTH_SHORT).show()
            }
        }

    // Permission request launcher
    private val requestPermissionLauncher =
        activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                // Permission granted
                getCurrentLocation()
            } else {
                // Permission denied
                Toast.makeText(activity, "位置权限被拒绝", Toast.LENGTH_SHORT).show()
            }
        }

    // Check and request location permissions
    fun checkLocationPermission() {
        when {
            hasLocationPermission() -> {
                // Permission already granted
                getCurrentLocation()
            }
            else -> {
                // Request permissions
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    ),
                )
            }
        }
    }

    // Check if location permissions are granted
    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Get current location using LocationManager
    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        if (!hasLocationPermission()) {
            checkLocationPermission()
            return
        }

        try {
            // 尝试先获取上次已知位置
            val lastKnownLocation = getLastKnownLocation()
            if (lastKnownLocation != null) {
                locationState.value = lastKnownLocation
                mockLocation.value = null
            }

            // 请求位置更新
            val providers = locationManager.getProviders(true)
            if (providers.isEmpty()) {
                Toast.makeText(activity, "没有可用的位置提供者", Toast.LENGTH_SHORT).show()
                return
            }

            // 优先使用GPS，然后是网络位置
            val provider =
                when {
                    providers.contains(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                    providers.contains(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                    else -> providers[0]
                }

            // 注册位置更新
            locationManager.requestLocationUpdates(
                provider,
                // 最小时间间隔（毫秒）
                1000,
                // 最小距离变化（米）
                1f,
                locationListener,
            )
        } catch (e: Exception) {
            Toast.makeText(activity, "获取位置错误: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // 获取最后已知位置
    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }

        val providers = locationManager.getProviders(true)
        var bestLocation: Location? = null

        for (provider in providers) {
            val location = locationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                bestLocation = location
            }
        }

        return bestLocation
    }

    // 设置虚拟位置（测试用）
    fun setMockLocation(latLng: String) {
        mockLocation.value = latLng

        // 如果需要将虚拟坐标转换为Location对象
        try {
            val parts = latLng.split(",")
            if (parts.size == 2) {
                val latitude = parts[0].trim().toDouble()
                val longitude = parts[1].trim().toDouble()

                val mockLoc = Location("mock")
                mockLoc.latitude = latitude
                mockLoc.longitude = longitude
                mockLoc.accuracy = 5.0f
                mockLoc.time = System.currentTimeMillis()

                // 如果需要的话可以使用这个虚拟Location对象
                // locationState.value = mockLoc
            }
        } catch (e: Exception) {
            // 解析错误处理
        }
    }

    // 清理资源
    fun cleanup() {
        try {
            locationManager.removeUpdates(locationListener)
        } catch (e: SecurityException) {
            // 权限问题处理
        }
    }
}
