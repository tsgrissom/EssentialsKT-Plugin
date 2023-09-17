package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.equalsIc
import io.github.tsgrissom.pluginapi.extension.lacksPermission
import io.github.tsgrissom.pluginapi.extension.roundToDigits
import io.github.tsgrissom.pluginapi.extension.sendColored
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class DamageCommand : CommandBase() {

    companion object {
        const val PERM = "essentials.damage"
        const val PERM_PERCENT = "essentials.damage.percent"
    }

    private fun sendUsage(context: CommandContext) {
        val label = context.label
        val sender = context.sender
        var usage = "&4Usage: &c/$label <Target> <AmountAsDecimal"

        usage += if (sender.hasPermission(PERM_PERCENT)) {
            "OrPercentage>"
        } else {
            ">"
        }

        sender.sendColored(usage)
    }

    override fun execute(context: CommandContext) {
        val args = context.args
        val sender = context.sender

        if (sender.lacksPermission(PERM))
            return context.sendNoPermission(sender, PERM)

        if (args.isEmpty())
            return sendUsage(context)

        val sub = args[0]
        val t: Player = Bukkit.getPlayer(sub)
            ?: return sender.sendColored("&4Could not find player &c\"$sub\"")

        if (args.size == 1) {
            if (sub.equalsIc("help", "h", "?", "usage"))
                return sendUsage(context)

            return sender.sendColored("&4You must specify an amount of damage")
        }

        val arg1 = args[1]

        if (isPercentage(arg1)) {
            return handlePercentage(context, t, arg1)
        }

        val amount: Double

        try {
            amount = arg1.toDouble()

            if (amount <= 0)
                return sender.sendColored("&4Your damage amount must be a positive nonzero number")
        } catch (ignored: NumberFormatException) {
            return sender.sendColored("&4Your damage amount must be a decimal or integer value")
        }

        t.damage(amount)
        sender.sendColored("&6Damaged &c${t.name} &6for &c${amount} hearts")
    }

    private fun handlePercentage(context: CommandContext, target: Player, input: String) {
        val sender = context.sender

        if (sender.lacksPermission(PERM_PERCENT))
            return context.sendNoPermission(sender, PERM_PERCENT)

        val sansPercent = input.removeSuffix("%")
        val percent: Double

        try {
            percent = sansPercent.toDouble()

            if (percent <= 0)
                return sender.sendColored("&4A percent of max health to damage must be a positive nonzero number")
        } catch (ignored: NumberFormatException) {
            val text = "&4Your damage percentage must be a decimal or integer value followed by a percent symbol"
            return sender.sendColored(text)
        }

        val chunk = percent / 100
        val attr = target.getAttribute(Attribute.GENERIC_MAX_HEALTH)
        val maxHealth = attr?.value ?: 20.0
        val amount = chunk * maxHealth

        target.damage(amount)
        sender.sendColored("&6You damaged &c${target.name} &6for &c${percent}% &6of their max health &8(&c${amount.roundToDigits(2)}&8)")
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ) : MutableList<String> {

        val tab =  mutableListOf<String>()
        val len = args.size

        if (len > 0) {
            val sub = args[0]

            if (len == 1) {
                StringUtil.copyPartialMatches(sub, getOnlinePlayerNamesToMutableList(), tab)
            }
        }

        return tab.sorted().toMutableList()
    }

    private fun isPercentage(s: String) : Boolean {
        val percentagePattern = """^\d+(\.\d+)?%$""".toRegex()
        return percentagePattern.matches(s)
    }
}