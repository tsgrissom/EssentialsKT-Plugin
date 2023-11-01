package io.github.tsgrissom.essentialskt.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.command.GameModeCommand
import io.github.tsgrissom.essentialskt.config.ChatColorKey
import io.github.tsgrissom.pluginapi.extension.*
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class GameModeSelectorGui(val sender: Player, val target: Player) : ChestGui(1, "Select Gamemode") {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()

    companion object {
        private const val STR_CURRENT      = "&eYour current gamemode"
        private const val STR_CLICK        = "&aClick to use"
        private const val STR_NOPERMISSION = "&cYou do not have permission to use this"
    }

    private fun alterGameMode(whoClicked: HumanEntity, mode: String) =
        Bukkit.dispatchCommand(whoClicked, "gm $mode ${target.name}")

    init {
        val conf = getConfig()
        val ccUser = conf.getChatColor(ChatColorKey.Username)
        val ccSec = conf.getChatColor(ChatColorKey.Secondary)
        val ccType = conf.getChatColor(ChatColorKey.Type)

        val tn = target.name
        val gm = target.gameMode.name.capitalizeAllCaps()
        val currentItem = GuiItem(
            ItemStack(Material.PLAYER_HEAD)
                .playerHeadOf(target)
                .name("${ccUser}$tn")
                .lore("${ccSec}In ${ccType}$gm")
        )

        val pane = OutlinePane(2, 0, 5, 1)
        pane.addItems(
            getAdventureGuiItem(),
            getCreativeGuiItem(),
            getSurvivalGuiItem(),
            getSpectatorGuiItem(),
            currentItem
        )

        setOnGlobalClick {
            it.isCancelled = true
        }
        setOnClose {
            click(it.player)
        }

        addPane(pane)
    }

    private fun getAdventureGuiItem(): GuiItem {
        val ccPrim = getConfig().getChatColor(ChatColorKey.Primary)
        val lore = if (sender.gameMode == GameMode.ADVENTURE) {
            STR_CURRENT
        } else {
            if (sender.hasPermission(GameModeCommand.PERM_ADVENTURE)) {
                STR_CLICK
            } else {
                STR_NOPERMISSION
            }
        }

        return GuiItem(
            ItemStack(Material.MAP)
                .name("${ccPrim}Adventure Mode")
                .lore(lore)
        ) { e ->
            val who = e.whoClicked
            who.closeInventory()
            alterGameMode(who, "adventure")
        }
    }

    private fun getCreativeGuiItem(): GuiItem {
        val ccPrim = getConfig().getChatColor(ChatColorKey.Primary)
        val lore = if (sender.gameMode == GameMode.CREATIVE) {
            STR_CURRENT
        } else {
            if (sender.hasPermission(GameModeCommand.PERM_CREATIVE)) {
                STR_CLICK
            } else {
                STR_NOPERMISSION
            }
        }

        return GuiItem(
            ItemStack(Material.BEDROCK)
                .name("${ccPrim}Creative Mode")
                .lore(lore)
        ) { e ->
            val who = e.whoClicked
            who.closeInventory()
            alterGameMode(who, "creative")
        }
    }

    private fun getSurvivalGuiItem(): GuiItem {
        val ccPrim = getConfig().getChatColor(ChatColorKey.Primary)
        val lore = if (sender.gameMode == GameMode.SURVIVAL) {
            STR_CURRENT
        } else {
            if (sender.hasPermission(GameModeCommand.PERM_SURVIVAL)) {
                STR_CLICK
            } else {
                STR_NOPERMISSION
            }
        }

        return GuiItem(
            ItemStack(Material.STONE_SWORD)
                .name("${ccPrim}Survival Mode")
                .lore(lore)
                .flag(ItemFlag.HIDE_ATTRIBUTES)
        ) { e ->
            val who = e.whoClicked
            who.closeInventory()
            alterGameMode(who, "survival")
        }
    }

    private fun getSpectatorGuiItem() : GuiItem {
        val ccPrim = getConfig().getChatColor(ChatColorKey.Primary)
        val lore = if (sender.gameMode == GameMode.SPECTATOR) {
            STR_CURRENT
        } else {
            if (sender.hasPermission(GameModeCommand.PERM_SPECTATOR)) {
                STR_CLICK
            } else {
                STR_NOPERMISSION
            }
        }

        return GuiItem(
            ItemStack(Material.COMPASS)
                .name("${ccPrim}Spectator Mode")
                .lore(lore)
        ) { e ->
            val who = e.whoClicked
            who.closeInventory()
            alterGameMode(who, "spectator")
        }
    }
}