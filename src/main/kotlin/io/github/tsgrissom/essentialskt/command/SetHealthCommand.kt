package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.misc.EssPlayer
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.isPercentage
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendColored
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class SetHealthCommand : CommandBase() {

    companion object {
        const val PERM = "essentialskt.sethealth"
        const val PERM_MAX = "essentialskt.sethealth.max"
        const val PERM_PERCENT = "essentialskt.sethealth.percent"
    }

    private val flagMax = Pair("max", "m")

    override fun execute(context: CommandContext) {
        val args = context.args
        val len = args.size
        val sender = context.sender

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        if (len == 0)
            return sender.sendColored("&4Usage: &c/sethealth <Target> <AmountAsDouble>")

        val sub = args[0]
        val t: Player = Bukkit.getPlayer(sub)
            ?: return sender.sendColored("&4Could not find player &c\"$sub\"")

        if (len == 1)
            return sender.sendColored("&4Please provide an amount of health to set to")

        val arg1 = args[1]

        if (arg1.isPercentage()) {
            return handlePercentageInput(context, t, arg1)
        }

        val arg1d = arg1.toDoubleOrNull()
            ?: return sender.sendColored("&4Invalid decimal value for &c\"$arg1\"")

        if (arg1d <= 0)
            return sender.sendColored("&4New health value must be a nonzero positive number")

        if (context.hasFlag(flagMax)) {
            return handleChangeMaxHealth(context, t, arg1d)
        }

        val max = EssPlayer(t).getMaxHealth()

        if (arg1d > max)
            return sender.sendColored("&c$arg1d &4would exceed &c${t.name}'s &4max health of &c$max")

        t.health = arg1d
        sender.sendColored("&6You set &c${t.name}'s &6health to &c$arg1")
    }

    private fun handleChangeMaxHealth(context: CommandContext, t: Player, maxHealth: Double) {
        val sender = context.sender

        if (sender.lacksPermission(PERM_MAX))
            return context.sendNoPermission(sender, PERM_MAX)

        t.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = maxHealth
        t.health = maxHealth
        sender.sendColored("&6You set &c${t.name}'s &6maximum health to &c$maxHealth")
    }

    private fun handlePercentageInput(context: CommandContext, t: Player, input: String) {
        val sender = context.sender

        if (sender.lacksPermission(PERM_PERCENT))
            return context.sendNoPermission(sender, PERM_PERCENT)

        val sansPercent = input.removeSuffix("%")
        val value = sansPercent.toDoubleOrNull()
            ?: return sender.sendColored("&c\"$input\" is not a valid percentage value")

        if (value <= 0)
            return sender.sendColored("&4A percentage of max health must be a positive nonzero number")

        val chunk = value / 100
        val attr = t.getAttribute(Attribute.GENERIC_MAX_HEALTH)
        val max = attr?.value ?: 20.0
        val amount = chunk * max

        t.health = amount
        sender.sendColored("&6You set &c${t.name}'s &6health to &c${input} &6of their max health")
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        val tab = mutableListOf<String>()

        if (sender.hasPermission(PERM)) {
            val len = args.size

            if (len == 1) {
                StringUtil.copyPartialMatches(args[0], getOnlinePlayerNamesToMutableList(), tab)
            } else if (len > 2) {
                StringUtil.copyPartialMatches(args[2], listOf("--max", "-m"), tab)
            }
        }

        return tab.sorted().toMutableList()
    }
}