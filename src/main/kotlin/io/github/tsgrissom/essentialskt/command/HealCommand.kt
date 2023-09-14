package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendColored
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class HealCommand : CommandBase() {

    private val permSelf = "essentials.heal"
    private val permOthers = "essentials.heal.others"

    private fun shouldRestoreFoodLevel() : Boolean {
        val plugin = EssentialsKTPlugin.instance ?: return true
        val config = plugin.config
        return config.getBoolean("Commands.HealRestoresFoodLevel", true)
    }

    private fun getActionString() : String =
        if (shouldRestoreFoodLevel()) "health and hunger" else "health"

    private fun handleEmptyArgs(context: CommandContext) {
        val label = context.label
        val sender = context.sender

        if (sender is ConsoleCommandSender)
            return sender.sendColored("&4Console Usage: &c/$label <Target>")
        if (sender !is Player)
            return

        if (sender.lacksPermission(permSelf))
            return context.sendNoPermission(sender, permSelf)

        restoreHealth(sender, sender)
        // TODO Heal sender
    }

    private fun handleArgs(context: CommandContext) {
        val args = context.args
        val sender = context.sender
        val sub = args[0]

        val target: Player = Bukkit.getPlayer(sub)
            ?: return sender.sendColored("&4Could not find player &c$sub")

        if (target == sender && sender.lacksPermission(permSelf))
            return context.sendNoPermission(sender, permSelf)
        if (target != sender && sender.lacksPermission(permOthers))
            return context.sendNoPermission(sender, permOthers)

        restoreHealth(sender, target)
    }

    override fun execute(context: CommandContext) {
        if (context.args.isEmpty())
            handleEmptyArgs(context)
        else
            handleArgs(context)
    }

    private fun restoreHealth(sender: CommandSender, target: Player) {
        val shouldSate = shouldRestoreFoodLevel()
        val maxHealth = target.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value!!

        target.health = maxHealth
        if (shouldSate)
            target.foodLevel = 20

        val postfix = getActionString()

        if (sender == target)
            sender.sendColored("&6You restored your own $postfix")
        else
            sender.sendColored("&6You restored &c${target.name}'s &6$postfix")
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        val tab = mutableListOf<String>()
        val len = args.size

        if (len > 0) {
            if (len == 1 && sender.hasPermission(permOthers))
                StringUtil.copyPartialMatches(args[0], getOnlinePlayerNamesToMutableList(), tab)
        }

        return tab.sorted().toMutableList()
    }
}