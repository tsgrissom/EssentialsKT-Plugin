package io.github.tsgrissom.pluginapi.func

import org.bukkit.ChatColor
import java.util.function.Predicate

class NonFormattingChatColorPredicate : Predicate<ChatColor> {

    override fun test(color: ChatColor): Boolean {
        return !color.isFormat && color != ChatColor.RESET
    }
}