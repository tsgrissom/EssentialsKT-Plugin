package io.github.tsgrissom.pluginapi.utility

import com.uchuhimo.collections.mutableBiMapOf
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.*

class EntityUtility {

    private val entityTypeToMaterial = mutableMapOf<EntityType, Material>()
    private val mobTypeToNames = mutableBiMapOf<EntityType, Set<String>>()

    private fun getProtectedEntityTypes() : Set<EntityType> =
        setOf(
            EntityType.ARMOR_STAND,   EntityType.BLOCK_DISPLAY, EntityType.CHEST_BOAT,      EntityType.ENDER_CRYSTAL,
            EntityType.ENDER_SIGNAL,  EntityType.ENDER_DRAGON,  EntityType.GLOW_ITEM_FRAME, EntityType.INTERACTION,
            EntityType.ITEM_DISPLAY,  EntityType.ITEM_FRAME,    EntityType.LEASH_HITCH,     EntityType.MARKER,
            EntityType.PAINTING,      EntityType.PLAYER,        EntityType.TEXT_DISPLAY,    EntityType.THROWN_EXP_BOTTLE
        )

    private fun generateMobKeys(type: EntityType) : Set<String> {
        val set = mutableSetOf<String>()
        val name = type.name.lowercase()

        set.add(name)
        if (name.contains("_"))
            set.add(name.replace("_", ""))

        return set
    }

    private fun generateEntityTypeRepresentedAsMaterial(type: EntityType) : Material {
        val specialCases = mapOf(
            Pair(EntityType.ARMOR_STAND, Material.ARMOR_STAND),
            Pair(EntityType.ARROW, Material.ARROW),
            Pair(EntityType.BOAT, Material.OAK_BOAT),
            Pair(EntityType.CHEST_BOAT, Material.OAK_CHEST_BOAT),
            Pair(EntityType.EGG, Material.EGG),
            Pair(EntityType.ENDER_CRYSTAL, Material.END_CRYSTAL),
            Pair(EntityType.ENDER_PEARL, Material.ENDER_PEARL),
            Pair(EntityType.FIREWORK, Material.FIREWORK_ROCKET),
            Pair(EntityType.FISHING_HOOK, Material.FISHING_ROD),
            Pair(EntityType.ITEM_FRAME, Material.ITEM_FRAME),
            Pair(EntityType.GLOW_ITEM_FRAME, Material.GLOW_ITEM_FRAME),
            Pair(EntityType.MINECART, Material.MINECART),
            Pair(EntityType.MINECART_CHEST, Material.CHEST_MINECART),
            Pair(EntityType.MINECART_COMMAND, Material.COMMAND_BLOCK_MINECART),
            Pair(EntityType.MINECART_FURNACE, Material.FURNACE_MINECART),
            Pair(EntityType.MINECART_HOPPER, Material.HOPPER_MINECART),
            Pair(EntityType.MINECART_TNT, Material.TNT_MINECART),
            Pair(EntityType.PAINTING, Material.PAINTING),
            Pair(EntityType.PRIMED_TNT, Material.TNT),
            Pair(EntityType.GIANT, Material.ZOMBIE_HEAD),
            Pair(EntityType.MUSHROOM_COW, Material.MOOSHROOM_SPAWN_EGG)
        )

        if (specialCases.containsKey(type))
            return specialCases[type] ?: Material.BARRIER

        val asHead: Material? = Material.entries.firstOrNull() { it.name == "${type.name}_HEAD" }
        val asSkull: Material? = Material.entries.firstOrNull() { it.name == "${type.name}_SKULL" }
        val asSpawnEgg: Material? = Material.entries.firstOrNull { it.name == "${type.name}_SPAWN_EGG"}

        if (asSpawnEgg != null)
            return asSpawnEgg

        if (asHead != null)
            return asHead

        if (asSkull != null)
            return asSkull

        return Material.BARRIER
    }

    init {
        getMobTypes().forEach {
            mobTypeToNames[it] = generateMobKeys(it)
        }
        EntityType.entries.forEach {
            entityTypeToMaterial[it] = generateEntityTypeRepresentedAsMaterial(it)
        }
    }

