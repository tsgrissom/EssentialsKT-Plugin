package io.github.tsgrissom.pluginapi.extension.bukkit

import io.github.tsgrissom.pluginapi.extension.kt.translateColor
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

// MARK: ItemStack Builder Functions

/**
 * Alters the ItemStack amount to the requisite integer.
 * @param amount The new amount of the ItemStack.
 * @return The instance of ItemStack.
 */
fun ItemStack.amount(amount: Int): ItemStack {
    setAmount(amount)
    return this
}

/**
 * Clears the Enchantments of the ItemStack.
 * @return The instance of ItemStack.
 */
fun ItemStack.clearEnchantments(): ItemStack {
    enchantments.keys.forEach(Consumer { this.removeEnchantment(it) })
    return this
}

/**
 * Clears the List of lore of the ItemStack.
 * @return The instance of ItemStack.
 */
fun ItemStack.clearLore(): ItemStack {
    val meta = itemMeta
    meta?.lore = ArrayList()
    itemMeta = meta
    return this
}

/**
 * Sets the leather armor meta color of the ItemStack. Only works on items with leather armor Materials.
 * @param color The Color to set the LeatherArmorMeta to.
 * @return The instance of ItemStack.
 */
fun ItemStack.color(color: Color): ItemStack {
    when (type) {
        Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET -> {
            val meta = itemMeta as LeatherArmorMeta
            meta.setColor(color)
            itemMeta = meta
            return this
        }
        else -> throw IllegalArgumentException("Colors only applicable for leather armor!")
    }
}

/**
 * Alters the data of the ItemStack to the requisite integer.
 * @param data The new data of the ItemStack.
 * @return The instance of ItemStack.
 */
fun ItemStack.data(data: Int): ItemStack {
    setData(MaterialData(type, data.toByte()))
    return this
}

/**
 * Alters the durability of the ItemStack to the requisite integer.
 * @param durability The new durability of the ItemStack.
 * @return The instance of ItemStack.
 */
fun ItemStack.durability(durability: Int): ItemStack {
    setDurability(durability.toShort())
    return this
}

/**
 * Adds an unsafe enchantment to the ItemStack.
 * @param enchantment The Enchantment to add.
 * @param level The new enchantment's level.
 * @return The instance of ItemStack.
 */
fun ItemStack.enchantment(enchantment: Enchantment, level: Int): ItemStack {
    addUnsafeEnchantment(enchantment, level)
    return this
}

/**
 * Adds the requisite Enchantment to the ItemStack at level 1.
 * @param enchantment The Enchantment to add.
 * @return The instance of ItemStack.
 */
fun ItemStack.enchantment(enchantment: Enchantment): ItemStack {
    addUnsafeEnchantment(enchantment, 1)
    return this
}

/**
 * Adds the requisite variable number of ItemFlags to the ItemStack.
 * @param flag The ItemFlags to add to the ItemStack.
 * @return The instance of ItemStack.
 */
fun ItemStack.flag(vararg flag: ItemFlag): ItemStack {
    val meta = itemMeta
    meta?.addItemFlags(*flag)
    itemMeta = meta
    return this
}

/**
 * Adds the requisite String to the lore of the ItemStack.
 * @param text The new line for the lore of the ItemStack.
 * @return The instance of ItemStack.
 */
fun ItemStack.lore(text: String): ItemStack {
    val meta = itemMeta
    var lore: MutableList<String>? = meta?.lore
    if (lore == null) {
        lore = ArrayList()
    }
    lore.add(text)
    meta?.lore = lore.translateColor()
    itemMeta = meta
    return this
}

/**
 * Adds the variable number of Strings to the lore of the ItemStack.
 * @param text The new lines for the lore of the ItemStack.
 * @return The instance of ItemStack.
 */
fun ItemStack.lore(vararg text: String): ItemStack {
    Arrays.stream(text).forEach { this.lore(it) }
    return this
}

/**
 * Adds the List of Strings to the lore of the ItemStack.
 * @param text The new lines for the lore of the ItemStack.
 * @return The instance of ItemStack.
 */
fun ItemStack.lore(text: List<String>): ItemStack {
    text.forEach { this.lore(it) }
    return this
}

/**
 * Alters the display name of the ItemStack to the requisite String.
 * @param name The new display name of the ItemStack.
 * @param withColor Whether the display name's alternate color codes should be translated. Default=true.
 * @return The instance of ItemStack.
 */
fun ItemStack.name(
    name: String,
    withColor: Boolean = true
) : ItemStack {
    val meta = itemMeta
    val new = if (withColor) name.translateColor() else name
    meta?.setDisplayName(new)
    this.itemMeta = meta
    return this
}

/**
 * Sets the owner profile of the ItemStack to the profile of the requisite Player. Only works for player head Materials.
 * @param player The Player whose head you wish to use with the ItemStack.
 * @return The instance of ItemStack.
 */
fun ItemStack.playerHeadOf(player: OfflinePlayer) : ItemStack =
    this.playerHeadOf(player.playerProfile)

/**
 * Sets the owner profile of the ItemStack to the requisite PlayerProfile. Only works for player head Materials.
 * @param profile The PlayerProfile of the Player whose head you wish to use with the ItemStack.
 * @return The instance of ItemStack.
 */
fun ItemStack.playerHeadOf(profile: PlayerProfile) : ItemStack {
    if (this.type != Material.PLAYER_HEAD && this.type != Material.PLAYER_WALL_HEAD)
        error("Cannot set skullOwner for non player head material \"${this.type}\"")

    val meta = this.itemMeta as SkullMeta
    meta.ownerProfile = profile
    this.itemMeta = meta
    return this
}

/**
 * Alters the Material type of the ItemStack.
 * @param material The Material to set the ItemStack to.
 * @return The instance of ItemStack.
 */
fun ItemStack.type(material: Material): ItemStack {
    type = material
    return this
}

// MARK: Material Extension Functions
inline val Material.isPickaxe: Boolean
    get() = this.name.endsWith("PICKAXE")
inline val Material.isSword: Boolean
    get() = this.name.endsWith("SWORD")
inline val Material.isAxe: Boolean
    get() = this.name.endsWith("_AXE")
inline val Material.isSpade: Boolean
    get() = this.name.endsWith("SPADE")
inline val Material.isHoe: Boolean
    get() = this.name.endsWith("HOE")
inline val Material.isOre: Boolean
    get() = this.name.endsWith("ORE")
inline val Material.isIngot: Boolean
    get() = this.name.endsWith("INGOT")
inline val Material.isDoor: Boolean
    get() = this.name.endsWith("_DOOR")
inline val Material.isTrapdoor: Boolean
    get() = this.name.endsWith("_TRAPDOOR")
inline val Material.isBoat: Boolean
    get() = this.name.endsWith("_BOAT")
inline val Material.isMinecart: Boolean
    get() = this.name.endsWith("MINECART")
inline val Material.isVehicle: Boolean
    get() = this.isBoat || this.isMinecart
inline val Material.isWater: Boolean
    get() = this == Material.WATER || this == Material.LEGACY_STATIONARY_WATER
inline val Material.isLava: Boolean
    get() = this == Material.LAVA || this == Material.LEGACY_STATIONARY_LAVA