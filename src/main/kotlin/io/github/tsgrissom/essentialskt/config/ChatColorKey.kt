package io.github.tsgrissom.essentialskt.config

import org.bukkit.ChatColor

enum class ChatColorKey(val defaultValue: ChatColor) {
    Detail(ChatColor.RED),
    Error(ChatColor.DARK_RED),
    ErrorDetail(ChatColor.RED),
    Primary(ChatColor.GOLD),
    Secondary(ChatColor.GRAY),
    Success(ChatColor.GREEN),
    Tertiary(ChatColor.DARK_GRAY),
    Type(ChatColor.AQUA),
    Username(ChatColor.YELLOW),
    Value(ChatColor.YELLOW);
}