package io.github.tsgrissom.pluginapi.command.flag

class ValidCommandFlag(val qualifiedName: String) {

    companion object {
        val FLAG_GRAPHICAL = ValidCommandFlag("gui")
    }

    init {
        if (qualifiedName.startsWith("-"))
            error("Qualified command flag names should not start with -")
        val trimmed = qualifiedName.trim()
        if (trimmed == "")
            error("Qualified command flag names should not be whitespace")
        if (trimmed.length <= 1)
            error("Qualified command flag names should be longer than 1 character")
    }

    private val shortName: String = qualifiedName.substring(0, 1)

    fun isShortNameUppercased() =
        shortName == shortName.uppercase()
    fun getShortName() =
        this.shortName
    fun getShortNameAsChar() =
        shortName.toCharArray()[0]
    fun toPair() : Pair<String, String> =
        Pair(qualifiedName, shortName)
}