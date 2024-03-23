package com.ubercab.presidio.auth_demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.uber.sdk2.auth.UberAuthClientImpl
import com.uber.sdk2.auth.api.request.AuthContext
import com.uber.sdk2.auth.api.request.AuthDestination
import com.uber.sdk2.auth.api.request.AuthType
import com.uber.sdk2.auth.api.request.CrossApp
import com.uber.sdk2.auth.api.request.PrefillInfo
import com.uber.sdk2.auth.api.response.UberToken
import com.ubercab.presidio.auth_demo.ui.theme.UberandroidsdkTheme

class DemoActivity : ComponentActivity() {

    private val uberAuthLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            logResult(result.resultCode, result.data)
        }

    private fun logResult(resultCode: Int, data: Intent?) {
        when (resultCode) {
            RESULT_OK -> {
                val uberToken = data?.getParcelableExtra<UberToken>("EXTRA_UBER_TOKEN")
                Log.d("xxxx", "Uber Token: ${uberToken?.accessToken}")
            }

            else -> {
                val errorMessage = data?.getStringExtra("EXTRA_ERROR")
                Log.d("xxxx", "Error: $errorMessage")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UberandroidsdkTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Note - you just need one of these buttons to authenticate
                        // Rider as top priority app
                        UberAuthButton(
                            context = this@DemoActivity,
                            launcher = uberAuthLauncher,
                            authDestination = AuthDestination.CrossAppSso(listOf(CrossApp.Rider, CrossApp.Eats)),
                            "Login with Rides"
                        )
                        Spacer(modifier = Modifier.height(16.dp)) // Add space between buttons

                        // Eater as top priority app
                        UberAuthButton(
                            context = this@DemoActivity,
                            launcher = uberAuthLauncher,
                            authDestination = AuthDestination.CrossAppSso(listOf(CrossApp.Eats, CrossApp.Rider)),
                            "Login with Eats"
                        )
                        Spacer(modifier = Modifier.height(16.dp)) // Add space between buttons

                        // Driver as top priority app
                        UberAuthButton(
                            context = this@DemoActivity,
                            launcher = uberAuthLauncher,
                            authDestination = AuthDestination.CrossAppSso(listOf(CrossApp.Driver, CrossApp.Rider)),
                            "Login with Driver"
                        )
                        Spacer(modifier = Modifier.height(16.dp)) // Add space between buttons

                        // InApp - Authenticate using system webview
                        UberAuthButton(
                            context = this@DemoActivity,
                            launcher = uberAuthLauncher,
                            authDestination = AuthDestination.InApp,
                            "Login with Uber"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UberAuthButton(context: Context, launcher: ActivityResultLauncher<Intent>, authDestination: AuthDestination, buttonText: String) {
    Button(
        onClick = {
            // Invoke the Uber authentication call here
            UberAuthClientImpl().authenticate(
                context,
                launcher,
                AuthContext(
                    authDestination,
                    AuthType.PKCE(),
                    PrefillInfo("saurabh@lalwani.com", "saurabh", "lalwani", "6455557342"),
                )
            )
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
    ) {
        Text(text = buttonText)
    }
}