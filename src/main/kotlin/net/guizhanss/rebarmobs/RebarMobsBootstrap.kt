package net.guizhanss.rebarmobs

import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.data.EnchantmentRegistryEntry
import io.papermc.paper.registry.event.RegistryEvents
import io.papermc.paper.registry.keys.EnchantmentKeys
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.tag.PostFlattenTagRegistrar
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlotGroup

@Suppress("UnstableApiUsage", "unused")
class RebarMobsBootstrap : PluginBootstrap {
    override fun bootstrap(context: BootstrapContext) {
        val soulStealerKey = NamespacedKey(PLUGIN_NAMESPACE, ENCHANTMENT_SOUL_STEALER)
        val soulStealerTypedKey = EnchantmentKeys.create(soulStealerKey)

        context.lifecycleManager.registerEventHandler(
            RegistryEvents.ENCHANTMENT.compose().newHandler { event ->
                event.registry().register(
                    soulStealerTypedKey,
                ) { builder ->
                    builder
                        .description(Component.translatable("$PLUGIN_NAMESPACE.enchantment.soul_stealer"))
                        .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_MELEE_WEAPON))
                        .primaryItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_MELEE_WEAPON))
                        .anvilCost(3)
                        .maxLevel(5)
                        .weight(10)
                        .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(0, 11))
                        .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(44, 0))
                        .activeSlots(EquipmentSlotGroup.MAINHAND)
                }
            },
        )

        context.lifecycleManager.registerEventHandler(
            LifecycleEvents.TAGS.postFlatten(RegistryKey.ENCHANTMENT),
        ) { event ->
            val registrar: PostFlattenTagRegistrar<Enchantment> = event.registrar()
            registrar.addToTag(
                EnchantmentTagKeys.IN_ENCHANTING_TABLE,
                setOf(soulStealerTypedKey),
            )
        }
    }

    companion object {
        private const val PLUGIN_NAMESPACE = "rebarmobs"
        private const val ENCHANTMENT_SOUL_STEALER = "soul_stealer"
    }
}
