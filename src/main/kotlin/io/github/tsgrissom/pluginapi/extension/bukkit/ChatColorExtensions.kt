package io.github.tsgrissom.pluginapi.extension.bukkit

import io.github.tsgrissom.pluginapi.extension.kt.equalsIc
import io.github.tsgrissom.pluginapi.func.NonFormattingChatColorPredicate
import net.md_5.bungee.api.ChatColor as BungeeChatColor
import org.bukkit.ChatColor
import org.bukkit.Material

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

fun ChatColor.getRepresentativeMaterial() : Material {
    val def = Material.GLASS
    val name = this.name
    val special = mapOf(
        ChatColor.GREEN to Material.LIME_WOOL,
        ChatColor.DARK_GREEN to Material.GREEN_WOOL,
        ChatColor.AQUA to Material.LIGHT_BLUE_WOOL,
        ChatColor.DARK_AQUA to Material.CYAN_WOOL,
        ChatColor.BLUE to Material.BLUE_CONCRETE_POWDER,
        ChatColor.DARK_BLUE to Material.BLUE_WOOL,
        ChatColor.RED to Material.RED_WOOL,
        ChatColor.DARK_RED to Material.REDSTONE_BLOCK,
        ChatColor.GRAY to Material.LIGHT_GRAY_WOOL,
        ChatColor.DARK_GRAY to Material.GRAY_WOOL,
        ChatColor.GOLD to Material.YELLOW_WOOL,
        ChatColor.YELLOW to Material.GOLD_BLOCK,
        ChatColor.LIGHT_PURPLE to Material.PURPLE_WOOL,
        ChatColor.DARK_PURPLE to Material.PURPLE_CONCRETE
    )

    if (special.contains(this))
        return special[this]!!

    return Material.entries.firstOrNull { it.name.contains(name, ignoreCase=true) } ?: def
}

// TODO Convert to Bungee ChatColor and vice-versa