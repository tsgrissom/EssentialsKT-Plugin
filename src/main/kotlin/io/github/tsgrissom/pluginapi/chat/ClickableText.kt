package io.github.tsgrissom.pluginapi.chat

import io.github.tsgrissom.pluginapi.extension.bukkit.getDynamicHoverEvent
import io.github.tsgrissom.pluginapi.extension.kt.translateColor
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ClickEvent.Action.*
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.command.CommandSender

class ClickableText(
    private var text: String,
    private var action: ClickEvent.Action = COPY_TO_CLIPBOARD,
    private var value: String?
) {

    private var prependColor: ChatColor? = null
    private val hoverText = mutableListOf<String>()

    companion object {

        /**
         * Begin creating a ClickableText via this static method.
         * @return A new ClickableText instance for the provided text.
         */
        fun compose(text: String) : ClickableText =
            ClickableText(text,"https://apple.com")
    }

    constructor(text: String, value: String) : this(text, COPY_TO_CLIPBOARD, value)
    constructor(text: String) : this(text, COPY_TO_CLIPBOARD, null)

    /**
     * Chainable. Sets the `ClickEvent.Action` which will be invoked when the user clicks on this TextComponent
     * in their chat box.
     * @param a The Action to invoke when the user clicks on the TextComponent.
     * @return The instance of ClickableText for further building.
     */
    fun action(a: ClickEvent.Action) : ClickableText {
        this.action = a
        return this
    }

    /**
     * Chainable. Sets a ChatColor to prepend to the text value when the TextComponent is displayed to the user.
     * @param c The Bungee ChatColor to prepend to the text when the TextComponent is created
     * @return The instance of ClickableText for further building.
     */
    fun color(c: ChatColor) : ClickableText {
        this.prependColor = c
        return this
    }

    /**
     * Chainable. Sets a String to the text value of the object to be displayed when the TextComponent is served to the
     * user. Use `ClickableText#color` to prepend a color for the TextComponent.
     * @param s The String to serve to the Player when the TextComponent is displayed.
     * @return The instance of ClickableText for further building
     */
    fun text(s: String) : ClickableText {
        this.text = s
        return this
    }

    /**
     * Chainable. Sets a String to the data value of the object which will be acted on by the `ClickEvent.Action` when
     * the user clicks on the TextComponent. Can be a URL to be copied, a command to be executed, etc.
     * @param s The String data value to attach to the action of the TextComponent.
     * @return The instance of ClickableText for further building.
     */
    fun value(s: String) : ClickableText {
        this.value = s
        return this
    }

    /**
     * Chainable. Adds hover text which, if present, overrides the default hover text which is derived from the type of
     * action attached to the ClickableText.
     * @param l The String values to add to the hover text to be displayed on the TextComponent. Supports `&` color.
     * @return The instance of ClickableText for further building.
     */
    fun hoverText(l: List<String>) : ClickableText {
        this.hoverText.addAll(l.translateColor())
        return this
    }

    /**
     * Chainable. Adds hover text which, if present, overrides the default hover text which is derived from the type of
     * action attached to the ClickableText.
     * @param s The String values to add to the hover text to be displayed on the TextComponent. Supports `&` color.
     * @return The instance of ClickableText for further building.
     */
    fun hoverText(vararg s: String) : ClickableText {
        this.hoverText.addAll(s.map { it.translateColor() })
        return this
    }

    /**
     * Chainable. Clears the existing hover text and adds the new text, effectively setting the contents of what is an
     * immutable non-null type. If custom hover text is present, it overrides the default hover text which is
     * derived from the type of action attached to the ClickableText.
     * @param l The new String values to appear when hovering over the TextComponent. Supports `&` color.
     * @return The instance of ClickableText for further building.
     */
    fun setHoverText(l: List<String>) : ClickableText {
        this.hoverText.clear()
        this.hoverText.addAll(l.translateColor())
        return this
    }

    /**
     * Chainable. Clears the existing hover text. An empty hover text list will be overridden by the default hover text,
     * derived from the action type of the ClickableText.
     * @return The instance of ClickableText for further building.
     */
    fun resetHoverText() : ClickableText {
        this.hoverText.clear()
        return this
    }

    /**
     * Builds the completed ClickableText into a TextComponent. This TextComponent can be safely inserted into another
     * TextComponent, a ComponentBuilder, or sent by itself.
     * @return The constructed TextComponent.
     */
    fun toComponent() : TextComponent {
        val text = TextComponent(text)
        text.clickEvent = ClickEvent(action, value)

        val hoverEvent: HoverEvent? = if (hoverText.isNotEmpty())
            HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText.map { Text(it) })
        else
            text.clickEvent.getDynamicHoverEvent()

        if (prependColor != null)
            text.color = prependColor
        if (hoverEvent != null)
            text.hoverEvent = hoverEvent

        return text
    }

    /**
     * Invokes `ClickableText#toComponent()` and sends the constructed TextComponent to the supplied CommandSender.
     * @param to Who to send the constructed TextComponent to.
     */
    fun send(to: CommandSender) {
        to.spigot().sendMessage(toComponent())
    }
}