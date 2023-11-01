package io.github.tsgrissom.essentialskt.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.config.ChatColorKey
import io.github.tsgrissom.pluginapi.extension.lore
import io.github.tsgrissom.pluginapi.extension.name
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.inventory.ItemStack

class ListWorldsGui : ChestGui(1, "Worlds") {

    private fun getPlugin() : EssentialsKTPlugin =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()

    init {
        this.rows = calculateRows()

        val pane = OutlinePane(0, 0, 9, this.rows)

        Bukkit.getWorlds().forEach { pane.addItem(createWorldGuiButton(it)) }

        this.addPane(pane)
    }

    private fun calculateRows() : Int {
        val size = Bukkit.getWorlds().size

        return if (size <= 9)
            1
        else if (size in 10..18)
            2
        else if (size in 18..27)
            3
        else if (size in 27..36)
            4
        else if (size in 36..45)
            5
        else
            6
    }

    private fun createWorldGuiButton(w: World) : GuiItem {
        val conf = getConfig()
        val ccType = conf.getChatColor(ChatColorKey.Type)
        val ccPrim = conf.getChatColor(ChatColorKey.Primary)
        val ccSec = conf.getChatColor(ChatColorKey.Secondary)
        val ccTert = conf.getChatColor(ChatColorKey.Tertiary)

        val env = w.environment
        val material = when (env) {
            World.Environment.NORMAL -> Material.GRASS_BLOCK
            World.Environment.NETHER -> Material.NETHERRACK
            World.Environment.THE_END -> Material.END_STONE
            else -> Material.COMMAND_BLOCK
        }
        val type = when (env) {
            World.Environment.NORMAL -> "Overworld"
            World.Environment.NETHER -> "Nether"
            World.Environment.THE_END -> "The End"
            World.Environment.CUSTOM -> "Other"
        }

        return GuiItem(
            ItemStack(material)
                .name("${ccPrim}\"${w.name}\"")
                .lore("${ccSec}Type${ccTert}: ${ccType}$type")
        ) {
            val who = it.whoClicked

            it.isCancelled = true
            who.closeInventory()
            // TODO Do something on world gui click
        }
    }
}