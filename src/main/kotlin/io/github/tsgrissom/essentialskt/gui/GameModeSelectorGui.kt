package io.github.tsgrissom.essentialskt.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import io.github.tsgrissom.essentialskt.command.GameModeCommand
import io.github.tsgrissom.pluginapi.extension.*
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.function.Consumer

class GameModeSelectorGui(val p: Player, val t: Player) : ChestGui(1, "Select Gamemode") {

    private fun getGameModeLoreMap() : Map<GameMode, String> {
        val strCurrent = "&eYour current gamemode"
        val strClick = "&7Click to change"
        val strPermission = "&4&lX &cYou do not have permission"

        val adventureLore = if (p.gameMode == GameMode.ADVENTURE) {
            strCurrent
        } else {
            if (p.hasPermission(GameModeCommand.PERM_ADVENTURE)) {
                strClick
            } else {
                strPermission
            }
        }

        val creativeLore = if (p.gameMode == GameMode.CREATIVE) {
            strCurrent
        } else {
            if (p.hasPermission(GameModeCommand.PERM_CREATIVE)) {
                strClick
            } else {
                strPermission
            }
        }

        val spectatorLore = if (p.gameMode == GameMode.SPECTATOR) {
            strCurrent
        } else {
            if (p.hasPermission(GameModeCommand.PERM_SPECTATOR)) {
                strClick
            } else {
                strPermission
            }
        }

        val survivalLore = if (p.gameMode == GameMode.SURVIVAL) {
            strCurrent
        } else {
            if (p.hasPermission(GameModeCommand.PERM_SURVIVAL)) {
                strClick
            } else {
                strPermission
            }
        }

        val map = mutableMapOf<GameMode, String>()
        map[GameMode.ADVENTURE] = adventureLore
        map[GameMode.CREATIVE] = creativeLore
        map[GameMode.SPECTATOR] = spectatorLore
        map[GameMode.SURVIVAL] = survivalLore
        return map
    }

    init {
        val tn = t.name
        val gm = t.gameMode.name.capitalizeAllCaps()
        val lore = getGameModeLoreMap()
        val skullItem = ItemStack(Material.PLAYER_HEAD)
            .name("&e$tn")
            .lore(
                "&7In &b$gm"
            )

        val sm = skullItem.itemMeta as SkullMeta
        sm.ownerProfile = t.playerProfile
        skullItem.itemMeta = sm

        val pane = OutlinePane(2, 0, 5, 1)
        pane.addItems(
            GuiItem(
                ItemStack(Material.MAP)
                    .name("&6Adventure Mode")
                    .lore(lore[GameMode.ADVENTURE] ?: "Error")
            ) { e ->
                e.whoClicked.closeInventory()
                Bukkit.dispatchCommand(e.whoClicked, "gm adventure ${t.name}")
            },
            GuiItem(
                ItemStack(Material.BEDROCK)
                    .name("&6Creative Mode")
                    .lore(lore[GameMode.CREATIVE] ?: "Error")
            ) { e ->
                e.whoClicked.closeInventory()
                Bukkit.dispatchCommand(e.whoClicked, "gm creative ${t.name}")
            },
            GuiItem(
                ItemStack(Material.STONE_SWORD)
                    .name("&6Survival Mode")
                    .lore(lore[GameMode.SURVIVAL] ?: "Error")
                    .flag(ItemFlag.HIDE_ATTRIBUTES)
            ) { e ->
                e.whoClicked.closeInventory()
                Bukkit.dispatchCommand(e.whoClicked, "gm survival ${t.name}")
            },
            GuiItem(
                ItemStack(Material.COMPASS)
                    .name("&6Spectator Mode")
                    .lore(lore[GameMode.SPECTATOR] ?: "Error")
            ) { e ->
                e.whoClicked.closeInventory()
                Bukkit.dispatchCommand(e.whoClicked, "gm spectator ${t.name}")
            },
            GuiItem(skullItem)
        )

        onGlobalClick = Consumer { e ->
            e.isCancelled = true
        }

        onClose = Consumer { e ->
            click(e.player)
        }

        addPane(pane)
    }
}