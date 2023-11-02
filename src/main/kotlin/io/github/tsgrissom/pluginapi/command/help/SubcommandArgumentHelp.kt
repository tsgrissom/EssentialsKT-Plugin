package io.github.tsgrissom.pluginapi.command.help

import io.github.tsgrissom.pluginapi.extension.bukkit.appendc
import io.github.tsgrissom.pluginapi.extension.kt.translateColor
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text

class SubcommandArgumentHelp(
    val name: String,
    private var required: Boolean = false
) {

    companion object {
        fun compose(name: String) = SubcommandArgumentHelp(name)
    }

    private var hoverText: MutableList<String> = mutableListOf()

    fun required(b: Boolean) : SubcommandArgumentHelp {
        this.required = b
        return this
    }

    fun hoverText(vararg text: String) : SubcommandArgumentHelp {
        this.hoverText = text.map { it.translateColor() }.toMutableList()
        return this
    }

    fun toComponents() : Array<BaseComponent> {
        val onHover: MutableList<Text> = mutableListOf()
        val requiredLine = ComponentBuilder()
            .appendc("Required: ", ChatColor.GRAY)
            .appendc(if (required) "Yes" else "No", if (required) ChatColor.GREEN else ChatColor.RED)
            .append("\n")
            .create()

        onHover.add(Text(requiredLine))

        if (hoverText.isNotEmpty()) {
            for ((i, str) in hoverText.withIndex()) {
                val postfix = if (i != (hoverText.size - 1)) "\n" else ""
                onHover.add(Text("$str$postfix"))
            }
        }

        val nameComp = TextComponent(name)

        if (onHover.isNotEmpty()) {
            nameComp.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, onHover.toList())
        }

        val comp = ComponentBuilder()
            .append(if (required) "<" else "[")
            .append(nameComp)
            .append(if (required) ">" else "]")

        return comp.create()
    }
}