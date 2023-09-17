package io.github.tsgrissom.essentialskt.misc

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.command.GamemodeCommand
import io.github.tsgrissom.pluginapi.extension.capitalizeAllCaps
import io.github.tsgrissom.pluginapi.extension.getIPString
import io.github.tsgrissom.pluginapi.extension.palatable
import io.github.tsgrissom.pluginapi.extension.roundToDigits
import io.github.tsgrissom.pluginapi.misc.ClickableText
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import java.util.*

class EssPlayer(private val uuid: UUID) {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")

    private val player: Player = Bukkit.getPlayer(uuid)
        ?: error("Player for UUID \"$uuid\" is offline!")

    constructor(player: Player) : this(player.uniqueId)
    constructor(username: String) : this(Bukkit.getPlayer(username)
        ?: error("Player for username \"$username\" is offline!"))

    fun getUniqueString() : String = uuid.toString()
    fun getIPString() : String = player.getIPString()
    fun getNickname() : String = player.displayName
    fun isAfk() : Boolean = getPlugin().afkManager.isAfk(player)

    fun getAvailableGameModes() : Set<GameMode> {
        val set = mutableSetOf<GameMode>()

        if (player.hasPermission(GamemodeCommand.PERM_ADVENTURE))
            set.add(GameMode.ADVENTURE)
        if (player.hasPermission(GamemodeCommand.PERM_CREATIVE))
            set.add(GameMode.CREATIVE)
        if (player.hasPermission(GamemodeCommand.PERM_SPECTATOR))
            set.add(GameMode.SPECTATOR)
        if (player.hasPermission(GamemodeCommand.PERM_SURVIVAL))
            set.add(GameMode.SURVIVAL)

        return set
    }

    fun generateTemporaryAttributesList(
        isAfk: Boolean,
        withHeader: Boolean = true,
        linePrefix: String = " - ",
        excludeGamemode: Boolean = false,
        excludeCoordinates: Boolean = false,
        excludeWorld: Boolean = false,
        excludeAfk: Boolean = false,
        excludeFireTicks: Boolean = false,
        excludeHealth: Boolean = false,
        excludeHunger: Boolean = false,
        excludeOxygen: Boolean = false,
        excludeSpeed: Boolean = false,
        excludeFlight: Boolean = false,
        excludeSneaking: Boolean = false,
        excludeSprinting: Boolean = false,
        excludeExperience: Boolean = false
    ) : Array<BaseComponent> {
        val builder = ComponentBuilder()

        fun hasPrefix() : Boolean = linePrefix.isNotEmpty()
        fun appendPrefix() {
            if (hasPrefix())
                builder.append(linePrefix).color(ChatColor.DARK_GRAY)
        }

        if (withHeader)
            builder
                .append("/whois for ").color(ChatColor.GOLD)
                .append(player.name).color(ChatColor.YELLOW)
                .append(" : ").color(ChatColor.DARK_GRAY)
                .append("Temporary").color(ChatColor.GRAY)
                .append("\n")

        if (!excludeGamemode) {
            val gm = player.gameMode.name.capitalizeAllCaps()
            appendPrefix()
            builder
                .append("Gamemode: ").color(ChatColor.GRAY)
                .append(gm).color(ChatColor.AQUA)
                .append("\n")
        }

        if (!excludeWorld) {
            appendPrefix()
            builder
                .append("World: ").color(ChatColor.GRAY)
                .append(player.name).color(ChatColor.YELLOW)
                .append("\n")
        }

        if (!excludeCoordinates) {
            val loc = player.location
            val x = loc.x.roundToDigits(2)
            val y = loc.y.roundToDigits(2)
            val z = loc.z.roundToDigits(2)

            appendPrefix()
            builder
                .append("Location ").color(ChatColor.GRAY)
                .append("X").color(ChatColor.RED)
                .append("Y").color(ChatColor.GREEN)
                .append("Z").color(ChatColor.AQUA)
                .append(": ").color(ChatColor.GRAY)
                .append("$x ").color(ChatColor.RED)
                .append("$y ").color(ChatColor.GREEN)
                .append("$z").color(ChatColor.AQUA)
                .append("\n")
        }

        if (!excludeAfk) {
            appendPrefix()
            builder
                .append("Is AFK: ").color(ChatColor.GRAY)
                .append(isAfk.palatable(withColor=true)).color(ChatColor.YELLOW)
        }

        if (!excludeHealth) {
            val health = player.health
            val attrMaxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
            val maxHealth = attrMaxHealth?.value ?: 20.0

            appendPrefix()
            builder
                .append("Health: ").color(ChatColor.GRAY)
                .append("$health").color(ChatColor.YELLOW)
                .append(" / ").color(ChatColor.DARK_GRAY)
                .append("Max: ").color(ChatColor.GRAY)
                .append("$maxHealth").color(ChatColor.YELLOW)
                .append("\n")
        }

        if (!excludeHunger) {
            val foodLevel = player.foodLevel
            val hunger = 20 - foodLevel

            appendPrefix()
            builder
                .append("Food Level: ").color(ChatColor.GRAY)
                .append("$foodLevel").color(ChatColor.YELLOW)
                .append("/").color(ChatColor.DARK_GRAY)
                .append("20").color(ChatColor.YELLOW)
                .append(" + ").color(ChatColor.DARK_GRAY)
                .append("Hunger: ").color(ChatColor.GRAY)
                .append("$hunger").color(ChatColor.YELLOW)
                .append("\n")
        }

        if (!excludeOxygen) {
            appendPrefix()
            builder
                .append("Oxygen: ").color(ChatColor.GRAY)
                .append("${player.remainingAir}").color(ChatColor.YELLOW)
                .append("/").color(ChatColor.DARK_GRAY)
                .append("300").color(ChatColor.YELLOW)
                .append("\n")
        }

        if (!excludeFireTicks) {
            val ticks = player.fireTicks

            appendPrefix()
            builder.append("On Fire: ").color(ChatColor.GRAY)
            if (ticks > 0) {
                builder
                    .append("Yes").color(ChatColor.GREEN)
                    .append(" + ").color(ChatColor.DARK_GRAY)
                    .append("Ticks Left: ").color(ChatColor.GRAY)
                    .append("$ticks").color(ChatColor.YELLOW)
            } else {
                builder
                    .append("No").color(ChatColor.RED)
            }
            builder.append("\n")
        }

        if (!excludeSpeed) {
            val fly = "${player.flySpeed}"
            val walk = "${player.walkSpeed}"

            appendPrefix()
            builder
                .append("Flying Speed: ").color(ChatColor.GRAY)
                .append(fly).color(ChatColor.YELLOW)
                .append(" + ").color(ChatColor.DARK_GRAY)
                .append("Walking Speed: ").color(ChatColor.GRAY)
                .append(walk).color(ChatColor.YELLOW)
                .append("\n")
        }

        if (!excludeFlight) {
            val canFly = player.allowFlight.palatable(withColor=true)
            val isFlying = player.isFlying.palatable(withColor=true)

            appendPrefix()
            builder
                .append("Can Fly: ").color(ChatColor.GRAY)
                .append(canFly)
                .append(" + ").color(ChatColor.DARK_GRAY)
                .append("Is Flying: ").color(ChatColor.GRAY)
                .append(isFlying)
                .append("\n")
        }

        if (!excludeSneaking || !excludeSprinting) {
            val isSneaking = player.isSneaking.palatable(withColor=true)
            val isSprinting = player.isSprinting.palatable(withColor=true)

            appendPrefix()

            if (!excludeSneaking) {
                builder
                    .append("Is Sneaking: ").color(ChatColor.GRAY)
                    .append(isSneaking).color(ChatColor.YELLOW)
            }

            if (!excludeSneaking && !excludeSprinting)
                builder.append(" + ").color(ChatColor.DARK_GRAY)

            if (!excludeSprinting) {
                builder
                    .append("Is Sprinting: ").color(ChatColor.GRAY)
                    .append(isSprinting).color(ChatColor.YELLOW)
            }

            builder.append("\n")
        }

        if (!excludeExperience) {
            appendPrefix()
            builder
                .append("Level: ").color(ChatColor.GRAY)
                .append("${player.level}").color(ChatColor.YELLOW)
                .append(" + ").color(ChatColor.DARK_GRAY)
                .append("Exp: ").color(ChatColor.GRAY)
                .append("${player.exp}").color(ChatColor.YELLOW)
                .append(" + ").color(ChatColor.DARK_GRAY)
                .append("Total Exp: ").color(ChatColor.GRAY)
                .append("${player.totalExperience}").color(ChatColor.YELLOW)
        }

        return builder.create()
    }

