package com.game.wildcatcher.domain.weather

enum class WeatherCondition(val label: String, val visibility: Float, val bubbleDrift: Float) {
    SUNNY("晴空萬里", 1.0f, 0.0f),
    MIST("雲霧繚繞", 0.75f, 0.05f),
    RAIN("熱帶暴雨", 0.55f, 0.15f)
}

enum class TimeOfDay(val label: String, val isNight: Boolean) {
    DAWN("清晨薄曦", false),
    DAYLIGHT("明亮日光", false),
    DUSK("夕陽暮色", false),
    STARLIGHT("星夜幽光", true)
}

data class EnvironmentState(
    val timeOfDay: TimeOfDay = TimeOfDay.DAYLIGHT,
    val weather: WeatherCondition = WeatherCondition.SUNNY,
    val windForce: Float = 0.0f
)

class WeatherDayNightEngine {
    private var timeCounter: Float = 0.0f

    fun getEnvironmentState(gameTimeSeconds: Long): EnvironmentState {
        val cycle = (gameTimeSeconds % 240) / 60 // 4 minute day/night cycle
        val timeOfDay = when (cycle) {
            0L -> TimeOfDay.DAWN
            1L -> TimeOfDay.DAYLIGHT
            2L -> TimeOfDay.DUSK
            else -> TimeOfDay.STARLIGHT
        }

        val weatherIndex = ((gameTimeSeconds / 90) % 3).toInt()
        val weather = when (weatherIndex) {
            0 -> WeatherCondition.SUNNY
            1 -> WeatherCondition.MIST
            else -> WeatherCondition.RAIN
        }

        val wind = when (weather) {
            WeatherCondition.SUNNY -> 0.2f
            WeatherCondition.MIST -> 0.5f
            WeatherCondition.RAIN -> 1.2f
        }

        return EnvironmentState(
            timeOfDay = timeOfDay,
            weather = weather,
            windForce = wind
        )
    }
}
