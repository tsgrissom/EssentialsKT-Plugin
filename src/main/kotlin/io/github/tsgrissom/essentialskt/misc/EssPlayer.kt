package io.github.tsgrissom.essentialskt.misc

import com.earth2me.essentials.User
import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.command.GameModeCommand
import io.github.tsgrissom.pluginapi.chat.ClickableText
import io.github.tsgrissom.pluginapi.extension.*
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.ChatColor.DARK_GRAY as D_GRAY
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

    fun getEssentialsUser() : User = getPlugin().getEssentials().getUser(player)
    fun isAfk() : Boolean = getEssentialsUser().isAfk // FIXME I don't think Essentials API is working here

    fun getMaxHealth() : Double {
        val attr = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
            ?: return 20.0
        return attr.value
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
                builder.appendc(linePrefix, D_GRAY)
        }

        if (withHeader)
            builder
                .appendc("/whois for ", GOLD)
                .appendc(player.name, YELLOW)
                .appendc(" : ", D_GRAY)
                .appendc("Temporary", GRAY)
                .append("\n")

        if (!excludeGamemode) {
            val gm = player.gameMode.name.capitalizeAllCaps()
            appendPrefix()
            builder
                .appendc("Gamemode: ", GRAY)
                .appendc(gm, AQUA)
                .append("\n")
        }

        if (!excludeWorld) {
            appendPrefix()
            builder
                .appendc("World: ", GRAY)
                .appendc(player.name, YELLOW)
                .append("\n")
        }

        if (!excludeCoordinates) {
            val loc = player.location
            val x = loc.x.roundToDigits(2)
            val y = loc.y.roundToDigits(2)
            val z = loc.z.roundToDigits(2)

            appendPrefix()
            builder
                .appendc("Location ", GRAY)
                .appendc("X", RED)
                .appendc("Y", GREEN)
                .appendc("Z", AQUA)
                .appendc(": ", GRAY)
                .appendc("$x ", RED)
                .appendc("$y ", GREEN)
                .appendc("$z", AQUA)
                .append("\n")
        }

        if (!excludeAfk) {
            appendPrefix()
            builder
                .appendc("Is AFK: ", GRAY)
                .append(isAfk().palatable(withColor=true))
        }

        if (!excludeHealth) {
            val health = player.health
            val attrMaxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)
            val maxHealth = attrMaxHealth?.value ?: 20.0

            appendPrefix()
            builder
                .appendc("Health: ", GRAY)
                .appendc("$health", YELLOW)
                .appendc(" / ", D_GRAY)
                .appendc("Max: ", GRAY)
                .appendc("$maxHealth", YELLOW)
                .append("\n")
        }

        if (!excludeHunger) {
            val foodLevel = player.foodLevel
            val hunger = 20 - foodLevel

            appendPrefix()
            builder
                .appendc("Food Level: ", GRAY)
                .appendc("$foodLevel", YELLOW)
                .appendc("/", D_GRAY)
                .appendc("20", YELLOW)
                .appendc(" + ", D_GRAY)
                .appendc("Hunger: ", GRAY)
                .appendc("$hunger", YELLOW)
                .append("\n")
        }

        if (!excludeOxygen) {
            appendPrefix()
            builder
                .appendc("Oxygen: ", GRAY)
                .appendc("${player.remainingAir}", YELLOW)
                .appendc("/", D_GRAY)
                .appendc("300", YELLOW)
                .append("\n")
        }

        if (!excludeFireTicks) {
            val ticks = player.fireTicks

            appendPrefix()
            builder.appendc("On Fire: ", GRAY)
            if (ticks > 0) {
                builder
                    .appendc("Yes", GREEN)
                    .appendc(" + ", D_GRAY)
                    .appendc("Ticks Left: ", GRAY)
                    .appendc("$ticks", YELLOW)
            } else {
                builder
                    .appendc("No", RED)
            }
            builder.append("\n")
        }

        if (!excludeSpeed) {
            val fly = "${player.flySpeed}"
            val walk = "${player.walkSpeed}"

            appendPrefix()
            builder
                .appendc("Flying Speed: ", GRAY)
                .appendc(fly, YELLOW)
                .appendc(" + ", D_GRAY)
                .appendc("Walking Speed: ", GRAY)
                .appendc(walk, YELLOW)
                .append("\n")
        }

        if (!excludeFlight) {
            val canFly = player.allowFlight.palatable(withColor=true)
            val isFlying = player.isFlying.palatable(withColor=true)

            appendPrefix()
            builder
                .appendc("Can Fly: ", GRAY)
                .append(canFly)
                .appendc(" + ", D_GRAY)
                .appendc("Is Flying: ", GRAY)
                .append(isFlying)
                .append("\n")
        }

        if (!excludeSneaking || !excludeSprinting) {
            val isSneaking = player.isSneaking.palatable(withColor=true)
            val isSprinting = player.isSprinting.palatable(withColor=true)

            appendPrefix()

            if (!excludeSneaking) {
                builder
                    .appendc("Is Sneaking: ", GRAY)
                    .appendc(isSneaking, YELLOW)
            }

            if (!excludeSneaking && !excludeSprinting)
                builder.appendc(" + ", D_GRAY)

            if (!excludeSprinting) {
                builder
                    .appendc("Is Sprinting: ", GRAY)
                    .appendc(isSprinting, YELLOW)
            }

            builder.append("\n")
        }

        if (!excludeExperience) {
            appendPrefix()
            builder
                .appendc("Level: ", GRAY)
                .appendc("${player.level}", YELLOW)
                .appendc(" + ", D_GRAY)
                .appendc("Exp: ", GRAY)
                .appendc("${player.exp}", YELLOW)
                .appendc(" + ", D_GRAY)
                .appendc("Total Exp: ", GRAY)
                .appendc("${player.totalExperience}", YELLOW)
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
                builder.appendc(linePrefix, D_GRAY)
        }

        if (withHeader)
            builder
                .appendc("/whois for ", GOLD)
                .appendc(player.name, YELLOW)
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
                .appendc("Username: ", GRAY)
                .appendc(player.name, YELLOW)
                .appendc(" + ", D_GRAY)
                .appendc("Display Name: ", GRAY)
                .appendc(nickname, nicknameColor)
                .append("\n")
        }

        if (!excludeUniqueId) {
            val uuid = player.uniqueId.toString()
            val data = ClickableText
                .compose(uuid)
                .color(YELLOW)
                .hoverText("${GRAY}Click to copy UUID")
                .action(ClickEvent.Action.COPY_TO_CLIPBOARD)
                .value(uuid)
                .toComponent()

            appendPrefix()
            builder
                .appendc("Unique ID: ", GRAY)
                .append(data).append("\n").reset()
        }

        if (!excludeIp) {
            val ip = this.getIPString()
            val data = ClickableText
                .compose(ip)
                .color(YELLOW)
                .hoverText("${GRAY}Click to copy IP address")
                .action(ClickEvent.Action.COPY_TO_CLIPBOARD)
                .value(ip)
                .toComponent()

            appendPrefix()
            builder
                .appendc("IP Address: ", GRAY)
                .append(data).append("\n").reset()
        }

        if (!excludeOperator) {
            val isOp = player.isOp.palatable(withColor=true)
            appendPrefix()
            builder
                .appendc("Is Op: ", GRAY)
                .appendc(isOp, YELLOW)
        }

        return builder.create()
    }
}