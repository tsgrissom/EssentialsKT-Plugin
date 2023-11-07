package io.github.tsgrissom.essentialskt.config

import BukkitChatColor
import BungeeChatColor

enum class ChatColorKey(val defaultBukkit: BukkitChatColor, val defaultBungee: BungeeChatColor) {
    Detail(BukkitChatColor.RED, BungeeChatColor.RED),
    Error(BukkitChatColor.DARK_RED, BungeeChatColor.DARK_RED),
    ErrorDetail(BukkitChatColor.RED, BungeeChatColor.RED),
    Primary(BukkitChatColor.GOLD, BungeeChatColor.GOLD),
    Secondary(BukkitChatColor.GRAY, BungeeChatColor.GRAY),
    Success(BukkitChatColor.GREEN, BungeeChatColor.GREEN),
    Tertiary(BukkitChatColor.DARK_GRAY, BungeeChatColor.DARK_GRAY),
    Type(BukkitChatColor.AQUA, BungeeChatColor.AQUA),
    Username(BukkitChatColor.YELLOW, BungeeChatColor.AQUA),
    Value(BukkitChatColor.YELLOW, BungeeChatColor.YELLOW);
}