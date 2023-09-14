package io.github.tsgrissom.pluginapi.command

import io.github.tsgrissom.pluginapi.extension.translateColor
import org.bukkit.ChatColor

class LeadingSpacedString(private val offsetCount: Int = 2, private val text: String = "") {
    override fun toString(): String {
        var s = String()
        repeat(times=offsetCount) {
            s += " "
        }
        s += text
        return s
    }
}

class CommandHelpGenerator(val context: CommandContext) {

    var label: String = String()

    init {
        label = context.label
    }

    private fun generateTitleStr(
        prefixColor: ChatColor = ChatColor.GOLD,
        infixColor: ChatColor = ChatColor.DARK_GRAY,
        labelColor: ChatColor = ChatColor.YELLOW,
        prefixStr: String = "Command Help",
        infixStr: String = " -> ",
        prefixLabelWithSlash: Boolean = true,
        overrideLabel: String? = null,
        leadingOffset: Int = 2
    ) : String {
        val prefix = "${prefixColor}${prefixStr}"
        val infix = "${infixColor}${infixStr}"
        var label = "$labelColor"
        if (prefixLabelWithSlash)
            label += "/"
        label += overrideLabel ?: context.label

        return LeadingSpacedString(leadingOffset, "${prefix}${infix}${label}")
            .toString()
            .translateColor()
    }

    // TODO Subcommand text generation with permission checks
}

//class SubcommandHelp()
