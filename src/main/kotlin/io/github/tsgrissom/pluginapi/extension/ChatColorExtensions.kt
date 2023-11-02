package io.github.tsgrissom.pluginapi.extension

import io.github.tsgrissom.pluginapi.func.NonFormattingChatColorPredicate
import net.md_5.bungee.api.ChatColor as BungeeChatColor
import org.bukkit.ChatColor

fun ChatColor.getValidInputAliases() : Set<String> {
    val set = mutableSetOf<String>()
    val name = this.name

    set.add(name)

    if (name.contains("_"))
        set.add(name.replace("_", ""))

    if (name.contains("GRAY")) {
        val altSpelling = name.replace("GRAY", "GREY")
        set.add(altSpelling)
        set.add(altSpelling.replace("_", ""))
    }

    return set.toSet()
}

fun ChatColor.isInputAlias(str: String) : Boolean {
    val valid = getValidInputAliases()

    for (alias in valid) {
        if (alias.equalsIc(str))
            return true
    }

    return false
}

fun ChatColor.convertToBungeeChatColor(): BungeeChatColor {
    val formattingColors = ChatColor.entries.filterNot { NonFormattingChatColorPredicate().test(it) }.toList()
    val name = this.name

    if (formattingColors.contains(this))
        error("Cannot convert Bukkit ChatColor.${name} to a BungeeChatColor: Formatting codes are not able to be converted")

    return BungeeChatColor.of(name)
        ?: error("Unable to resolve BungeeChatColor for name \"$name\"")
}

// TODO Convert to Bungee ChatColor and vice-versa