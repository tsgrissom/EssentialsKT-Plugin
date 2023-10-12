package io.github.tsgrissom.essentialskt.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.pluginapi.extension.*
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack

class ListEntitiesGui(
    private val types: Collection<EntityType>,
    private val titleNoun: String,
    private val paginatedPaneHeight: Int
) : ChestGui(6, "Entities") {

    private fun getPlugin() =
        EssentialsKTPlugin.instance ?: error("Plugin is not instantiated!")
    private fun getEntityUtility() =
        getPlugin().getEntityUtility()

    constructor():
            this(EntityType.entries, "Entities", 5)
    constructor(types: Collection<EntityType>, title: String):
            this(types, title, 5)

    private fun getPageIndexString(pagination: PaginatedPane) : String =
        "${pagination.page + 1}/${pagination.pages}"
    private fun getPreviousIndex(pagination: PaginatedPane) =
        if (pagination.page > 0)
            pagination.page - 1
        else
            pagination.pages - 1
    private fun getNextIndex(pagination: PaginatedPane) =
        if ((pagination.pages - 1) > pagination.page)
            pagination.page + 1
        else
            0

    private fun createEntityTypeGuiItem(type: EntityType) : GuiItem {
        val name = type.name.capitalizeEachWordAllCaps()
        val isAlive = type.isAlive.palatable(withColor=true)
        val isSpawnable = type.isSpawnable.palatable(withColor=true)

        val material = getEntityUtility()
            .getMaterialRepresentationForType(type)

        return GuiItem(
            ItemStack(material)
                .name("&e${name}")
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

    private fun updateTitle(pagination: PaginatedPane) {
        val indexStr = getPageIndexString(pagination)
        if (indexStr=="1/1") {
            this.title = titleNoun
            return
        }
        this.title = "$titleNoun (${getPageIndexString(pagination)})"
    }

    private fun createToolbar(pagination: PaginatedPane) : OutlinePane {
        val toolbar = OutlinePane(0, paginatedPaneHeight, 9, 1)
        val btnPrevious = GuiItem(
            ItemStack(Material.ARROW)
                .name("&6Previous Page")
        ) {
            it.isCancelled = true

            pagination.setPage(getPreviousIndex(pagination))
            updateTitle(pagination)
            update()
        }
        val btnCurrent = GuiItem(
            ItemStack(Material.COMPASS)
                .name("&eCurrent: &7${getPageIndexString(pagination)}")
        ) {
            it.isCancelled = true
        }
        val btnNext = GuiItem(
            ItemStack(Material.ARROW)
                .name("&6Next Page")
        ) {
            it.isCancelled = true

            pagination.setPage(getNextIndex(pagination))
            updateTitle(pagination)
            update()
        }

        toolbar.align(OutlinePane.Alignment.CENTER)
        toolbar.addItems(btnPrevious, btnCurrent, btnNext)

        return toolbar
    }

    private fun populateContents() {
        this.panes.clear()
        val pagination = PaginatedPane(0, 0, 9, paginatedPaneHeight)

        val maxSlot = paginatedPaneHeight * 9
        var currentIndex = 0
        var currentPane = OutlinePane(0, 0, 9, paginatedPaneHeight)

        val entityNames = types.map { it.name }.sorted()

        for (name in entityNames) {
            val type = EntityType.valueOf(name)

            if (currentIndex == maxSlot) {
                pagination.addPage(currentPane)
                currentPane = OutlinePane(0, 0, 9, paginatedPaneHeight)
                currentIndex = 0
                continue
            }

            val el = createEntityTypeGuiItem(type)
            currentPane.addItem(el)
            currentIndex++
        }

        this.addPane(createToolbar(pagination))
        this.addPane(pagination)
        updateTitle(pagination)
    }

    init {
        populateContents()
    }
}