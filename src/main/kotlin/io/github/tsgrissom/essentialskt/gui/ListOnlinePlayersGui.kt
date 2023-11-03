package io.github.tsgrissom.essentialskt.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.config.ChatColorKey
import io.github.tsgrissom.pluginapi.extension.bukkit.*
import io.github.tsgrissom.pluginapi.extension.kt.roundToDigits
import net.md_5.bungee.api.ChatColor.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class ListOnlinePlayersGui : ChestGui(5, "Online Players") {

    private fun getPlugin() =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()

    init {
        val op = Bukkit.getOnlinePlayers()
        val pane = OutlinePane(0, 0, 9, 5)

        op.forEach { pane.addItem(createPlayerHead(it)) }

        this.addPane(pane)
    }

    private fun createPlayerHead(p: Player) : GuiItem {
        val conf = getConfig()
        val ccUser = conf.getChatColor(ChatColorKey.Username)
        val ccVal = conf.getChatColor(ChatColorKey.Value)
        val ccSec = conf.getChatColor(ChatColorKey.Secondary)
        val ccTert = conf.getChatColor(ChatColorKey.Tertiary)
        
        val wn = p.world.name
        val x = p.location.x.roundToDigits(1)
        val y = p.location.y.roundToDigits(1)
        val z = p.location.z.roundToDigits(1)

        val n = p.name
        val dn = p.displayName
        val uuid = p.uniqueString

        return GuiItem(
            ItemStack(Material.PLAYER_HEAD)
                .playerHeadOf(p)
                .name("${GOLD}$n")
                .lore(
                    "${ccSec}Click to view their ${ccVal}/whois ${ccSec}profile",
                    "${ccTert}> ${ccSec}Nickname${ccTert}: ${ccUser}$dn",
                    "${ccTert}> ${ccSec}UUID${ccTert}: ${ccUser}$uuid",
                    "${ccTert}> ${ccSec}World${ccTert}: ${ccVal}$wn",
                    "${ccTert}> ${ccSec}Location ${RED}X${GREEN}Y${AQUA}Z${ccTert}: ${RED}$x ${GREEN}$y ${AQUA}$z"
                )
                .flag(ItemFlag.HIDE_ATTRIBUTES)
        ) { e ->
            e.isCancelled = true
            e.whoClicked.closeInventory()
            Bukkit.dispatchCommand(e.whoClicked, "whois ${p.name}")
        }
    }
}