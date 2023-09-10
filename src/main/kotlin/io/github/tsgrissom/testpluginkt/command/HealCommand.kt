package io.github.tsgrissom.testpluginkt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendColored
import io.github.tsgrissom.testpluginkt.TestPluginKT
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class HealCommand : CommandBase() {

    private fun shouldRestoreFoodLevel() : Boolean {
        val plugin = TestPluginKT.instance ?: return true
        val config = plugin.config ?: return true
        return config.getBoolean("Commands.HealRestoresFoodLevel", true)
    }

    private fun getActionString() : String = if (shouldRestoreFoodLevel()) "health and hunger" else "health"

    private fun handleEmptyArgs(context: CommandContext) {
        val label = context.label
        val sender = context.sender

        if (sender is ConsoleCommandSender)
            return sender.sendColored("&4Console Usage: &c/$label <Target>")
        if (sender !is Player)
            return

        if (sender.lacksPermission("essentials.command.heal"))
            return sender.sendColored("&4You do not have permission to do that")

        restoreHealth(sender, sender)
        // TODO Heal sender
    }

    private fun handleArgs(context: CommandContext) {
        val args = context.args
        val sender = context.sender
        val sub = args[0]

        val target: Player = Bukkit.getPlayer(sub)
            ?: return sender.sendColored("&4Could not find player &c$sub")

        if (target == sender && sender.lacksPermission("essentials.command.heal"))
            return sender.sendColored("&4You do not have permission to do that")
        if (target != sender && sender.lacksPermission("essentials.command.heal.other"))
            return sender.sendColored("&4You do not have permission to do that")

        restoreHealth(sender, target)
    }

    override fun execute(context: CommandContext) {
        if (context.args.isEmpty())
            handleEmptyArgs(context)
        else
            handleArgs(context)
    }

    fun restoreHealth(sender: CommandSender, target: Player) {
        val shouldSate = shouldRestoreFoodLevel()
        val maxHealth = target.getAttribute(Attribute.GENERIC_MAX_HEALTH).value

        target.health = maxHealth
        if (shouldSate)
            target.foodLevel = 20

        val postfix = getActionString()

        if (sender == target)
            sender.sendColored("&6You restored your own $postfix")
        else
            sender.sendColored("&6You restored &c${target.name}'s &6$postfix")
    }
}