    fun getMaterialRepresentationForType(type: EntityType) : Material =
        entityTypeToMaterial[type] ?: Material.BARRIER

    fun getMobKeys(type: EntityType) : Set<String> =
        mobTypeToNames[type] ?: error("The given type is not a mob")

    fun getAllMobKeys() : Set<String> {
        val set = mutableSetOf<String>()

        mobTypeToNames.values.forEach {
            set.addAll(it)
        }

        return set
    }

    fun getMobTypeFromKey(key: String) : EntityType? {
        for (entry in mobTypeToNames) {
            val keys: Set<String> = entry.value
            if (keys.contains(key.lowercase()))
                return entry.key
        }
        return null
    }

    fun getMobTypes() =
        EntityType.entries
            .filter { it.isAlive && it.isSpawnable }
            .filter { it != EntityType.ARMOR_STAND }
            .toSet()
    fun getNonAliveTypes() = EntityType.entries.filter { !it.isAlive }.toSet()
    fun getNonSpawnableTypes() = EntityType.entries.filter { !it.isSpawnable }.toSet()
    fun getNonMobTypes() = EntityType.entries.filter { !it.isAlive || !it.isSpawnable }.toSet()

    fun getLocationalNearbyEntities(from: Location, radius: Double) : List<Entity> {
        val w = from.world ?: error("World was null from Location")
        var allEntities: List<Entity> = w.entities

        if (radius > 0)
            allEntities = allEntities
                .filter { it.location.distance(from) <= radius }
                .toList()

        return allEntities
    }

    fun getAllNearby(from: Location, radius: Double) : List<Entity> =
        getLocationalNearbyEntities(from, radius).filter { !getProtectedEntityTypes().contains(it.type) }
    fun getMonstersNearby(from: Location, radius: Double) : List<Entity> =
        getLocationalNearbyEntities(from, radius).filterIsInstance<Monster>()
    fun getAnimalsNearby(from: Location, radius: Double) : List<Entity> =
        getLocationalNearbyEntities(from, radius).filterIsInstance<Animals>()
    fun getAmbientNearby(from: Location, radius: Double) : List<Entity> =
        getLocationalNearbyEntities(from, radius).filterIsInstance<Ambient>()
    fun getMobsNearby(from: Location, radius: Double) : List<Entity> =
        getLocationalNearbyEntities(from, radius).filterIsInstance<Mob>()
    fun getTamedNearby(from: Location, radius: Double) : List<Entity> =
        getLocationalNearbyEntities(from, radius).filterIsInstance<Tameable>()
    fun getNamedNearby(from: Location, radius: Double) : List<Entity> =
        getLocationalNearbyEntities(from, radius).filter { it.customName != null }
    fun getDroppedItemsNearby(from: Location, radius: Double) : List<Entity> =
        getLocationalNearbyEntities(from, radius).filter { it.type == EntityType.DROPPED_ITEM }
    fun getBoatsNearby(from: Location, radius: Double) : List<Entity> {
        return getLocationalNearbyEntities(from, radius).filter {
            it.type == EntityType.BOAT || it.type == EntityType.CHEST_BOAT
        }
    }
    fun getMinecartsNearby(from: Location, radius: Double) : List<Entity> =
        getLocationalNearbyEntities(from, radius).filter { it.type.name.contains("MINECART") }
    fun getExperienceOrbsNearby(from: Location, radius: Double) : List<Entity> =
        getLocationalNearbyEntities(from, radius).filter { it.type==EntityType.EXPERIENCE_ORB }
    fun getPaintingsNearby(from: Location, radius: Double) : List<Entity> =
        getLocationalNearbyEntities(from, radius).filter { it.type==EntityType.PAINTING }
    fun getItemFramesNearby(from: Location, radius: Double) : List<Entity> =
        getLocationalNearbyEntities(from, radius).filter {
            it.type==EntityType.ITEM_FRAME || it.type==EntityType.GLOW_ITEM_FRAME
        }
    fun getEnderCrystalsNearby(from: Location, radius: Double) : List<Entity> =
        getLocationalNearbyEntities(from, radius).filter { it.type==EntityType.ENDER_CRYSTAL }
}