package io.github.tsgrissom.essentialskt.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import io.github.tsgrissom.essentialskt.command.GamemodeCommand
import io.github.tsgrissom.pluginapi.extension.capitalizeAllCaps
import io.github.tsgrissom.pluginapi.extension.flag
import io.github.tsgrissom.pluginapi.extension.lore
import io.github.tsgrissom.pluginapi.extension.name
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class GameModeSelectorGui(val p: Player, val t: Player) : ChestGui(1, "Select Gamemode") {

    init {
        val changeLore = "&7Click to change"
        val noPermission = "&4You do not have permission"
        val adventureLore =
            if (p.hasPermission(GamemodeCommand.PERM_ADVENTURE)) changeLore
            else noPermission
        val creativeLore =
            if (p.hasPermission(GamemodeCommand.PERM_CREATIVE)) changeLore
            else noPermission
        val survivalLore =
            if (p.hasPermission(GamemodeCommand.PERM_SURVIVAL)) changeLore
            else noPermission
        val spectatorLore =
            if (p.hasPermission(GamemodeCommand.PERM_SPECTATOR)) changeLore
            else noPermission

        val adventure = GuiItem(
            ItemStack(Material.MAP)
                .name("&6Adventure Mode")
                .lore(adventureLore)
        ) { e ->
            val who = e.whoClicked
            e.isCancelled = true
            who.closeInventory()
            Bukkit.dispatchCommand(who, "gm adventure ${t.name}")
        }
        val creative = GuiItem(
            ItemStack(Material.BEDROCK)
                .name("&6Creative Mode")
                .lore(creativeLore)
        ) { e ->
            val who = e.whoClicked
            e.isCancelled = true
            who.closeInventory()
            Bukkit.dispatchCommand(who, "gm creative ${t.name}")
        }
        val survival = GuiItem(
            ItemStack(Material.STONE_SWORD)
                .name("&6Survival Mode")
                .lore(survivalLore)
                .flag(ItemFlag.HIDE_ATTRIBUTES)
        ) { e ->
            val who = e.whoClicked
            e.isCancelled = true
            who.closeInventory()
            Bukkit.dispatchCommand(who, "gm survival ${t.name}")
        }
        val spectator = GuiItem(
            ItemStack(Material.COMPASS)
                .name("&6Spectator Mode")
                .lore(spectatorLore)
        ) { e ->
            val who = e.whoClicked
            e.isCancelled = true
            who.closeInventory()
            Bukkit.dispatchCommand(who, "gm spectator ${t.name}")
        }

        val tn = t.name
        val gm = t.gameMode.name.capitalizeAllCaps()
        val skullItem = ItemStack(Material.PLAYER_HEAD)
            .name("&bCurrent Gamemode")
            .lore(
                "&e$tn &8-> &7$gm"
            )

        val sm = skullItem.itemMeta as SkullMeta
        sm.ownerProfile = t.playerProfile
        skullItem.itemMeta = sm

        val skull = GuiItem(skullItem) { e ->
            e.isCancelled = true
        }

        val pane = OutlinePane(2, 0, 5, 1)
        pane.addItem(adventure)
        pane.addItem(creative)
        pane.addItem(survival)
        pane.addItem(spectator)
        pane.addItem(skull)

        addPane(pane)
    }
}