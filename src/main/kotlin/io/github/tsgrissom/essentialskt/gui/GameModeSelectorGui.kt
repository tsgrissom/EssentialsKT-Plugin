package io.github.tsgrissom.essentialskt.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import io.github.tsgrissom.essentialskt.command.GameModeCommand
import io.github.tsgrissom.pluginapi.extension.*
import net.md_5.bungee.api.ChatColor.*
import net.md_5.bungee.api.ChatColor.DARK_GRAY as D_GRAY
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

class GameModeSelectorGui(val p: Player, val t: Player) : ChestGui(1, "Select Gamemode") {

    companion object {
        private const val STR_CURRENT      = "&eYour current gamemode"
        private const val STR_CLICK        = "&aClick to use"
        private const val STR_NOPERMISSION = "&cYou do not have permission to use this"
    }

    private fun alterGameMode(whoClicked: HumanEntity, mode: String) =
        Bukkit.dispatchCommand(whoClicked, "gm $mode ${t.name}")

    init {
        val tn = t.name
        val gm = t.gameMode.name.capitalizeAllCaps()
        val currentItem = GuiItem(
            ItemStack(Material.PLAYER_HEAD)
                .playerHeadOf(t)
                .name("${YELLOW}$tn")
                .lore("${GRAY}In ${AQUA}$gm")
        )

        val pane = OutlinePane(2, 0, 5, 1)
        pane.addItems(
            getAdventureGuiItem(),
            getCreativeGuiItem(),
            getSurvivalGuiItem(),
            getSpectatorGuiItem(),
            currentItem
        )

        onGlobalClick = Consumer { e ->
            e.isCancelled = true
        }

        onClose = Consumer { e ->
            click(e.player)
        }

        addPane(pane)
    }

    private fun getAdventureGuiItem(): GuiItem {
        val lore = if (p.gameMode == GameMode.ADVENTURE) {
            STR_CURRENT
        } else {
            if (p.hasPermission(GameModeCommand.PERM_ADVENTURE)) {
                STR_CLICK
            } else {
                STR_NOPERMISSION
            }
        }

        return GuiItem(
            ItemStack(Material.MAP)
                .name("${GOLD}Adventure Mode")
                .lore(lore)
        ) { e ->
            val who = e.whoClicked
            who.closeInventory()
            alterGameMode(who, "adventure")
        }
    }

    private fun getCreativeGuiItem(): GuiItem {
        val lore = if (p.gameMode == GameMode.CREATIVE) {
            STR_CURRENT
        } else {
            if (p.hasPermission(GameModeCommand.PERM_CREATIVE)) {
                STR_CLICK
            } else {
                STR_NOPERMISSION
            }
        }

        return GuiItem(
            ItemStack(Material.BEDROCK)
                .name("${GOLD}Creative Mode")
                .lore(lore)
        ) { e ->
            val who = e.whoClicked
            who.closeInventory()
            alterGameMode(who, "creative")
        }
    }

    private fun getSurvivalGuiItem(): GuiItem {
        val lore = if (p.gameMode == GameMode.SURVIVAL) {
            STR_CURRENT
        } else {
            if (p.hasPermission(GameModeCommand.PERM_SURVIVAL)) {
                STR_CLICK
            } else {
                STR_NOPERMISSION
            }
        }

        return GuiItem(
            ItemStack(Material.STONE_SWORD)
                .name("${GOLD}Survival Mode")
                .lore(lore)
                .flag(ItemFlag.HIDE_ATTRIBUTES)
        ) { e ->
            val who = e.whoClicked
            who.closeInventory()
            alterGameMode(who, "survival")
        }
    }

    private fun getSpectatorGuiItem() : GuiItem {
        val lore = if (p.gameMode == GameMode.SPECTATOR) {
            STR_CURRENT
        } else {
            if (p.hasPermission(GameModeCommand.PERM_SPECTATOR)) {
                STR_CLICK
            } else {
                STR_NOPERMISSION
            }
        }

        return GuiItem(
            ItemStack(Material.COMPASS)
                .name("${GOLD}Spectator Mode")
                .lore(lore)
        ) { e ->
            val who = e.whoClicked
            who.closeInventory()
            alterGameMode(who, "spectator")
        }
    }
}