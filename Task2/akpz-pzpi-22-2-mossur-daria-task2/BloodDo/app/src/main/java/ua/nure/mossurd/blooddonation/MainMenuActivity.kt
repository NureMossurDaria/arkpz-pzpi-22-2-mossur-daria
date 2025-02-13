package ua.nure.mossurd.blooddonation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.nure.mossurd.blooddonation.ui.theme.BloodDoTheme

class MainMenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BloodDoTheme {
                Content(this@MainMenuActivity)
            }
        }
    }

    @Composable
    fun Content(context: Context?) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        val intent = Intent(context, DonationAppointmentActivity::class.java)
                        context?.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(80.dp),
                    colors = ButtonColors(Color.Red, Color.White, Color.Gray, Color.White),
                    enabled = true
                ) {
                    Text(text = "Button 1")
                }

                Button(
                    onClick = { /* Handle click for Button 2 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(80.dp)
                ) {
                    Text(text = "Button 2")
                }

                Button(
                    onClick = { /* Handle click for Button 3 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(80.dp)
                ) {
                    Text(text = "Button 3")
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        BloodDoTheme {
            Content(null)
        }
    }
}