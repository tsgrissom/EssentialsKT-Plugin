package io.github.tsgrissom.pluginapi.utility

import org.bukkit.ChatColor

class StringUtility {

    companion object {
        fun createFormattedList(
            name: String = "List",
            collection: Collection<String>,
            withColor: Boolean = true,
            delimiter: String = ", ",
            colorPrimary: ChatColor = ChatColor.GOLD,
            colorPunctuation: ChatColor = ChatColor.DARK_GRAY,
            colorValue: ChatColor = ChatColor.YELLOW
        ) : String {

            var str = ""
            fun appendColorIf(color: ChatColor) {
                if (withColor)
                    str += color.toString()
            }

            appendColorIf(colorPrimary)
            str += name

            appendColorIf(colorPunctuation)
            str += ": "

            if (collection.isEmpty()) {
                appendColorIf(ChatColor.RED)
                str += "None"
                return str
            }

            for ((i, elem) in collection.withIndex()) {
                appendColorIf(colorValue)
                str += elem
                if (i != (collection.size - 1)) {
                    appendColorIf(colorPunctuation)
                    str += delimiter
                }
            }

            return str
        }
    }
}