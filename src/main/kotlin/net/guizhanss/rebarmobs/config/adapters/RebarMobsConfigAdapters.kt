package net.guizhanss.rebarmobs.config.adapters

import io.github.pylonmc.rebar.config.adapter.ConfigAdapter
import org.bukkit.entity.EntityType

@Suppress("UnstableApiUsage")
object RebarMobsConfigAdapters {
    @JvmField
    val ENTITY_TYPE = ConfigAdapter { EntityType.fromName(ConfigAdapter.Companion.STRING.convert(it)) }
}
