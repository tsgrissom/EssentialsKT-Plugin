package io.github.tsgrissom.pluginapi.conversation

import io.github.tsgrissom.pluginapi.extension.kt.equalsIc
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.ValidatingPrompt
import org.bukkit.entity.Player

class WorldNamePrompt(private val nextPrompt: Prompt?) : ValidatingPrompt() {

    constructor() : this(null)

    override fun getPromptText(context: ConversationContext): String {
        return "${ChatColor.GOLD}Which world are you targeting? ${ChatColor.YELLOW}Current ${ChatColor.GOLD}for the current${ChatColor.DARK_GRAY}: "
    }

    override fun isInputValid(context: ConversationContext, input: String): Boolean {
        return Bukkit.getWorld(input) != null
    }

    override fun acceptValidatedInput(context: ConversationContext, input: String): Prompt? {
        if (input.equalsIc("none", "exit"))
            return END_OF_CONVERSATION

        if (context.forWhom is Player)
            (context.forWhom as Player).sendMessage("${ChatColor.GREEN}The world you entered is valid")

        context.setSessionData("world", input)

        return nextPrompt
    }
}