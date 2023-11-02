package io.github.tsgrissom.pluginapi.chat

import io.github.tsgrissom.pluginapi.extension.kt.translateColor
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class HoverableText(private var text: String) {

    companion object {

        /**
         * Begin creating a HoverableText via this static method.
         * @return A new HoverableText instance for the provided text.
         */
        fun compose(text: String) : HoverableText = HoverableText(text)
    }

    private var autoscroll: Boolean = true
    private var prependColor: ChatColor? = null
    private var hoverText: MutableList<String> = mutableListOf()

    /**
     * Whether newline characters will be automatically appended between lines
     * of the hover text.
     */
    fun shouldAutoscroll() = this.autoscroll

    /**
     * Whether a color will be applied to the TextComponent.
     */
    fun hasPrependColor() = this.prependColor != null

    /**
     * Whether the hover text field (a List) is empty.
     */
    fun hasHoverText() = this.hoverText.isNotEmpty()

    /**
     * Chainable. Sets whether to append newline characters between each line of the hover
     * text so that each separate String is automatically on a new line. Default true.
     * @param b Whether to append newline characters between each line of the hover text.
     * @return The instance of HoverableText for further building.
     */
    fun autoscroll(b: Boolean) : HoverableText {
        this.autoscroll = b
        return this
    }

    /**
     * Chainable. Sets a ChatColor to prepend to the text value when the TextComponent is
     * displayed to the user.
     * @param c The Bungee ChatColor to prepend to the text when the TextComponent is created.
     * @return The instance of HoverableText for further building.
     */
    fun color(c: ChatColor) : HoverableText {
        this.prependColor = c
        return this
    }

    /**
     * Chainable. Sets a String to the text value of the object to be displayed when the
     * TextComponent is served to the user. Use `HoverableText#color` to set the
     * TextComponent's color property.
     * @param s The String to serve to the Player when the TextComponent is displayed.
     * @return The instance of HoverableText for further building.
     */
    fun text(s: String) : HoverableText {
        this.text = s
        return this
    }

    /**
     * Chainable. Adds hover text to be displayed when the user hovers over the TextComponent
     * in their chat box.
     * @param l The String values to add to the hover text to be displayed on the TextComponent.
     * @return The instance of HoverableText for further building.
     */
    fun hoverText(l: List<String>) : HoverableText {
        this.hoverText.addAll(l.map { it.translateColor() })
        return this
    }

    /**
     * Chainable. Adds hover text to be displayed when the user hovers over the TextComponent
     * in their chat box.
     * @param s The String values to add to the hover text to be displayed on the TextComponent.
     * @return The instance of HoverableText for further building.
     */
    fun hoverText(vararg s: String) : HoverableText {
        this.hoverText.addAll(s.map { it.translateColor() })
        return this
    }

    /**
     * Chainable. Clears the existing hover text and adds the new text.
     * @param l The new String values to appear when hovering over the TextComponent.
     * @return The instance of HoverableText for further building.
     */
    fun setHoverText(l: List<String>) : HoverableText {
        this.hoverText.clear()
        this.hoverText.addAll(l.translateColor())
        return this
    }

    /**
     * Chainable. Clears the existing hover text.
     * @return The instance of HoverableText for further building.
     */
    fun resetHoverText() : HoverableText {
        this.hoverText.clear()
        return this
    }

    /**
     * Builds the completed HoverableText into a TextComponent. This TextComponent can be
     * safely inserted into another TextComponent, a ComponentBuilder, or sent by itself.
     * @return The constructed TextComponent.
     */
    fun toComponent() : TextComponent {
        val comp = TextComponent(text)

        if (prependColor != null)
            comp.color = prependColor

        if (hoverText.isNotEmpty()) {
            var onHover = ""

            for ((i, s) in hoverText.withIndex()) {
                onHover += s.translateColor()

                if (autoscroll && i != (hoverText.size - 1))
                    onHover += "\n"
            }

            val e = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(onHover))
            comp.hoverEvent = e
        }

        return comp
    }

    /**
     * Invokes `HoverableText#toComponent()` and sends the constructed TextComponent
     * to the supplied CommandSender.
     * @param to Who to send the constructed TextComponent to.
     */
    fun send(to: CommandSender) {
        if (to is ConsoleCommandSender)
            return sendUnfolded(to)
        if (to !is Player)
            return

        sendHoverText(to)
    }

    private fun sendHoverText(to: Player) {
        to.spigot().sendMessage(this.toComponent())
    }

    private fun getUnfolded() : List<String> {
        val ls = mutableListOf<String>()

        ls.add(text)
        ls.addAll(hoverText.map { it.translateColor() })

        return ls
    }

    private fun sendUnfolded(to: CommandSender) = getUnfolded().forEach { to.sendMessage(it) }
}