package com.example.firstapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.firstapplication.ui.theme.FirstApplicationTheme

class MainActivity : ComponentActivity() {
    private lateinit var locationHelper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize location helper
        locationHelper = LocationHelper(this)

        // Set mock location as specified by user
        locationHelper.setMockLocation("123.23,60.33")

        enableEdgeToEdge()
        setContent {
            FirstApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LocationScreen(
                        locationHelper = locationHelper,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationHelper.cleanup()
    }
}

@Composable
fun LocationScreen(
    locationHelper: LocationHelper,
    modifier: Modifier = Modifier,
) {
    // Get location state
    val location by remember { locationHelper.locationState }
    val mockLocation by remember { locationHelper.mockLocation }

    // Clean up when the composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            locationHelper.cleanup()
        }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "位置信息",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (mockLocation != null) {
            Text(
                text = "当前位置（预设）:",
                style = MaterialTheme.typography.bodyLarge,
            )
            val mockLocationValue = mockLocation // 提取实际值到局部变量
            if (mockLocationValue != null) { // 再次检查非空
                val coords = mockLocationValue.split(",")
                if (coords.size == 2) {
                    Text(
                        text = "经度: ${coords[0]}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "纬度: ${coords[1]}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    Text(
                        text = mockLocationValue,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        } else if (location != null) {
            Text(
                text = "当前位置（真实）:",
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = "经度: ${location?.longitude}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "纬度: ${location?.latitude}",
                style = MaterialTheme.typography.bodyMedium,
            )
        } else {
            Text(
                text = "位置信息不可用",
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { locationHelper.checkLocationPermission() },
        ) {
            Text("获取当前位置")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LocationScreenPreview() {
    FirstApplicationTheme {
        // We can't actually create a LocationHelper in the preview
        // So this is just a placeholder UI
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "位置信息",
                style = MaterialTheme.typography.headlineMedium,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "当前位置（预设）:",
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = "经度: 123.23",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "纬度: 60.33",
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { }) {
                Text("获取当前位置")
            }
        }
    }
}
