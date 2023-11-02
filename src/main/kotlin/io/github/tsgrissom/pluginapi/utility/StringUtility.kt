package io.github.tsgrissom.pluginapi.utility

import net.md_5.bungee.api.ChatColor as BungeeChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Content
import org.bukkit.ChatColor

class StringUtility {

    companion object {
        fun createFormattedList(
            name: String = "List",
            collection: Collection<String>,
            withColor: Boolean = true,
            delimiter: String = ", ",
            colorPrimary: ChatColor = ChatColor.GOLD,
            colorPunctuation: ChatColor = ChatColor.DARK_GRAY,
            colorValue: ChatColor = ChatColor.YELLOW
        ) : String {
            var str = ""
            fun appendColorIf(color: ChatColor) {
                if (withColor)
                    str += color.toString()
            }

            appendColorIf(colorPrimary)
            str += name

            appendColorIf(colorPunctuation)
            str += ": "

            if (collection.isEmpty()) {
                appendColorIf(ChatColor.RED)
                str += "None"
                return str
            }

            for ((i, elem) in collection.withIndex()) {
                appendColorIf(colorValue)
                str += elem
                if (i != (collection.size - 1)) {
                    appendColorIf(colorPunctuation)
                    str += delimiter
                }
            }

            return str
        }

        fun createPlainFormattedList(
            name: String = "List",
            collection: Collection<String>,
            delimiter: String = ", "
        ) = createFormattedList(name, collection, delimiter=delimiter)

        fun createHoverableFormattedList(
            name: String = "List",
            collection: Collection<String>,
            delimiter: String = ", ",
            onHoverElement: (str: String) -> Array<BaseComponent>,
            colorPrimary: BungeeChatColor = BungeeChatColor.GOLD,
            colorPunctuation: BungeeChatColor = BungeeChatColor.DARK_GRAY,
            colorValue: BungeeChatColor = BungeeChatColor.YELLOW,
        ) : Array<BaseComponent> {
            val parent = TextComponent(name)
            parent.color = colorPrimary
            fun finish() = ComponentBuilder(parent).create()

            val colon = TextComponent(": ")
            colon.color = colorPunctuation
            parent.addExtra(colon)

            if (collection.isEmpty()) {
                val none = TextComponent("None")
                none.color = BungeeChatColor.RED

                parent.addExtra(none)

                return finish()
            }

            val delimiterComponent = TextComponent(delimiter)
            delimiterComponent.color = colorPunctuation

            val list = TextComponent()

            for ((i, el) in collection.withIndex()) {
                val hoverText = onHoverElement(el)
                val item = TextComponent(el)
                item.color = colorValue
                item.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText)

                list.addExtra(item)

                if (i != (collection.size - 1)) {
                    list.addExtra(delimiterComponent)
                }
            }

            parent.addExtra(list)

            return finish()
        }

        fun createClickableFormattedList(

        ) : Array<BaseComponent> {
            return ComponentBuilder().create()
        }
    }
}