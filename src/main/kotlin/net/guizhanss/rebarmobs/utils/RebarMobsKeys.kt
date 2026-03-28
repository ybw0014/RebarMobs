package net.guizhanss.rebarmobs.utils

import net.guizhanss.guizhanlib.common.utils.StringUtil
import net.guizhanss.rebarmobs.RebarMobs
import org.bukkit.NamespacedKey
import java.util.Locale

fun rmKey(key: String) = NamespacedKey(RebarMobs.instance(), StringUtil.dehumanize(key).lowercase(Locale.ENGLISH))

fun pylonKey(key: String) = NamespacedKey("pylon", StringUtil.dehumanize(key).lowercase(Locale.ENGLISH))

fun rmTranslatableKey(path: String) = "${RebarMobs.instance().key.namespace}.$path"

object RebarMobsKeys {
    // guide pages
    val REBAR_MOBS = rmKey("rebar_mobs")
    val RESOURCES_MAGIC = rmKey("resources_magic")
    val MULTIBLOCKS = rmKey("multiblocks")
    val BLOCKS = rmKey("blocks")

    // enchantments
    val SOUL_STEALER = rmKey("soul_stealer")

    // items
    val SOUL_SHARD = rmKey("soul_shard")
    val CORRUPTED_ESSENCE = rmKey("corrupted_essence")
    val VILE_DUST = rmKey("vile_dust")
    val CORRUPTED_INGOT = rmKey("corrupted_ingot")
    val VILE_SWORD_FRAME = rmKey("vile_sword_frame")
    val SOUL_ALTAR = rmKey("soul_altar") // also recipe key
    val SOUL_CAGE = rmKey("soul_cage")
    val QUARTZ_AND_STEEL = rmKey("quartz_and_steel")
    val VILE_SWORD = rmKey("vile_sword")

    // recipes
    val CURSING = rmKey("cursing")

    // misc
    val SOUL_CAGE_SPAWNED = rmKey("soul_cage_spawned") // tag added to spawned
    val VILE_SWORD_KILLED = rmKey("vile_sword_killed") // tag added to entity that is killed by vile sword
    val CURSED_FIRE = rmKey("cursed_fire")
    val CURSED_FIRE_IMMUNE_UNTIL = rmKey("cursed_fire_immune_until") // tag added to item entity after processing
}
