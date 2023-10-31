package io.github.tsgrissom.essentialskt.enum

import org.bukkit.ChatColor

enum class ChatColorKey(val defaultValue: ChatColor) {
    Detail(ChatColor.RED),
    Error(ChatColor.DARK_RED),
    ErrorDetail(ChatColor.RED),
    Primary(ChatColor.GOLD),
    Secondary(ChatColor.GRAY),
    Success(ChatColor.GREEN),
    Type(ChatColor.AQUA),
    Username(ChatColor.YELLOW);
}