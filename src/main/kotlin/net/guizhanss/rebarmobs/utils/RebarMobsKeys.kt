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
    val BLOCKS = rmKey("blocks")

    // enchantments
    val SOUL_STEALER = rmKey("soul_stealer")

    // items
    val SOUL_SHARD = rmKey("soul_shard")
    val SOUL_ALTAR = rmKey("soul_altar")
}
