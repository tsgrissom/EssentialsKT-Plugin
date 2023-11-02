package io.github.tsgrissom.pluginapi.extension

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.hover.content.Text

fun ClickEvent.getDynamicHoverEvent() : HoverEvent? {
    fun generateHoverEvent(text: String) : HoverEvent =
        HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(text.translateColor()))
    
    val action = this.action
    val value = this.value

    return when (action) {
        ClickEvent.Action.CHANGE_PAGE -> generateHoverEvent("${ChatColor.GRAY}Click to change page")
        ClickEvent.Action.COPY_TO_CLIPBOARD -> generateHoverEvent("${ChatColor.GRAY}Click to copy to clipboard: ${ChatColor.YELLOW}$value")
        ClickEvent.Action.OPEN_FILE -> generateHoverEvent("${ChatColor.GRAY}Click to open file")
        ClickEvent.Action.OPEN_URL -> generateHoverEvent("${ChatColor.GRAY}Click to open URL: ${ChatColor.YELLOW}$value")
        ClickEvent.Action.SUGGEST_COMMAND -> generateHoverEvent("${ChatColor.GRAY}Click to suggest command: ${ChatColor.YELLOW}$value")
        ClickEvent.Action.RUN_COMMAND -> generateHoverEvent("${ChatColor.GRAY}Click to run command: ${ChatColor.YELLOW}$value")
        else -> null
    }
}