    fun generatePermanentAttributesList(
        withHeader: Boolean = true,
        linePrefix: String = " - ",
        excludeNames: Boolean = false,
        excludeUniqueId: Boolean = false,
        excludeIp: Boolean = false,
        excludeOperator: Boolean = false
    ) : Array<BaseComponent> {
        val builder = ComponentBuilder()

        fun hasPrefix() : Boolean = linePrefix.isNotEmpty()
        fun appendPrefix() {
            if (hasPrefix())
                builder.append(linePrefix).color(ChatColor.DARK_GRAY)
        }

        if (withHeader)
            builder
                .append("/whois for ").color(ChatColor.GOLD)
                .append(player.name).color(ChatColor.YELLOW)
                .append("\n")

        if (!excludeNames) {
            val dn = player.displayName
            val hasNoNickname = dn == player.name
            val nickname = if (hasNoNickname)
                "None"
            else
                dn
            val nicknameColor = if (hasNoNickname)
                ChatColor.RED
            else
                ChatColor.YELLOW

            appendPrefix()
            builder
                .append("Username: ").color(ChatColor.GRAY)
                .append(player.name).color(ChatColor.YELLOW)
                .append(" + ").color(ChatColor.DARK_GRAY)
                .append("Display Name: ").color(ChatColor.GRAY)
                .append(nickname).color(nicknameColor)
                .append("\n")
        }

        if (!excludeUniqueId) {
            val uuid = player.uniqueId.toString()
            val data = ClickableText
                .compose("&e$uuid")
                .hoverText("&7Click to copy UUID")
                .action(ClickEvent.Action.COPY_TO_CLIPBOARD)
                .value(uuid)
                .toTextComponent()

            appendPrefix()
            builder
                .append("Unique ID: ").color(ChatColor.GRAY)
                .append(data).append("\n").reset()
        }

        if (!excludeIp) {
            val ip = this.getIPString()
            val data = ClickableText
                .compose("&e$ip")
                .hoverText("&7Click to copy IP address")
                .action(ClickEvent.Action.COPY_TO_CLIPBOARD)
                .value(ip)
                .toTextComponent()

            appendPrefix()
            builder
                .append("IP Address: ").color(ChatColor.GRAY)
                .append(data).append("\n").reset()
        }

        if (!excludeOperator) {
            val isOp = player.isOp.palatable(withColor=true)
            appendPrefix()
            builder
                .append("Is Op: ").color(ChatColor.GRAY)
                .append(isOp).color(ChatColor.YELLOW)
        }

        return builder.create()
    }
}