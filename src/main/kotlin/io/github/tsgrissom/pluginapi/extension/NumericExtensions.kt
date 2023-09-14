package io.github.tsgrissom.pluginapi.extension

fun Double.roundToDigits(i: Int) : Double =
    String.format("%.${i}f", this).toDouble()