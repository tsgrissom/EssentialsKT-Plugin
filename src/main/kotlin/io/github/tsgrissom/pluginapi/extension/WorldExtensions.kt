package io.github.tsgrissom.pluginapi.extension

import org.bukkit.World

fun World.isRaining() = !this.isClearWeather
fun World.setRaining(b: Boolean) = if (b) this.makeRain() else this.clearRain()
fun World.toggleRain() = if (this.isRaining()) clearRain() else makeRain()

fun World.makeRain(ticks: Int = 20000) {
    this.weatherDuration = ticks
    this.clearWeatherDuration = 1
}

fun World.clearRain(ticks: Int = 25000) {
    this.weatherDuration = 1
    this.clearWeatherDuration = ticks
}