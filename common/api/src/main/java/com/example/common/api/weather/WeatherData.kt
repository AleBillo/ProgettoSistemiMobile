package com.example.common.api.weather

import com.google.gson.annotations.SerializedName

data class CurrentWeatherResponse(
    @SerializedName("coord") val coord: CoordDetails?,
    @SerializedName("weather") val weather: List<WeatherCondition>?,
    @SerializedName("base") val base: String?,
    @SerializedName("main") val main: MainDetails?,
    @SerializedName("visibility") val visibility: Int?,
    @SerializedName("wind") val wind: WindDetails?,
    @SerializedName("clouds") val clouds: CloudsDetails?,
    @SerializedName("rain") val rain: PrecipitationDetails?,
    @SerializedName("snow") val snow: PrecipitationDetails?,
    @SerializedName("dt") val dt: Long?,
    @SerializedName("sys") val sys: SysDetails?,
    @SerializedName("timezone") val timezone: Int?,
    @SerializedName("id") val cityId: Int?,
    @SerializedName("cod") val cod: Int?
)

data class CoordDetails(
    @SerializedName("lon") val lon: Double?,
    @SerializedName("lat") val lat: Double?
)

data class WeatherCondition(
    @SerializedName("id") val id: Int?,
    @SerializedName("main") val main: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("icon") val icon: String?
)

data class MainDetails(
    @SerializedName("temp") val temp: Double?,
    @SerializedName("feels_like") val feelsLike: Double?,
    @SerializedName("temp_min") val tempMin: Double?,
    @SerializedName("temp_max") val tempMax: Double?,
    @SerializedName("pressure") val pressure: Int?,
    @SerializedName("humidity") val humidity: Int?,
    @SerializedName("sea_level") val seaLevelPressure: Int?,
    @SerializedName("grnd_level") val groundLevelPressure: Int?
)

data class WindDetails(
    @SerializedName("speed") val speed: Double?,
    @SerializedName("deg") val deg: Int?,
    @SerializedName("gust") val gust: Double?
)

data class CloudsDetails(
    @SerializedName("all") val all: Int?
)

data class PrecipitationDetails(
    @SerializedName("1h") val lastHour: Double?
)

data class SysDetails(
    @SerializedName("type") val type: Int?,
    @SerializedName("id") val id: Int?,
    @SerializedName("country") val country: String?,
    @SerializedName("sunrise") val sunrise: Long?,
    @SerializedName("sunset") val sunset: Long?
)