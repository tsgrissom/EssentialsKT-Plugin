package io.github.tsgrissom.pluginapi.extension.bukkit

import org.bukkit.World

// MARK: Weather

/**
 * Checks if the World's weather is not clear.
 * @return Whether it is currently raining in the World.
 */
fun World.isRaining() : Boolean =
    !this.isClearWeather

/**
 * Sets the weather of the World to be clear (b=false) or raining (b=true).
 * @param b Whether to make it rain or clear the rain.
 */
fun World.setRaining(b: Boolean) =
    if (b) this.makeRain() else this.clearRain()

/**
 * Toggles the weather of the World. If it is currently raining, clears the weather. Otherwise, makes it rain.
 */
fun World.toggleRain() =
    if (this.isRaining()) clearRain() else makeRain()

/**
 * Makes it rain for the requisite number of ticks. Achieves this by setting the World's weather duration to the
 * amount of ticks and setting the World's clear weather duration to 1 tick, or 1/20th of a second.
 * @param ticks The weather duration to set. Default=20000, or 1000 seconds.
 */
fun World.makeRain(ticks: Int = 20000) {
    this.weatherDuration = ticks
    this.clearWeatherDuration = 1
}

/**
 * Clears the rain for the requisite number of ticks. Achieves this by setting the World's weather duration to
 * 1 tick, or 1/20th of a second and setting the World's clear weather duration to the amount of ticks.
 * @param ticks The clear weather duration to set. Default=25000, or 1250 seconds.
 */
fun World.clearRain(ticks: Int = 25000) {
    this.weatherDuration = 1
    this.clearWeatherDuration = ticks
}