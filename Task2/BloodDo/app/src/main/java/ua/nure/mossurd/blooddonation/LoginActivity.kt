package ua.nure.mossurd.blooddonation

import android.content.Context
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ua.nure.mossurd.blooddonation.misc.Constants
import ua.nure.mossurd.blooddonation.request.ApiService
import ua.nure.mossurd.blooddonation.request.model.LoginResponseDto
import ua.nure.mossurd.blooddonation.ui.theme.BloodDoTheme

class LoginActivity : ComponentActivity() {

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BLOODDO_API_PATH)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        enableEdgeToEdge()
        setContent {
            BloodDoTheme {
                Content(this@LoginActivity)
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
                var username by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }

                Text(
                    text = "Welcome to BloodDo\n\n"
                )

                TextField(
                    value = username,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(80.dp),
                    onValueChange = { username = it },
                    label = { Text("Username") }
                )
                TextField(
                    value = password,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(80.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = { password = it },
                    label = { Text("Password") }
                )
                Button(
                    onClick = {
                        try {
                            loginRequest(username, password)
                        } catch (e: Exception) {
                            Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                        }
//                        // FIXME do login request and redirect if success; toast error otherwise
//                        var success: Boolean = false
//
//                        context?.getSharedPreferences(Constants.BLOODDO_CONTEXT_PREFS, MODE_PRIVATE)
//                            ?.edit() { putString(Constants.BLOODDO_TOKEN_NAME, "test_token") }
//
//                        val token = context?.getSharedPreferences(
//                            Constants.BLOODDO_CONTEXT_PREFS,
//                            MODE_PRIVATE
//                        )
//                            ?.getString(Constants.BLOODDO_TOKEN_NAME, "cringe")
//
//                        Toast.makeText(
//                            this@LoginActivity, token,
//                            Toast.LENGTH_SHORT
//                        ).show()
//
//                        if (success) {
//                            val intent = Intent(context, MainMenuActivity::class.java)
//                            context?.startActivity(intent)
//                        } else {
//                            Toast.makeText(
//                                this@LoginActivity, "Invalid username or password",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(80.dp),
                    colors = ButtonColors(Color.Red, Color.White, Color.Gray, Color.White),
                    enabled = true
                ) {
                    Text(text = "Login")
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

    fun loginRequest(username: String, password: String) {
        val requestBody = mapOf("username" to username, "password" to password)

        apiService.loginCall(requestBody).enqueue(object : Callback<LoginResponseDto> {
            override fun onResponse(call: Call<LoginResponseDto>, response: Response<LoginResponseDto>) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    if (responseData != null) {
                        // Show the response values as a Toast
                        val message = "${responseData.name} :: ${responseData.token} :: ${responseData.bloodType} :: ${responseData.rhesusFactor}"
                        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Request failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponseDto>, t: Throwable) {
                Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

