package net.guizhanss.rebarmobs.items.resources

import io.github.pylonmc.rebar.datatypes.RebarSerializers
import io.github.pylonmc.rebar.i18n.RebarArgument
import io.github.pylonmc.rebar.item.RebarItem
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import net.guizhanss.guizhanlib.kt.minecraft.extensions.isAir
import net.guizhanss.guizhanlib.kt.rebar.utils.persistentItemData
import net.guizhanss.rebarmobs.datatypes.RebarMobsDataTypes
import net.guizhanss.rebarmobs.utils.RebarMobsKeys
import net.guizhanss.rebarmobs.utils.refreshLore
import net.guizhanss.rebarmobs.utils.rmKey
import net.guizhanss.rebarmobs.utils.translatableKey
import net.kyori.adventure.text.Component
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

class SoulShard(
    item: ItemStack,
) : RebarItem(item) {
    override fun getPlaceholders() =
        listOf(
            RebarArgument.of(
                "mob-type",
                Component.translatable(
                    mobType?.translationKey() ?: translatableKey("no_mob_type"),
                ),
            ),
            RebarArgument.of(
                "tier",
                "TODO", // TODO: tier display
            ),
            RebarArgument.of("souls", soulAmount),
        )

    var mobType: EntityType? by persistentItemData(MOB_TYPE_KEY, RebarMobsDataTypes.ENTITY_TYPE) { null }
    var soulAmount: Int by persistentItemData(SOUL_AMOUNT_KEY, RebarSerializers.INTEGER) { 0 }

    companion object : Listener {
        val MOB_TYPE_KEY = rmKey("mob_type")
        val SOUL_AMOUNT_KEY = rmKey("soul_amount")

        private val soulStealerEnchant: Enchantment by lazy {
            RegistryAccess
                .registryAccess()
                .getRegistry(RegistryKey.ENCHANTMENT)
                .get(RebarMobsKeys.SOUL_STEALER) ?: error("Soul Stealer enchantment is not initialized!")
        }

        private fun getEnchantLevel(
            player: Player,
            enchant: Enchantment?,
        ): Int {
            if (enchant == null) return 0
            val mainHandLevel = player.inventory.itemInMainHand.getEnchantmentLevel(enchant)
            val offHandLevel = player.inventory.itemInOffHand.getEnchantmentLevel(enchant)
            return maxOf(mainHandLevel, offHandLevel)
        }

        private fun findApplicableShard(
            player: Player,
            targetType: EntityType,
        ): Pair<SoulShard, Int>? {
            val mainContents = player.inventory.contents
            var emptyShard: Pair<SoulShard, Int>? = null

            // check offhand item
            val offHandItem = player.inventory.itemInOffHand
            if (!offHandItem.isAir()) {
                val shard = fromStack(offHandItem) as? SoulShard
                if (shard != null) {
                    when (shard.mobType) {
                        targetType -> return shard to -1
                        null -> emptyShard = shard to -1
                        else -> Unit
                    }
                }
            }

            // check inventory
            for (slot in mainContents.indices) {
                val item = mainContents[slot]
                if (item.isAir()) continue

                val shard = fromStack(item) as? SoulShard ?: continue

                when (shard.mobType) {
                    targetType -> return shard to slot
                    null if emptyShard == null -> emptyShard = shard to slot
                    else -> Unit
                }
            }

            // now the empty shard will be used, set up its type now
            emptyShard?.first?.mobType = targetType
            return emptyShard
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        fun onEntityDeath(e: EntityDeathEvent) {
            val p = e.damageSource.causingEntity as? Player ?: return

            val extraSouls = getEnchantLevel(p, soulStealerEnchant)
            val applicableShard = findApplicableShard(p, e.entity.type) ?: return
            val shard = applicableShard.first
            shard.soulAmount += 1 + extraSouls
            shard.refreshLore(p.locale())
        }
    }
}
