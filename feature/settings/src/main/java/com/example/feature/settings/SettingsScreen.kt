package com.example.feature.settings

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.common.api.quotes.QuotesRetrofitClient
import com.example.common.api.weather.CurrentWeatherResponse
import com.example.common.api.weather.MainDetails
import com.example.common.api.weather.WeatherRetrofitClient
import com.example.common.login.GoogleSignInHandler
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

fun isGoodWeatherForRPS(weatherData: CurrentWeatherResponse?): Boolean {
    val mainDetails: MainDetails? = weatherData?.main
    val weatherConditions = weatherData?.weather

    if (mainDetails == null || weatherConditions.isNullOrEmpty()) return false

    val tempCelsius = mainDetails.temp
    val windSpeedMps = weatherData.wind?.speed
    val conditions = weatherConditions.firstOrNull()?.main?.lowercase() ?: ""

    if (tempCelsius == null) return false

    val badConditions = listOf("rain", "snow", "thunderstorm", "drizzle", "squall", "tornado", "extreme")

    return tempCelsius in 10.0..30.0 && (windSpeedMps ?: 0.0) < 10.0 && !badConditions.any { it in conditions }
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onNavigateToAppSettings: () -> Unit
) {
    val context = LocalContext.current
    val applicationContext = context.applicationContext
    val coroutineScope = rememberCoroutineScope()

    var currentGoogleUser by remember { mutableStateOf<GoogleIdTokenCredential?>(null) }

    val signInHelper = remember {
        GoogleSignInHandler(
            context = context,
            coroutineScope = coroutineScope,
            onSignInSuccess = { credential ->
                currentGoogleUser = credential
            },
            onSignInFailure = {
                currentGoogleUser = null
            }
        )
    }

    var showAboutDialog by remember { mutableStateOf(false) }
    var currentQuoteString by remember { mutableStateOf<String?>(null) }
    var isLoadingQuote by remember { mutableStateOf(false) }
    var quoteError by remember { mutableStateOf<String?>(null) }
    val quoteKeywords = listOf("rock", "paper", "scissors")

    var weatherData by remember { mutableStateOf<CurrentWeatherResponse?>(null) }
    var isLoadingWeather by remember { mutableStateOf(true) }
    var weatherError by remember { mutableStateOf<String?>(null) }
    var weatherClientInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        weatherError = null
        isLoadingWeather = true

        try {
            WeatherRetrofitClient.initialize(applicationContext)
            weatherClientInitialized = true
            Log.d("SettingsScreen", "WeatherRetrofitClient initialized. API Key: ${WeatherRetrofitClient.API_KEY}")

            val lat = 44.1646677902515
            val lon = 12.219122156784248
            Log.d("SettingsScreen", "Fetching weather for lat: $lat, lon: $lon")

            val response = WeatherRetrofitClient.openWeatherApi.getCurrentWeather(
                latitude = lat,
                longitude = lon,
                apiKey = WeatherRetrofitClient.API_KEY
            )

            if (response.isSuccessful) {
                val responseBody = response.body()
                weatherData = responseBody

                val localMainDetails = responseBody?.main
                val localWeatherConditions = responseBody?.weather

                if (localMainDetails == null || localWeatherConditions.isNullOrEmpty()) {
                    weatherError = "Could not parse essential weather data."
                    Log.w("SettingsScreen", "Weather response successful but essential data is null. Body: $responseBody")
                } else {
                    Log.d("SettingsScreen", "Weather data fetched for Spot Software SRL - Via dell'Arrigoni 260, Cesena (FC) - Temp: ${localMainDetails.temp}, Condition: ${localWeatherConditions.firstOrNull()?.main}")
                }
            } else {
                weatherError = "Failed to fetch weather: ${response.message()} (Code: ${response.code()})"
                Log.e("SettingsScreen", "Weather API error: ${response.code()} - ${response.message()} - Error Body: ${response.errorBody()?.string()}")
            }
        } catch (e: IllegalStateException) {
            weatherError = "Weather client error: ${e.message}"
            Log.e("SettingsScreen", "Weather client IllegalStateException", e)
            weatherClientInitialized = false
        } catch (e: Exception) {
            weatherError = "Weather fetch error: ${e.localizedMessage ?: "Unknown error"}"
            Log.e("SettingsScreen", "Exception during weather fetch or init", e)
            if (e.message?.contains("R.string.openweather_api_key") == true || e.cause?.message?.contains("R.string.openweather_api_key") == true) {
                weatherError = "API Key for weather not found. Check string resources."
            }
            weatherClientInitialized = false
        } finally {
            isLoadingWeather = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Welcome, ${currentGoogleUser?.displayName ?: "username"}!",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        SettingsListItem(
            title = if (currentGoogleUser == null) "Login with Google" else "Logout",
            icon = Icons.Filled.Person,
            onClick = {
                if (currentGoogleUser == null) {
                    if (context is Activity) { signInHelper.signIn() }
                } else {
                    signInHelper.signOut()
                    currentGoogleUser = null
                }
            }
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        SettingsListItem(
            title = "Camera Permissions",
            icon = Icons.Filled.ThumbUp,
            onClick = { onNavigateToAppSettings() }
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Weather for playing rock, paper, scissors",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Is the weather good for playing rock paper scissors at Spot Software SRL - Via dell'Arrigoni 260, Cesena (FC)?",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (isLoadingWeather) {
                        Text("Checking weather...", style = MaterialTheme.typography.bodySmall)
                    } else if (!weatherClientInitialized && weatherError != null) {
                        Text(weatherError!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    } else if (weatherError != null) {
                        Text(weatherError!!, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                    } else {
                        val localMainDetails = weatherData?.main
                        val localWeatherConditions = weatherData?.weather

                        if (localMainDetails != null && !localWeatherConditions.isNullOrEmpty()) {
                            val temp = localMainDetails.temp?.let { "%.1f°C".format(it) } ?: "N/A"
                            val conditionDesc = localWeatherConditions.firstOrNull()?.description?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } ?: "Unknown"
                            Text("Currently: $temp, $conditionDesc", style = MaterialTheme.typography.bodySmall)
                        } else {
                            Text("Weather data unavailable.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            if (isLoadingWeather) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else if (weatherClientInitialized && weatherError == null && weatherData != null) {
                Icon(
                    imageVector = if (isGoodWeatherForRPS(weatherData)) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                    contentDescription = if (isGoodWeatherForRPS(weatherData)) "Good weather for a match of rock paper scissors" else "Bad weather for a match of rock paper scissors",
                    tint = if (isGoodWeatherForRPS(weatherData)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
            } else if (!weatherClientInitialized) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = "Weather client initialization failed",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        SettingsListItem(
            title = "Learn to play",
            icon = Icons.Filled.Info,
            onClick = {
                isLoadingQuote = true
                quoteError = null
                currentQuoteString = null
                coroutineScope.launch {
                    try {
                        val randomKeyword = quoteKeywords.random()
                        val prompt = "generate 1 quote about $randomKeyword"
                        val response = QuotesRetrofitClient.jsonGptApi.getQuote(prompt = prompt)
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody?.quotes.isNullOrEmpty()) {
                                quoteError = "No quotes found."
                            } else {
                                currentQuoteString = responseBody.quotes!!.first().removeSurrounding("\"")
                            }
                        } else {
                            quoteError = "Failed to fetch quote: ${response.message()}"
                        }
                    } catch (e: Exception) {
                        quoteError = "Quote fetch error: ${e.localizedMessage}"
                    } finally {
                        isLoadingQuote = false
                        showAboutDialog = true
                    }
                }
            }
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("Today's tip:") },
            text = {
                Column {
                    if (isLoadingQuote) {
                        Text("Thinking...")
                    } else if (currentQuoteString != null) {
                        Text("\"$currentQuoteString\"")
                    } else if (quoteError != null) {
                        Text("I don't know anything about rock, paper or scissors: $quoteError")
                    } else {
                        Text("Brought to you by PinguSoftware.")
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showAboutDialog = false }) { Text("OK") } }
        )
    }
}

@Composable
fun SettingsListItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(imageVector = icon, contentDescription = title, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
    }
}