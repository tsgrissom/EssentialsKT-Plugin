package io.github.tsgrissom.pluginapi.misc

import io.github.tsgrissom.pluginapi.extension.translateColor
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class HoverableText(
    private var autoscroll: Boolean,
    private var text: String,
    private var hoverText: MutableList<String>
) {

    companion object {
        fun compose(initialText: String) : HoverableText = HoverableText(initialText)
    }

    fun shouldAutoscroll() = this.autoscroll
    fun autoscroll(b: Boolean) : HoverableText {
        this.autoscroll = b
        return this
    }

    constructor(text: String) : this(false, text, mutableListOf())
    constructor(text: String, vararg initialHoverText: String) : this(false, text, initialHoverText.toMutableList())

    fun text(t: String) : HoverableText {
        this.text = t
        return this
    }

    fun hoverText(t: List<String>) : HoverableText {
        this.hoverText = t.toMutableList()
        return this
    }

    fun hoverText(vararg t: String) : HoverableText {
        this.hoverText = t.toMutableList()
        return this
    }

    fun addHoverText(t: String) : HoverableText {
        this.hoverText.add(t)
        return this
    }

    fun toTextComponent() : TextComponent {
        val component = TextComponent(text.translateColor())
        val e = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("${hoverText[0].translateColor()}\n"))

        if (hoverText.size > 1) {
            val subL = hoverText.subList(1, hoverText.size)
            for (t in subL) {
                var new = t.translateColor()
                if (autoscroll)
                    new += "\n"
                e.addContent(Text(new))
            }
        }

        component.hoverEvent = e
        return component
    }

    fun send(to: CommandSender) {
        if (to is ConsoleCommandSender)
            return sendUnfolded(to)
        if (to !is Player)
            return

        sendHoverText(to)
    }

    private fun sendHoverText(to: Player) {
        to.spigot().sendMessage(this.toTextComponent())
    }

    private fun getUnfolded() : List<String> {
        val ls = mutableListOf<String>()

        ls.add(text)
        ls.addAll(hoverText.map { it.translateColor() })

        return ls
    }

    private fun sendUnfolded(to: CommandSender) = getUnfolded().forEach { to.sendMessage(it) }
}