package net.guizhanss.rebarmobs

import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.registry.data.EnchantmentRegistryEntry
import io.papermc.paper.registry.event.RegistryEvents
import io.papermc.paper.registry.keys.EnchantmentKeys
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.inventory.EquipmentSlotGroup

@Suppress("UnstableApiUsage", "unused")
class RebarMobsBootstrap : PluginBootstrap {
    override fun bootstrap(context: BootstrapContext) {
        context.lifecycleManager.registerEventHandler(
            RegistryEvents.ENCHANTMENT.compose().newHandler { event ->
                event.registry().register(
                    EnchantmentKeys.create(NamespacedKey(PLUGIN_NAMESPACE, "soul_stealer")),
                ) { builder ->
                    builder
                        .description(Component.translatable("$PLUGIN_NAMESPACE.enchantment.soul_stealer"))
                        .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.ENCHANTABLE_MELEE_WEAPON))
                        .anvilCost(3)
                        .maxLevel(5)
                        .weight(10)
                        .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(0, 11))
                        .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(44, 0))
                        .activeSlots(EquipmentSlotGroup.MAINHAND)
                }
            },
        )
    }

    companion object {
        private const val PLUGIN_NAMESPACE = "rebarmobs"
    }
}
