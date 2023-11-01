package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.enum.ChatColorKey
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.name
import io.github.tsgrissom.pluginapi.extension.sendColored
import io.github.tsgrissom.pluginapi.extension.translateAndStripColorCodes
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class RenameItemCommand : CommandBase() {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()

    companion object {
        const val PERM = "essentialskt.renameitem"
        const val PERM_BYPASS_LENGTH_LIMIT = "essentialskt.renameitem.bypasslimit"
    }

    override fun execute(context: CommandContext) {
        val conf = getConfig()
        val ccErr = conf.getChatColor(ChatColorKey.Error)
        val ccErrDetl = conf.getChatColor(ChatColorKey.ErrorDetail)
        val ccPrim = conf.getChatColor(ChatColorKey.Primary)
        val ccReset = ChatColor.RESET

        val label = context.label
        val sender = context.sender

        if (sender is ConsoleCommandSender)
            return sender.sendMessage("${ccErr}Console cannot rename items")
        if (sender !is Player)
            return

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        val arguments = context.getExecutedString(withLabel=false).trim()

        if (arguments.isEmpty())
            return sender.sendMessage("${ccErr}Usage: ${ccErrDetl}/${label} <NewName>")

        val stripped = arguments.translateAndStripColorCodes()

        if (stripped.length > 32 && sender.lacksPermission(PERM_BYPASS_LENGTH_LIMIT)) {
            val text = "${ccErr}Item display names must be less than or equal to ${ccErrDetl}32 characters${ccErr}, excluding color codes"
            return sender.sendMessage(text)
        }

        val p: Player = sender
        val pi = p.inventory
        val inHand = pi.itemInMainHand

        if (inHand.type == Material.AIR)
            return sender.sendMessage("${ccErr}There is nothing in your hand to rename")

        val iS = ItemStack(inHand)
            .name(arguments)
        pi.setItemInMainHand(iS)

        sender.sendColored("${ccPrim}Item name updated to ${ccReset}${arguments}") // sendColored is used to translate arguments
        // TODO Use sendMessage and translate input's color codes only if they are actually getting a colored name
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : MutableList<String> = mutableListOf()
}