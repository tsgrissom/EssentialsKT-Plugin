package io.github.tsgrissom.pluginapi.enum

enum class BooleanFormat(val trueStr: String, val falseStr: String) {
    ENABLE_DISABLE("enable", "disable"),
    ENABLED_DISABLED("enabled", "disabled"),
    TRUE_FALSE("true", "false"),
    ON_OFF("on", "off"),
    YES_NO("yes", "no");
}