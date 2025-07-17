package com.example.downtimeguard.ui.theme

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.downtimeguard.ui.theme.ui.theme.DowntimeGuardTheme

class MainScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DowntimeGuardTheme {
                MainScreen(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
@Composable
fun MainScreenUI() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "DowntimeGuard",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        CreateButton(text = "Let’s Get Started") {
            Toast.makeText(context, "Starting...", Toast.LENGTH_SHORT).show()
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Activate Blocker", color = Color.White)
        }
    }
}


// composable function for a button
@Composable
fun CreateButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03DAC5)),
        modifier = Modifier.padding(vertical = 8.dp),
        elevation = ButtonDefaults.buttonElevation()
    ) {
        Text(
            text = text,
            color = Color.Black
        )
    }
}



@Preview(showBackground = true, name = "MainScreen Preview")
@Composable
fun PreviewMainScreen() {
    MainScreenUI(
//        onStartClick = {},
//        isBlockerEnabled = false,
//        onToggleBlocker = {}
    )
}