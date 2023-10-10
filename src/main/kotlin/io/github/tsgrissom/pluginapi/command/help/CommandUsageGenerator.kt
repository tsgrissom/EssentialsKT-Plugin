package io.github.tsgrissom.pluginapi.command.help

import io.github.tsgrissom.pluginapi.command.CommandContext
import org.bukkit.ChatColor

/*
 * Should be able to generate a few types of command usage texts
 * Examples:
 * "Usage: /heal <player>"
 * "Usage: /whois [temporary,permanent]"
 * "Usage: /whois [temporary,permanent]. Do /whois ? for help."
 */
class CommandUsageGenerator(
    val context: CommandContext,
    val fgColor: ChatColor,
    val detailFgColor: ChatColor
) {
    // TODO Implement CommandUsageGenerator
}