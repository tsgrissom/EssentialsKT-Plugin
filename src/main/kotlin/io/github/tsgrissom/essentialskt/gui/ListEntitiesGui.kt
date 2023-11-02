package io.github.tsgrissom.essentialskt.gui

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane
import io.github.tsgrissom.essentialskt.EssentialsKTPlugin
import io.github.tsgrissom.essentialskt.config.ChatColorKey
import io.github.tsgrissom.pluginapi.extension.bukkit.*
import io.github.tsgrissom.pluginapi.extension.kt.capitalizeEachWordAllCaps
import io.github.tsgrissom.pluginapi.extension.kt.fmtYesNo
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack

class ListEntitiesGui(
    private val types: Collection<EntityType>,
    private val titleNoun: String,
    private val paginatedPaneHeight: Int
) : ChestGui(6, "Entities") {

    private fun getPlugin() =
        EssentialsKTPlugin.instance ?: error("plugin instance is null")
    private fun getConfig() = getPlugin().getConfigManager()
    private fun getEntityUtility() =
        getPlugin().getEntityUtility()

    constructor():
            this(EntityType.entries, "Entities", 5)
    constructor(types: Collection<EntityType>, title: String):
            this(types, title, 5)

    init {
        populateContents()
    }

    private fun createEntityTypeGuiItem(type: EntityType) : GuiItem {
        val conf = getConfig()
        val ccType = conf.getChatColor(ChatColorKey.Type)
        val ccUser = conf.getChatColor(ChatColorKey.Username)
        val ccSec = conf.getChatColor(ChatColorKey.Secondary)
        val ccTert = conf.getChatColor(ChatColorKey.Tertiary)
        val ccReset = ChatColor.RESET

        val typeName = type.name
        val name = typeName.capitalizeEachWordAllCaps()
        val isAlive = type.isAlive.fmtYesNo(withColor=true)
        val isSpawnable = type.isSpawnable.fmtYesNo(withColor=true)

        val material = getEntityUtility()
            .getMaterialRepresentationForType(type)

        return GuiItem(
            ItemStack(material)
                .name("${ccUser}$name")
                .lore(
                    "${ccTert}> ${ccSec}Type${ccTert}: ${ccType}$typeName",
                    "${ccTert}> ${ccSec}Alive${ccTert}: ${ccReset}$isAlive",
                    "${ccTert}> ${ccSec}Spawnable${ccTert}: ${ccReset}$isSpawnable"
                )
        ) {
            // TODO Do something on entity gui click
        }
    }

    private fun updateTitle(pagination: PaginatedPane) {
        val indexStr = pagination.getPageIndexString()
        if (indexStr=="1/1") {
            this.title = titleNoun
            return
        }
        this.title = "$titleNoun ($indexStr)"
    }

    private fun createToolbar(pagination: PaginatedPane) : OutlinePane {
        val conf = getConfig()
        val ccPrim = conf.getChatColor(ChatColorKey.Primary)
        val ccSec = conf.getChatColor(ChatColorKey.Secondary)

        val toolbar = OutlinePane(0, paginatedPaneHeight, 9, 1)
        val itemPrevious = ItemStack(Material.ARROW).name("${ccPrim}Previous Page")
        val itemCenter = ItemStack(Material.COMPASS).name("${ccSec}Navigation")
        val itemNext = ItemStack(Material.ARROW).name("${ccPrim}Next Page")
        val btnPrevious = GuiItem(itemPrevious) {
            pagination.setPage(pagination.getPreviousIndex())
            updateTitle(pagination)
            update()
        }
        val btnCenter = GuiItem(itemCenter)
        val btnNext = GuiItem(itemNext) {
            pagination.setPage(pagination.getNextIndex())
            updateTitle(pagination)
            update()
        }

        toolbar.align(OutlinePane.Alignment.CENTER)
        toolbar.addItems(btnPrevious, btnCenter, btnNext)

        return toolbar
    }

    private fun populateContents() {
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

        addPane(createToolbar(pagination))
        addPane(pagination)

        setOnGlobalClick {
            it.isCancelled = true
        }
        setOnClose {
            click(it.player)
        }

        updateTitle(pagination)
    }
}