package net.guizhanss.rebarmobs.datatypes

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType

object EntityTypePersistent : PersistentDataType<String, EntityType> {
    override fun getPrimitiveType(): Class<String> = String::class.java

    override fun getComplexType(): Class<EntityType> = EntityType::class.java

    override fun fromPrimitive(primitive: String, context: PersistentDataAdapterContext): EntityType {
        val nsKey = NamespacedKey.fromString(primitive)
            ?: throw IllegalArgumentException("String is not a valid NamespacedKey: $primitive")
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENTITY_TYPE).get(nsKey)
            ?: throw IllegalArgumentException("Invalid entity type: $nsKey")
    }

    override fun toPrimitive(complex: EntityType, context: PersistentDataAdapterContext): String {
        return complex.key.toString()
    }
}