package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.isPercentage
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.sendColored
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import kotlin.math.roundToInt

class SetFoodLevelCommand : CommandBase() {

    companion object {
        const val PERM = "essentialskt.setfoodlevel"
        const val PERM_PERCENT = "essentialskt.setfoodlevel.percent"
    }

    override fun execute(context: CommandContext) {
        val args = context.args
        val label = context.label
        val len = args.size
        val sender = context.sender

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        if (len == 0)
            return sender.sendColored("&4Usage: &c/$label <Target> <AmountAsDouble>")

        val sub = args[0]
        val t: Player = Bukkit.getPlayer(sub)
            ?: return sender.sendColored("&4Could not find player &c\"$sub\"")

        if (len == 1)
            return sender.sendColored("&4Please provide an amount to set food level to")

        val arg1 = args[1]

        if (arg1.isPercentage()) {
            return handlePercentageInput(context, t, arg1)
        }

        var arg1d = arg1.toIntOrNull()
            ?: return sender.sendColored("&4Invalid integer value for &c\"$arg1\"")

        if (arg1d < 0)
            return sender.sendColored("&4New food level must be a positive number less than 20")

        if (arg1d > 20)
            arg1d = 20

        t.foodLevel = arg1d
        sender.sendColored("&6You set &c${t.name}'s &6food level to &c$arg1")
    }

    private fun handlePercentageInput(context: CommandContext, t: Player, input: String) {
        val sender = context.sender

        if (sender.lacksPermission(PERM_PERCENT))
            return context.sendNoPermission(sender, PERM_PERCENT)

        val sansPercent = input.removeSuffix("%")
        val value = sansPercent.toIntOrNull()
            ?: return sender.sendColored("&c\"$input\" is not a valid percentage value")

        if (value <= 0)
            return sender.sendColored("&4A percentage of max food level must be a positive nonzero integer")

        val chunk = value / 100.0
        val amount = chunk * 20

        t.foodLevel = amount.roundToInt()
        sender.sendColored("&6You set &c${t.name}'s &6food level to &c${input}")
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
            }
        }

        return tab.sorted().toMutableList()
    }
}