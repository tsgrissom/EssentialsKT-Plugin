package io.github.tsgrissom.pluginapi.extension.bukkit

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.event.player.PlayerMoveEvent

fun ClickEvent.getDynamicHoverEvent(
    colorText: ChatColor = ChatColor.GRAY,
    colorValue: ChatColor = ChatColor.YELLOW
) : HoverEvent? {
    fun generateHoverEvent(text: String) : HoverEvent =
        HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(text))
    
    val action = this.action
    val value = this.value

    return when (action) {
        ClickEvent.Action.CHANGE_PAGE -> generateHoverEvent("${colorText}Click to change page")
        ClickEvent.Action.COPY_TO_CLIPBOARD -> generateHoverEvent("${colorText}Click to copy to clipboard: ${colorValue}$value")
        ClickEvent.Action.OPEN_FILE -> generateHoverEvent("${colorText}Click to open file")
        ClickEvent.Action.OPEN_URL -> generateHoverEvent("${colorText}Click to open URL: ${colorValue}$value")
        ClickEvent.Action.SUGGEST_COMMAND -> generateHoverEvent("${colorText}Click to suggest command: ${colorValue}$value")
        ClickEvent.Action.RUN_COMMAND -> generateHoverEvent("${colorText}Click to run command: ${colorValue}$value")
        else -> null
    }
}

val PlayerMoveEvent.isCameraMovement: Boolean
    get() = this.from.x == this.to?.x && this.from.y == this.to?.y && this.from.z == this.to?.z

val PlayerMoveEvent.isPhysicalMovement: Boolean
    get() = !this.isCameraMovement