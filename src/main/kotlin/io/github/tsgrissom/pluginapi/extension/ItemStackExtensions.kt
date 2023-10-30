package io.github.tsgrissom.pluginapi.extension

import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.material.MaterialData
import org.bukkit.profile.PlayerProfile
import java.util.*
import java.util.function.Consumer

// TODO Write docs

fun ItemStack.playerHeadOf(player: OfflinePlayer) : ItemStack =
    this.playerHeadOf(player.playerProfile)
fun ItemStack.playerHeadOf(profile: PlayerProfile) : ItemStack {
    if (this.type != Material.PLAYER_HEAD && this.type != Material.PLAYER_WALL_HEAD)
        error("Cannot set skullOwner for non player head material \"${this.type}\"")

    val meta = this.itemMeta as SkullMeta
    meta.ownerProfile = profile
    this.itemMeta = meta
    return this
}

fun ItemStack.amount(amount: Int): ItemStack {
    setAmount(amount)
    return this
}

fun ItemStack.name(name: String): ItemStack {
    val meta = itemMeta
    meta?.setDisplayName(name.translateColor())
    itemMeta = meta
    return this
}

fun ItemStack.lore(text: String): ItemStack {
    val meta = itemMeta
    var lore: MutableList<String>? = meta?.lore
    if (lore == null) {
        lore = ArrayList()
    }
    lore.add(text)
    meta?.lore = lore.c()
    itemMeta = meta
    return this
}

fun ItemStack.lore(vararg text: String): ItemStack {
    Arrays.stream(text).forEach { this.lore(it) }
    return this
}

fun ItemStack.lore(text: List<String>): ItemStack {
    text.forEach { this.lore(it) }
    return this
}

fun ItemStack.durability(durability: Int): ItemStack {
    setDurability(durability.toShort())
    return this
}

fun ItemStack.data(data: Int): ItemStack {
    setData(MaterialData(type, data.toByte()))
    return this
}

fun ItemStack.enchantment(enchantment: Enchantment, level: Int): ItemStack {
    addUnsafeEnchantment(enchantment, level)
    return this
}

fun ItemStack.enchantment(enchantment: Enchantment): ItemStack {
    addUnsafeEnchantment(enchantment, 1)
    return this
}

fun ItemStack.type(material: Material): ItemStack {
    type = material
    return this
}

fun ItemStack.clearLore(): ItemStack {
    val meta = itemMeta
    meta?.lore = ArrayList()
    itemMeta = meta
    return this
}

fun ItemStack.clearEnchantments(): ItemStack {
    enchantments.keys.forEach(Consumer { this.removeEnchantment(it) })
    return this
}

fun ItemStack.color(color: Color): ItemStack {
    if (type == Material.LEATHER_BOOTS
        || type == Material.LEATHER_CHESTPLATE
        || type == Material.LEATHER_HELMET
        || type == Material.LEATHER_LEGGINGS) {

        val meta = itemMeta as LeatherArmorMeta
        meta.setColor(color)
        itemMeta = meta
        return this
    } else {
        throw IllegalArgumentException("Colors only applicable for leather armor!")
    }
}

fun ItemStack.flag(vararg flag: ItemFlag): ItemStack {
    val meta = itemMeta
    meta?.addItemFlags(*flag)
    itemMeta = meta
    return this
}

private fun List<String>.c(): List<String> {
    val tempStringList = ArrayList<String>()
    for (text in this) {
        tempStringList.add(text.translateColor())
    }
    return tempStringList
}