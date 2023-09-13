package io.github.tsgrissom.essentialskt.command

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.pluginapi.command.CommandBase
import io.github.tsgrissom.pluginapi.command.CommandContext
import io.github.tsgrissom.pluginapi.extension.*
import org.bukkit.Bukkit
import org.bukkit.WorldType
import org.bukkit.attribute.Attribute
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class WhoIsCommand : CommandBase() {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getAfkManager() = getPlugin().afkManager

    private val permSelf = "essentials.whoami"
    private val permOthers = "essentials.whois"

    private fun whoAmI(context: CommandContext) {
        val sender = context.sender

        if (sender.lacksPermission(permSelf))
            return context.sendNoPermission(sender, permSelf)

        if (sender is ConsoleCommandSender)
            return sender.sendColored("&4Console Usage: &c/whois <Target>")

        if (sender !is Player)
            return

        val p: Player = sender

        displayWhoIs(p, p)
    }

    private fun whoIs(context: CommandContext) {
        val args = context.args
        val sender = context.sender

        if (args.isEmpty())
            return sender.sendColored("&4Usage: &c/whois <Target>")

        if (sender.lacksPermission(permOthers))
            return context.sendNoPermission(sender, permOthers)

        val sub = args[0]
        val t = Bukkit.getPlayer(sub)
            ?: return sender.sendColored("&4Could not find player &c\"$sub\"")

        displayWhoIs(sender, t)
    }

    override fun execute(context: CommandContext) {
        val label = context.label
        val sender = context.sender

        when (label) {
            "whoami", "ewhoami" -> whoAmI(context)
            "whois", "ewhois" -> whoIs(context)
            else -> sender.sendColored("&4Unknown /whois subcommand (when hit else)")
        }
    }

    private fun displayWhoIsTemporary(sender: CommandSender, target: Player) {
        // TODO Display temporary attributes
    }

    private fun displayWhoIsPermanent(sender: CommandSender, target: Player) {
        // TODO Display permanent attributes
    }

    private fun displayWhoIs(sender: CommandSender, target: Player) {
        val x = target.location.x.roundToDigits(2)
        val y = target.location.y.roundToDigits(2)
        val z = target.location.z.roundToDigits(2)

        val worldType: WorldType? = target.world.worldType
        var worldLine = " &8- &7World: &e${target.world.name}"

        if (worldType != null)
            worldLine += " &8+ &7World Type: &b${worldType.name.capitalizeAllCaps()}"

        val isAfk = getAfkManager().isAfk(target)

        var fireLine = " &8- &7On Fire: "

        fireLine += if (target.fireTicks > 0)
            "${true.palatable(withColor=true)} &8+ &7Ticks Left: &e${target.fireTicks}"
        else
            false.palatable(withColor=true)

        val hunger = 20 - target.foodLevel

        // TODO essentials.whois.ip

        sender.sendColored(
            "&6/whois for &e${target.name}",
            " &8- &7Username: &e${target.name} &8+ &7Display Name: &r${target.displayName}",
            " &8- &7Unique ID: &e${target.getUniqueString()}",
            " &8- &7IP Address: &e${target.getIPString()}",
            " &8- &7Gamemode: &b${target.gameMode.name.capitalizeAllCaps()}",
            worldLine,
            " &8- &7Location &cX&aY&bZ&7: &c${x} &a${y} &b${z}",
            " &8- &7Is AFK: &e${isAfk.palatable(withColor=true)}",
            " &8- &7Is OP: &e${target.isOp.palatable(withColor=true)}",
            " &8- &7Health: &e${target.health} &8/ &7Max: &e${target.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value}",
            " &8- &7Food Level: &e${target.foodLevel}&8/&e20 &8+ &7Hunger: &e$hunger",
            " &8- &7Oxygen: &e${target.remainingAir}&8/&e300",
            fireLine,
            " &8- &7Flying Speed: &e${target.flySpeed} &8+ &7Walking Speed: &e${target.walkSpeed}",
            " &8- &7Can Fly: &e${target.allowFlight.palatable(withColor=true)} &8+ &7Is Flying: &e${target.isFlying.palatable(withColor=true)}",
            " &8- &7Is Sneaking: &e${target.isSneaking.palatable(withColor=true)} &8+ &7Is Sprinting: &e${target.isSprinting.palatable(withColor=true)}",
            " &8- &7Level: &e${target.level} &8+ &7Exp: &e${target.exp} &8+ &7Total Exp: &e${target.totalExperience}"
        )
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        val tab = mutableListOf<String>()

        if (label.equalsIc("whoami"))
            return tab
        else {
            if (args.size == 1 && sender.hasPermission(permOthers))
                StringUtil.copyPartialMatches(args[0], getSortedOnlinePlayerNames(), tab)
        }

        return tab.sorted().toMutableList()
    }
}