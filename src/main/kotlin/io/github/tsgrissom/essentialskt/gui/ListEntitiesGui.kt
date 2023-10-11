package io.github.tsgrissom.essentialskt.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import io.github.tsgrissom.pluginapi.extension.*
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack

class ListEntitiesGui(
    types: Collection<EntityType>
) : ChestGui(6, "Entities") {

    private fun createEntityTypeGuiItem(type: EntityType) : GuiItem {
        val name = type.name.capitalizeEachWordAllCaps()
        val isAlive = type.isAlive.palatable(withColor=true)
        val isSpawnable = type.isSpawnable.palatable(withColor=true)

        return GuiItem(
            ItemStack(Material.SKELETON_SKULL)
                .name("&b${name}")
                .lore(
                    "&8> &7Type: &b${type.name}",
                    "&8> &7Alive: &r$isAlive",
                    "&8> &7Spawnable: &r$isSpawnable"
                )
        ) {
            it.isCancelled = true
            // TODO Do something on entity gui click
        }
    }

    init {
        val paginatedHeight = 5;
        val pagination = PaginatedPane(0, 0, 9, paginatedHeight)

        var currentIndex = 0
        var currentPane = OutlinePane(0, 0, 9, paginatedHeight)

        val entityNames = types.map { it.name }.sorted()

        for (name in entityNames) {
            val type = EntityType.valueOf(name)

            if (currentIndex == (paginatedHeight * 9)) {
                pagination.addPage(currentPane)
                currentPane = OutlinePane(0, 0, 9, paginatedHeight)
                currentIndex = 0
                continue
            }

            val el = createEntityTypeGuiItem(type)
            currentPane.addItem(el)
            currentIndex++
        }

        val toolbar = OutlinePane(0, paginatedHeight, 9, 1)
        val btnPrevious = GuiItem(
            ItemStack(Material.ARROW)
                .name("&6Previous Page")
        ) {
            it.isCancelled = true

            val previous = if (pagination.page > 0)
                pagination.page - 1
            else
                pagination.pages - 1

            this.title = "Entities (${pagination.page + 1}/${pagination.pages})"
            pagination.setPage(previous)
            this.update()
        }
        val btnCurrent = GuiItem(
            ItemStack(Material.COMPASS)
                .name("&eCurrent: &7${pagination.page + 1}/${pagination.pages}")
        ) {
            it.isCancelled = true
        }
        val btnNext = GuiItem(
            ItemStack(Material.ARROW)
                .name("&6Next Page")
        ) {
            it.isCancelled = true

            val next = if ((pagination.pages - 1) > pagination.page)
                pagination.page + 1
            else
                0

            this.title = "Entities (${pagination.page + 1}/${pagination.pages})"
            pagination.setPage(next)
            this.update()
        }

        toolbar.align(OutlinePane.Alignment.CENTER)
        toolbar.addItems(btnPrevious, btnCurrent, btnNext)

        this.addPane(toolbar)
        this.addPane(pagination)
    }
}