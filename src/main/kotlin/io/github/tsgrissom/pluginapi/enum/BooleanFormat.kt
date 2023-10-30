package io.github.tsgrissom.pluginapi.enum

/**
 * Enumeration of the types of BooleanFormats for the Boolean#fmt extension method. Provides a true and a false String.
 */
enum class BooleanFormat(val trueStr: String, val falseStr: String) {

    /**
     * true->"enable" or false->"disable"
     */
    ENABLE_DISABLE("enable", "disable"),

    /**
     * true->"enabled" or false->"disabled"
     */
    ENABLED_DISABLED("enabled", "disabled"),

    /**
     * true->"true" or false->"false"
     */
    TRUE_FALSE("true", "false"),

    /**
     * true->"on" or false->"off"
     */
    ON_OFF("on", "off"),

    /**
     * true->"yes" or false->"no"
     */
    YES_NO("yes", "no");
}