package net.guizhanss.rebarmobs.datatypes.cmd

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.guizhanss.guizhanlib.kt.rebar.utils.delegates.CustomModelDataType
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType

object EntityTypeDataType : CustomModelDataType<EntityType> {
    override fun fromString(value: String): EntityType {
        val nsKey =
            NamespacedKey.fromString(value)
                ?: throw IllegalArgumentException("String is not a valid NamespacedKey: $value")
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENTITY_TYPE).get(nsKey)
            ?: throw IllegalArgumentException("Invalid entity type: $nsKey")
    }

    override fun toString(value: EntityType) = value.key.toString()
}
