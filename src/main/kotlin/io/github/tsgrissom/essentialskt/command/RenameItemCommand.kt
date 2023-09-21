package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.name
import io.github.tsgrissom.pluginapi.extension.sendColored
import io.github.tsgrissom.pluginapi.extension.translateAndStripColorCodes
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class RenameItemCommand : CommandBase() {

    companion object {
        const val PERM = "essentialskt.renameitem"
        const val PERM_BYPASS_LENGTH_LIMIT = "essentialskt.renameitem.bypasslimit"
    }

    override fun execute(context: CommandContext) {
        val label = context.label
        val sender = context.sender

        if (sender is ConsoleCommandSender)
            return sender.sendColored("&4Console cannot rename items")
        if (sender !is Player)
            return

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        val arguments = context.getExecutedString(withLabel=false).trim()

        if (arguments.isEmpty())
            return sender.sendColored("&4Usage: &c/${label} <NewName>")

        val stripped = arguments.translateAndStripColorCodes()

        if (stripped.length > 32 && sender.lacksPermission(PERM_BYPASS_LENGTH_LIMIT))
            return sender.sendColored("&4Item display names must be less than or equal to &c32 characters&4, excluding color codes")

        val p: Player = sender
        val pi = p.inventory
        val inHand = pi.itemInMainHand

        if (inHand.type == Material.AIR)
            return sender.sendColored("&4There is nothing in your hand to rename")

        val iS = ItemStack(inHand)
            .name(arguments)
        pi.setItemInMainHand(iS)

        sender.sendColored("&6Item name updated to &r${arguments}")
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : MutableList<String> = mutableListOf()
}