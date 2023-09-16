package io.github.tsgrissom.pluginapi.misc

import io.github.tsgrissom.pluginapi.extension.translateColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ClickEvent.Action.*
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player

class ClickableText(
    private var text: String,
    private var action: ClickEvent.Action = COPY_TO_CLIPBOARD,
    private var value: String?
) {

    companion object {
        fun compose(text: String) : ClickableText = ClickableText(text,"https://google.com")
    }

    constructor(text: String, value: String) : this(text, COPY_TO_CLIPBOARD, value)
    constructor(text: String) : this(text, COPY_TO_CLIPBOARD, null)

    fun action(a: ClickEvent.Action) : ClickableText {
        this.action = a
        return this
    }

    fun text(t: String) : ClickableText {
        this.text = t
        return this
    }

    fun value(t: String) : ClickableText {
        this.value = t
        return this
    }

    fun toTextComponent() : TextComponent {
        fun getShowTextOnHoverEvent(text: String) : HoverEvent =
            HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(text.translateColor()))

        val component = TextComponent(text.translateColor())

        component.clickEvent = ClickEvent(action, value)
        val hoverEvent: HoverEvent? = when (action) {
            COPY_TO_CLIPBOARD -> getShowTextOnHoverEvent("&7Click to copy to clipboard")
            SUGGEST_COMMAND -> getShowTextOnHoverEvent("&7Click to suggest command")
            RUN_COMMAND -> getShowTextOnHoverEvent("&7Click to run command: &e$value")
            else -> null
        }

        if (hoverEvent != null)
            component.hoverEvent = hoverEvent

        return component
    }

    fun send(to: Player) {
        to.spigot().sendMessage(toTextComponent())
    }
}