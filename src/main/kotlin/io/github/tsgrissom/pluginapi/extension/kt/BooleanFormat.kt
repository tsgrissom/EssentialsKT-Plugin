package io.github.tsgrissom.pluginapi.extension.kt

/**
 * Enumeration of the types of BooleanFormats for the Boolean#fmt extension method. Provides a true and a false String.
 */
enum class BooleanFormat(val trueStr: String, val falseStr: String) {

    /**
     * true->"agree" or false->"disagree"
     */
    AGREE_DISAGREE("agree", "disagree"),

    /**
     * true->"approved" or false->"denied"
     */
    APPROVED_DENIED("approved", "denied"),

    /**
     * true->"authorized" or false->"unauthorized"
     */
    AUTHORIZED_UNAUTHORIZED("authorized", "unauthorized"),

    /**
     * true->"correct" or false->"incorrect"
     */
    CORRECT_INCORRECT("correct", "incorrect"),

    /**
     * true->"enable" or false->"disable"
     */
    ENABLE_DISABLE("enable", "disable"),

    /**
     * true->"enabled" or false->"disabled"
     */
    ENABLED_DISABLED("enabled", "disabled"),

    /**
     * true->"is" or false->"isn't"
     */
    IS_ISNT("is", "isn't"),

    /**
     * true->"on" or false->"off"
     */
    ON_OFF("on", "off"),

    /**
     * true->"permitted" or false->"forbidden"
     */
    PERMITTED_FORBIDDEN("permitted", "forbidden"),

    /**
     * true->"+" or false->"-"
     */
    PLUS_MINUS_SYMBOLS("+", "-"),

    /**
     * true->"prepared" or false->"unprepared"
     */
    PREPARED_UNPREPARED("prepared", "unprepared"),

    /**
     * true->"present" or false->"absent"
     */
    PRESENT_ABSENT("present", "absent"),

    /**
     * true->"true" or false->"false"
     */
    TRUE_FALSE("true", "false"),

    /**
     * true->"yes" or false->"no"
     */
    YES_NO("yes", "no");
}