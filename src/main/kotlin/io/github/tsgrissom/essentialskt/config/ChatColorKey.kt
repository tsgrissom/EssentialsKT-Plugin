package io.github.tsgrissom.essentialskt.config

import org.bukkit.ChatColor
import net.md_5.bungee.api.ChatColor as BungeeChatColor

enum class ChatColorKey(val defaultBukkit: ChatColor, val defaultBungee: BungeeChatColor) {
    Detail(ChatColor.RED, BungeeChatColor.RED),
    Error(ChatColor.DARK_RED, BungeeChatColor.DARK_RED),
    ErrorDetail(ChatColor.RED, BungeeChatColor.RED),
    Primary(ChatColor.GOLD, BungeeChatColor.GOLD),
    Secondary(ChatColor.GRAY, BungeeChatColor.GRAY),
    Success(ChatColor.GREEN, BungeeChatColor.GREEN),
    Tertiary(ChatColor.DARK_GRAY, BungeeChatColor.DARK_GRAY),
    Type(ChatColor.AQUA, BungeeChatColor.AQUA),
    Username(ChatColor.YELLOW, BungeeChatColor.AQUA),
    Value(ChatColor.YELLOW, BungeeChatColor.YELLOW);
}