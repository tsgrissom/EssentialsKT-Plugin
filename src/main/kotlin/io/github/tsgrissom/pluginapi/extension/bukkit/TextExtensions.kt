package io.github.tsgrissom.pluginapi.extension.bukkit

import io.github.tsgrissom.pluginapi.extension.kt.equalsIc
import io.github.tsgrissom.pluginapi.func.NonFormattingChatColorPredicate
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Material

// MARK: ChatColor Extensions

/**
 * Creates a Set of Strings representing input aliases for the receiver ChatColor. These aliases are potential forms
 * that a user might enter with the intent of using the receiver ChatColor.
 * @return A Set of Strings representing the receiver ChatColor.
 */
fun org.bukkit.ChatColor.getValidInputAliases() : Set<String> {
    val set = mutableSetOf<String>()
    val name = this.name
    val code = this.char

    set.add(name)
    set.add("&${code}")
    set.add("$code")

    if (name.contains("_"))
        set.add(name.replace("_", ""))

    if (name.contains("GRAY")) {
        val altSpelling = name.replace("GRAY", "GREY")
        set.add(altSpelling)
        set.add(altSpelling.replace("_", ""))
    }

    return set.toSet()
}

/**
 * Checks if the requisite String is an input alias for the receiver ChatColor.
 * @param str The String to test.
 * @return Whether the provided string is an input alias for the ChatColor.
 */
fun org.bukkit.ChatColor.isInputAlias(str: String) : Boolean {
    val valid = getValidInputAliases()

    for (alias in valid) {
        if (alias.equalsIc(str))
            return true
    }

    return false
}

/**
 * Converts a Bukkit ChatColor value to a BungeeChatColor for compatibility with the Chat Component API. Does not
 * support formatting chat colors.
 * @return A Bungee ChatColor or null.
 */
fun org.bukkit.ChatColor.convertToBungeeChatColor(): ChatColor? {
    val formattingColors = org.bukkit.ChatColor.entries.filterNot { NonFormattingChatColorPredicate().test(it) }.toList()
    val name = this.name
    fun warnAndNull(str: String) : ChatColor? {
        Bukkit.getLogger().warning(str)
        return null
    }

    if (formattingColors.contains(this))
        return warnAndNull("Cannot convert Bukkit ChatColor.${name} to a BungeeChatColor: Formatting codes are not able to be converted")

    return ChatColor.of(name) ?: warnAndNull("Unable to resolve BungeeChatColor for name \"$name\"")
}
// TODO Convert to Bungee ChatColor and vice-versa

/**
 * Attempts to match the ChatColor to a Material to represent it in GUIs. Falls back to glass if no matches are made.
 * @return The best matching Material for the ChatColor.
 */
fun org.bukkit.ChatColor.getRepresentativeMaterial() : Material {
    val def = Material.GLASS
    val name = this.name
    val special = mapOf(
        org.bukkit.ChatColor.GREEN to Material.LIME_WOOL,
        org.bukkit.ChatColor.DARK_GREEN to Material.GREEN_WOOL,
        org.bukkit.ChatColor.AQUA to Material.LIGHT_BLUE_WOOL,
        org.bukkit.ChatColor.DARK_AQUA to Material.CYAN_WOOL,
        org.bukkit.ChatColor.BLUE to Material.BLUE_CONCRETE_POWDER,
        org.bukkit.ChatColor.DARK_BLUE to Material.BLUE_WOOL,
        org.bukkit.ChatColor.RED to Material.RED_WOOL,
        org.bukkit.ChatColor.DARK_RED to Material.REDSTONE_BLOCK,
        org.bukkit.ChatColor.GRAY to Material.LIGHT_GRAY_WOOL,
        org.bukkit.ChatColor.DARK_GRAY to Material.GRAY_WOOL,
        org.bukkit.ChatColor.GOLD to Material.YELLOW_WOOL,
        org.bukkit.ChatColor.YELLOW to Material.GOLD_BLOCK,
        org.bukkit.ChatColor.LIGHT_PURPLE to Material.PURPLE_WOOL,
        org.bukkit.ChatColor.DARK_PURPLE to Material.PURPLE_CONCRETE
    )

    if (special.contains(this))
        return special[this]!!

    return Material.entries.firstOrNull { it.name.contains(name, ignoreCase=true) } ?: def
}

// MARK: ComponentBuilder Extensions

/**
 * Shorthand method for appending a String and then a ChatColor to a ComponentBuilder. Reduces mental overhead and
 * increases readability of the construction of complex ComponentBuilder chains.
 *
 * @param str The String to append to the ComponentBuilder before appending a ChatColor.
 * @param color The ChatColor to append to the ComponentBuilder after appending the String.
 * @return The ComponentBuilder instance to continue method chaining.
 */
fun ComponentBuilder.appendc(str: String, color: ChatColor) : ComponentBuilder =
    this.append(str).color(color)

/**
 * Shorthand method for appending a TextComponent and then a ChatColor to a ComponentBuilder. Reduces mental overhead
 * and increases readability of the construction of complex ComponentBuilder chains.
 *
 * @param text The TextComponent to append to the ComponentBuilder before appending a ChatColor.
 * @param color The ChatColor to append to the ComponentBuilder after appending the text.
 * @return The ComponentBuilder instance to continue method chaining.
 */
fun ComponentBuilder.appendc(text: TextComponent, color: ChatColor) : ComponentBuilder =
    this.append(text).color(color)