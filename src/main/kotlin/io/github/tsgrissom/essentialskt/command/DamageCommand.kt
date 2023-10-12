package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.*
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.ChatColor.DARK_GRAY as D_GRAY
import net.md_5.bungee.api.ChatColor.DARK_RED as D_RED
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class DamageCommand : CommandBase() {

    companion object {
        const val PERM = "essentialskt.damage"
        const val PERM_PERCENT = "essentialskt.damage.percent"
    }

    private fun sendUsage(context: CommandContext) {
        val label = context.label
        val sender = context.sender
        var usage = "${D_RED}Usage: ${RED}/$label <Target> <AmountAsDecimal"

        usage += if (sender.hasPermission(PERM_PERCENT)) {
            "OrPercentage>"
        } else {
            ">"
        }

        sender.sendMessage(usage)
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
            ?: return sender.sendMessage("${D_RED}Could not find player ${RED}\"$sub\"")

        if (args.size == 1) {
            if (sub.equalsIc("help", "h", "?", "usage"))
                return sendUsage(context)

            return sender.sendMessage("${D_RED}You must specify an amount of damage")
        }

        val arg1 = args[1]

        if (arg1.isPercentage()) {
            return handlePercentageInput(context, t, arg1)
        }

        val amount: Double

        try {
            amount = arg1.toDouble()

            if (amount <= 0)
                return sender.sendMessage("${D_RED}Your damage amount must be a positive nonzero number")
        } catch (ignored: NumberFormatException) {
            return sender.sendMessage("${D_RED}Your damage amount must be a decimal or integer value")
        }

        t.damage(amount)
        sender.sendMessage("${GOLD}Damaged ${RED}${t.name} ${GOLD}for ${RED}${amount} hearts")
    }

    private fun handlePercentageInput(context: CommandContext, target: Player, input: String) {
        val sender = context.sender

        if (sender.lacksPermission(PERM_PERCENT))
            return context.sendNoPermission(sender, PERM_PERCENT)

        val sansPercent = input.removeSuffix("%")
        val percent: Double

        try {
            percent = sansPercent.toDouble()

            if (percent <= 0)
                return sender.sendMessage("${D_RED}A percent of max health to damage must be a positive nonzero number")
        } catch (ignored: NumberFormatException) {
            val text = "${D_RED}Your damage percentage must be a decimal or integer value followed by a percent symbol"
            return sender.sendMessage(text)
        }

        val chunk = percent / 100
        val attr = target.getAttribute(Attribute.GENERIC_MAX_HEALTH)
        val maxHealth = attr?.value ?: 20.0
        val amount = chunk * maxHealth

        target.damage(amount)
        sender.sendMessage("${GOLD}You damaged ${RED}${target.name} ${GOLD}for ${RED}${percent}% ${GOLD}of their max health ${D_GRAY}(${RED}${amount.roundToDigits(2)}${D_GRAY})")
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
}