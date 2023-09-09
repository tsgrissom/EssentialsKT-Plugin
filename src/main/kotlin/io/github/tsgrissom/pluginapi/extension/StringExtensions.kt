package io.github.tsgrissom.pluginapi.extension

fun String.equalsIc(s: String) : Boolean = this.equals(s, ignoreCase=true)
fun String.equalsIc(vararg matches: String) : Boolean = matches.firstOrNull { this.equalsIc(it) } != null