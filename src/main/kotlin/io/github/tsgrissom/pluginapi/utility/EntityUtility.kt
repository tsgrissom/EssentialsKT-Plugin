package io.github.tsgrissom.pluginapi.utility

import com.uchuhimo.collections.mutableBiMapOf
import org.bukkit.Location
import org.bukkit.entity.*

class EntityUtility {

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

    init {
        getMobTypes().forEach {
            mobTypeToNames[it] = generateMobKeys(it)
        }
    }

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