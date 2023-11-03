package io.github.tsgrissom.pluginapi.utility

import io.github.tsgrissom.pluginapi.extension.bukkit.getDynamicHoverEvent
import net.md_5.bungee.api.ChatColor as BungeeChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.ChatColor

class StringUtility {

    /**
     * TODO Generate multi-line list
     */
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
        ) : String {
            return createFormattedList(
                name, collection, delimiter=delimiter,
                colorPrimary=ChatColor.WHITE, colorPunctuation=ChatColor.WHITE, colorValue=ChatColor.WHITE
            )
        }

        fun createHoverableFormattedList(
            name: String = "List",
            collection: Collection<String>,
            delimiter: String = ", ",
            onHoverElement: (String) -> Array<BaseComponent>,
            colorPrimary: BungeeChatColor = BungeeChatColor.GOLD,
            colorPunctuation: BungeeChatColor = BungeeChatColor.DARK_GRAY,
            colorValue: BungeeChatColor = BungeeChatColor.YELLOW
        ) : Array<BaseComponent> {
            val root = TextComponent(name)
            root.color = colorPrimary
            fun finish() = ComponentBuilder(root).create()

            val colon = TextComponent(": ")
            colon.color = colorPunctuation
            root.addExtra(colon)

            if (collection.isEmpty()) {
                val none = TextComponent("None")
                none.color = BungeeChatColor.RED

                root.addExtra(none)

                return finish()
            }

            val delim = TextComponent(delimiter)
            delim.color = colorPunctuation

            val list = TextComponent()

            for ((i, el) in collection.withIndex()) {
                val hoverText = onHoverElement(el)
                val item = TextComponent(el)
                item.color = colorValue
                item.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(hoverText))

                list.addExtra(item)

                if (i != (collection.size - 1)) {
                    list.addExtra(delim)
                }
            }

            root.addExtra(list)

            return finish()
        }

        fun createClickableFormattedList(
            name: String = "List",
            collection: Collection<String>,
            delimiter: String = ", ",
            onClickAction: (String) -> ClickEvent.Action,
            onClickValue: (String) -> String,
            colorPrimary: BungeeChatColor = BungeeChatColor.GOLD,
            colorPunctuation: BungeeChatColor = BungeeChatColor.DARK_GRAY,
            colorValue: BungeeChatColor = BungeeChatColor.YELLOW
        ) : Array<BaseComponent> {
            val root = TextComponent(name)
            root.color = colorPrimary
            fun finish() = ComponentBuilder(root).create()

            val colon = TextComponent(": ")
            colon.color = colorPunctuation
            root.addExtra(colon)

            if (collection.isEmpty()) {
                val none = TextComponent("None")
                none.color = BungeeChatColor.RED

                root.addExtra(none)

                return finish()
            }

            val delim = TextComponent(delimiter)
            delim.color = colorPunctuation

            val list = TextComponent()

            for ((i, el) in collection.withIndex()) {
                val action = onClickAction(el)
                val value = onClickValue(el)
                val item = TextComponent(el)

                item.color = colorValue
                item.clickEvent = ClickEvent(action, value)
                item.hoverEvent = item.clickEvent.getDynamicHoverEvent()

                list.addExtra(item)

                if (i != (collection.size - 1)) {
                    list.addExtra(delim)
                }
            }

            root.addExtra(list)

            return finish()
        }
    }
}