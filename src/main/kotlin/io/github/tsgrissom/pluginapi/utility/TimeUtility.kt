package io.github.tsgrissom.pluginapi.utility

import org.bukkit.ChatColor

class TimeUtility {

    companion object {
        // 12hr time without a space between the time->AM/PM
        const val REGEX_12HR_CLOCK = "^(0[1-9]|1[0-2]):[0-5]\\d[APap][Mm]\$"
        // Military time
        const val REGEX_24HR_CLOCK = "([01]\\d|2[0-3]):[0-5]\\d\$"
        // 1h, 09H, etc.
        const val REGEX_HOURS = "^(?i)\\d+[hH]\$"
        // 10m, 20M, etc.
        const val REGEX_MINUTES = "^(?i)\\d+[mM]\$"
        // 10s, 20S, etc.
        const val REGEX_SECONDS = "^(?i)\\d+[sS]\$"
    }

    fun isInputInSeconds(input: String) : Boolean =
        REGEX_SECONDS.toRegex().matches(input)
    fun isInputInMinutes(input: String) : Boolean =
        REGEX_MINUTES.toRegex().matches(input)
    fun isInputInHours(input: String) : Boolean =
        REGEX_HOURS.toRegex().matches(input)
    fun isInputAs12HourClock(input: String) : Boolean =
        REGEX_12HR_CLOCK.toRegex().matches(input)
    fun isInputAs24HourClock(input: String) : Boolean =
        REGEX_24HR_CLOCK.toRegex().matches(input)

    fun convert12HTo24H(time12Hour: String) : String? {
        try {
            // Attempt to match the input string with the regex pattern
            val matchResult = REGEX_12HR_CLOCK.toRegex().find(time12Hour)

            if (matchResult != null) {
                // Extract components from the matched result
                val (hourStr, minuteStr) = matchResult.destructured
                val hour = hourStr.toInt()
                val minute = minuteStr.toInt()

                // Adjust the hour based on "AM" or "PM"
                val isPM = time12Hour.endsWith("PM", ignoreCase = true)

                return if (isPM && hour != 12) {
                    // If it's PM and not 12:00 PM, add 12 hours to the hour component
                    val hour24 = hour + 12
                    String.format("%02d:%02d", hour24, minute)
                } else if (!isPM && hour == 12) {
                    // If it's 12:00 AM, convert to 00:00
                    String.format("00:%02d", minute)
                } else {
                    // Otherwise, it's already in 24-hour format
                    String.format("%02d:%02d", hour, minute)
                }
            }
        } catch (e: Throwable) {
            // Handle any exceptions that may occur during parsing or conversion
            e.printStackTrace()
        }

        // Return null if the input string doesn't match the expected format
        return null
    }

    fun convertTicksTo24Hour(ticks: Long, withColor: Boolean = false) : String {
        val ticksPerDay = 24000
        val ticksPerHour = ticksPerDay / 24

        // Calculate the number of hours and minutes
        val hours = (ticks % ticksPerDay) / ticksPerHour
        val minutes = ((ticks % ticksPerDay) % ticksPerHour) * 60 / ticksPerHour

        // Format the time as "HH:mm"
        val valueColor = if (withColor) ChatColor.RED else ""
        val accentColor = if (withColor) ChatColor.DARK_GRAY else ""
        return String.format("${valueColor}%02d${accentColor}:${valueColor}%02d", hours, minutes)
    }

    fun convert24HourToTicks(time: String) : Long {
        if (!isInputAs24HourClock(time))
            error("\"$time\" is not a valid 24 hour time String")

        val split = time.split(":")
        if (split.size > 2)
            error("\"$time\" split by \":\" resulted in more than two pieces. Should NEVER happen.")

        val strHours = split[0]
        val hours = strHours.toIntOrNull()
            ?: error("\"$strHours\" is not an integer")
        val strMins = split[1]
        val mins = strMins.toIntOrNull()
            ?: error("\"$strMins\" is not an integer")

        // 1min = 1200 ticks
        // 60mins = 72000 ticks
        val hoursAsTicks = 72000L * hours
        val minsAsTicks = 1200L * mins

        return hoursAsTicks + minsAsTicks
    }
}