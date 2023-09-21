package io.github.tsgrissom.essentialskt.misc

import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.command.GameModeCommand
import io.github.tsgrissom.pluginapi.extension.capitalizeAllCaps
import io.github.tsgrissom.pluginapi.extension.getIPString
import io.github.tsgrissom.pluginapi.extension.palatable
import io.github.tsgrissom.pluginapi.extension.roundToDigits
import io.github.tsgrissom.pluginapi.chat.ClickableText
import net.md_5.bungee.api.ChatColor.*
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

    fun getMaxHealth() : Double {
        val attr = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
            ?: return 20.0
        return attr.value
    }

    fun sendChatComponents(arr: Array<BaseComponent>) {
        player.spigot().sendMessage(*arr)
    }

    fun getAvailableGameModes() : Set<GameMode> {
        val set = mutableSetOf<GameMode>()

        if (player.hasPermission(GameModeCommand.PERM_ADVENTURE))
            set.add(GameMode.ADVENTURE)
        if (player.hasPermission(GameModeCommand.PERM_CREATIVE))
            set.add(GameMode.CREATIVE)
        if (player.hasPermission(GameModeCommand.PERM_SPECTATOR))
            set.add(GameMode.SPECTATOR)
        if (player.hasPermission(GameModeCommand.PERM_SURVIVAL))
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
                builder.append(linePrefix).color(DARK_GRAY)
        }

        if (withHeader)
            builder
                .append("/whois for ").color(GOLD)
                .append(player.name).color(YELLOW)
                .append(" : ").color(DARK_GRAY)
                .append("Temporary").color(GRAY)
                .append("\n")

        if (!excludeGamemode) {
            val gm = player.gameMode.name.capitalizeAllCaps()
            appendPrefix()
            builder
                .append("Gamemode: ").color(GRAY)
                .append(gm).color(AQUA)
                .append("\n")
        }

        if (!excludeWorld) {
            appendPrefix()
            builder
                .append("World: ").color(GRAY)
                .append(player.name).color(YELLOW)
                .append("\n")
        }

        if (!excludeCoordinates) {
            val loc = player.location
            val x = loc.x.roundToDigits(2)
            val y = loc.y.roundToDigits(2)
            val z = loc.z.roundToDigits(2)

            appendPrefix()
            builder
                .append("Location ").color(GRAY)
                .append("X").color(RED)
                .append("Y").color(GREEN)
                .append("Z").color(AQUA)
                .append(": ").color(GRAY)
                .append("$x ").color(RED)
                .append("$y ").color(GREEN)
                .append("$z").color(AQUA)
                .append("\n")
        }

        if (!excludeAfk) {
            appendPrefix()
            builder
                .append("Is AFK: ").color(GRAY)
                .append(isAfk.palatable(withColor=true)).color(YELLOW)
        }

        if (!excludeHealth) {
            val health = player.health
            val attrMaxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
            val maxHealth = attrMaxHealth?.value ?: 20.0

            appendPrefix()
            builder
                .append("Health: ").color(GRAY)
                .append("$health").color(YELLOW)
                .append(" / ").color(DARK_GRAY)
                .append("Max: ").color(GRAY)
                .append("$maxHealth").color(YELLOW)
                .append("\n")
        }

        if (!excludeHunger) {
            val foodLevel = player.foodLevel
            val hunger = 20 - foodLevel

            appendPrefix()
            builder
                .append("Food Level: ").color(GRAY)
                .append("$foodLevel").color(YELLOW)
                .append("/").color(DARK_GRAY)
                .append("20").color(YELLOW)
                .append(" + ").color(DARK_GRAY)
                .append("Hunger: ").color(GRAY)
                .append("$hunger").color(YELLOW)
                .append("\n")
        }

        if (!excludeOxygen) {
            appendPrefix()
            builder
                .append("Oxygen: ").color(GRAY)
                .append("${player.remainingAir}").color(YELLOW)
                .append("/").color(DARK_GRAY)
                .append("300").color(YELLOW)
                .append("\n")
        }

        if (!excludeFireTicks) {
            val ticks = player.fireTicks

            appendPrefix()
            builder.append("On Fire: ").color(GRAY)
            if (ticks > 0) {
                builder
                    .append("Yes").color(GREEN)
                    .append(" + ").color(DARK_GRAY)
                    .append("Ticks Left: ").color(GRAY)
                    .append("$ticks").color(YELLOW)
            } else {
                builder
                    .append("No").color(RED)
            }
            builder.append("\n")
        }

        if (!excludeSpeed) {
            val fly = "${player.flySpeed}"
            val walk = "${player.walkSpeed}"

            appendPrefix()
            builder
                .append("Flying Speed: ").color(GRAY)
                .append(fly).color(YELLOW)
                .append(" + ").color(DARK_GRAY)
                .append("Walking Speed: ").color(GRAY)
                .append(walk).color(YELLOW)
                .append("\n")
        }

        if (!excludeFlight) {
            val canFly = player.allowFlight.palatable(withColor=true)
            val isFlying = player.isFlying.palatable(withColor=true)

            appendPrefix()
            builder
                .append("Can Fly: ").color(GRAY)
                .append(canFly)
                .append(" + ").color(DARK_GRAY)
                .append("Is Flying: ").color(GRAY)
                .append(isFlying)
                .append("\n")
        }

        if (!excludeSneaking || !excludeSprinting) {
            val isSneaking = player.isSneaking.palatable(withColor=true)
            val isSprinting = player.isSprinting.palatable(withColor=true)

            appendPrefix()

            if (!excludeSneaking) {
                builder
                    .append("Is Sneaking: ").color(GRAY)
                    .append(isSneaking).color(YELLOW)
            }

            if (!excludeSneaking && !excludeSprinting)
                builder.append(" + ").color(DARK_GRAY)

            if (!excludeSprinting) {
                builder
                    .append("Is Sprinting: ").color(GRAY)
                    .append(isSprinting).color(YELLOW)
            }

            builder.append("\n")
        }

        if (!excludeExperience) {
            appendPrefix()
            builder
                .append("Level: ").color(GRAY)
                .append("${player.level}").color(YELLOW)
                .append(" + ").color(DARK_GRAY)
                .append("Exp: ").color(GRAY)
                .append("${player.exp}").color(YELLOW)
                .append(" + ").color(DARK_GRAY)
                .append("Total Exp: ").color(GRAY)
                .append("${player.totalExperience}").color(YELLOW)
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
                builder.append(linePrefix).color(DARK_GRAY)
        }

        if (withHeader)
            builder
                .append("/whois for ").color(GOLD)
                .append(player.name).color(YELLOW)
                .append("\n")

        if (!excludeNames) {
            val dn = player.displayName
            val hasNoNickname = dn == player.name
            val nickname = if (hasNoNickname)
                "None"
            else
                dn
            val nicknameColor = if (hasNoNickname)
                RED
            else
                YELLOW

            appendPrefix()
            builder
                .append("Username: ").color(GRAY)
                .append(player.name).color(YELLOW)
                .append(" + ").color(DARK_GRAY)
                .append("Display Name: ").color(GRAY)
                .append(nickname).color(nicknameColor)
                .append("\n")
        }

        if (!excludeUniqueId) {
            val uuid = player.uniqueId.toString()
            val data = ClickableText
                .compose(uuid)
                .color(YELLOW)
                .hoverText("&7Click to copy UUID")
                .action(ClickEvent.Action.COPY_TO_CLIPBOARD)
                .value(uuid)
                .toComponent()

            appendPrefix()
            builder
                .append("Unique ID: ").color(GRAY)
                .append(data).append("\n").reset()
        }

        if (!excludeIp) {
            val ip = this.getIPString()
            val data = ClickableText
                .compose(ip)
                .color(YELLOW)
                .hoverText("&7Click to copy IP address")
                .action(ClickEvent.Action.COPY_TO_CLIPBOARD)
                .value(ip)
                .toComponent()

            appendPrefix()
            builder
                .append("IP Address: ").color(GRAY)
                .append(data).append("\n").reset()
        }

        if (!excludeOperator) {
            val isOp = player.isOp.palatable(withColor=true)
            appendPrefix()
            builder
                .append("Is Op: ").color(GRAY)
                .append(isOp).color(YELLOW)
        }

        return builder.create()
    }
}