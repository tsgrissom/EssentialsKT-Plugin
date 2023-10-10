package io.github.tsgrissom.essentialskt.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import io.github.tsgrissom.pluginapi.extension.lore
import io.github.tsgrissom.pluginapi.extension.name
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.inventory.ItemStack

class WorldListGui : ChestGui(1, "Worlds") {

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
                .name("&e\"${w.name}\"")
                .lore("&7Type&8: &b$type")
        ) {
            val who = it.whoClicked

            it.isCancelled = true
            who.closeInventory()
            // TODO Do something on world gui click
        }
    }

    init {
        this.rows = calculateRows()

        val pane = OutlinePane(0, 0, 9, this.rows)

        Bukkit.getWorlds().forEach { pane.addItem(createWorldGuiButton(it)) }

        this.addPane(pane)
    }
}